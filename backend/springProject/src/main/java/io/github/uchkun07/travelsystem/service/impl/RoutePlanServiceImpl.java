package io.github.uchkun07.travelsystem.service.impl;

import io.github.uchkun07.travelsystem.config.RouteAlgorithmProperties;
import io.github.uchkun07.travelsystem.dto.RoutePlanRequest;
import io.github.uchkun07.travelsystem.dto.RoutePlanResult;
import io.github.uchkun07.travelsystem.entity.Attraction;
import io.github.uchkun07.travelsystem.mapper.AttractionMapper;
import io.github.uchkun07.travelsystem.service.IRoutePlanService;
import io.github.uchkun07.travelsystem.util.GeocodingUtil;
import io.github.uchkun07.travelsystem.util.RoutePathUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 路线规划服务实现
 * <p>流程：
 * 1. 批量地理编码（出发地 + 各景点地址）
 * 2. 计算 n×n 路径成本矩阵（高德 API + Redis 缓存）
 * 3. 在 Java 内执行模拟退火，求解最优游览顺序
 * 4. 补充景点信息/天气/人流量，封装返回结果
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoutePlanServiceImpl implements IRoutePlanService {

    private final AttractionMapper attractionMapper;
    private final GeocodingUtil geocodingUtil;
    private final RoutePathUtil routePathUtil;
    private final RouteAlgorithmProperties algorithmProps;

    // ── 模拟天气/人流量数据（预留接口，后续可对接真实 API） ──────────────────
    private static final String[] WEATHER_POOL = {
            "晴，22℃", "多云，18℃", "阴，15℃", "小雨，12℃",
            "晴，28℃", "风，20℃", "晴转多云，24℃"
    };
    private static final String[] CROWD_POOL = {"低", "中", "中", "高"};

    @Override
    public RoutePlanResult plan(RoutePlanRequest request) {
        List<Long> attractionIds = request.getAttractionIds();

        // ── 1. 查询景点信息 ────────────────────────────────────────────────────
        List<Attraction> attractions = attractionMapper.selectBatchIds(attractionIds);
        if (attractions.isEmpty()) {
            throw new RuntimeException("未找到任何景点信息");
        }

        // ── 2. 地理编码 ────────────────────────────────────────────────────────
        // index 0 = 出发地，index 1..n = 景点
        List<String> addresses = new ArrayList<>();
        addresses.add(request.getDeparture());
        for (Attraction a : attractions) {
            String addr = (a.getAddress() != null && !a.getAddress().isBlank())
                    ? a.getAddress() : a.getName();
            addresses.add(addr);
        }

        List<double[]> coords = new ArrayList<>();
        for (String addr : addresses) {
            // 优先使用景点表中已有的 lat/lng，避免多余 API 调用
            coords.add(geocodingUtil.geocode(addr));
        }
        // 用景点表中记录的实际坐标覆盖编码结果（避免偏差）
        for (int i = 0; i < attractions.size(); i++) {
            Attraction a = attractions.get(i);
            if (a.getLatitude() != null && a.getLongitude() != null) {
                coords.set(i + 1, new double[]{
                        a.getLongitude().doubleValue(),
                        a.getLatitude().doubleValue()
                });
            }
        }

        // ── 3. 构建路径成本矩阵 ────────────────────────────────────────────────
        // nodes[0]=出发地, nodes[1..n]=景点
        int n = coords.size();
        double[][] distMatrix = new double[n][n];   // 距离 km
        double[][] timeMatrix = new double[n][n];   // 耗时 min
        double[][] costMatrix = new double[n][n];   // 通行费 元

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) continue;
                RoutePathUtil.PathCost pc = routePathUtil.calcCost(
                        coords.get(i), coords.get(j), request.getTravelMode());
                distMatrix[i][j] = pc.distanceKm();
                timeMatrix[i][j] = pc.durationMinutes();
                costMatrix[i][j] = pc.cost();
            }
        }

        // ── 4. 确定时间权重（根据出行偏好） ────────────────────────────────────
        double timeWeight = switch (request.getTravelPreference()) {
            case "经济" -> algorithmProps.getPrefEconomyTimeWeight();
            case "舒适" -> algorithmProps.getPrefComfortTimeWeight();
            default      -> algorithmProps.getPrefModerateTimeWeight();
        };

        // ── 5. Java 模拟退火求解最优顺序 ────────────────────────────────────────
        int[] orderedIndexes = runSimulatedAnnealing(n, timeMatrix, costMatrix, timeWeight);

        // ── 6. 组装结果 ────────────────────────────────────────────────────────
        return buildResult(request, attractions, coords, distMatrix, timeMatrix,
                costMatrix, orderedIndexes);
    }

    // ────────────────────── Java 模拟退火实现 ────────────────────────────────

    private int[] runSimulatedAnnealing(int nodeCount,
                                        double[][] timeMatrix,
                                        double[][] costMatrix,
                                        double timeWeight) {
        if (nodeCount <= 1) {
            return new int[0];
        }

        int[] fallback = fallbackOrder(nodeCount);
        if (nodeCount == 2) {
            return fallback;
        }

        List<Integer> currentOrder = new ArrayList<>();
        for (int i = 1; i < nodeCount; i++) {
            currentOrder.add(i);
        }
        Collections.shuffle(currentOrder);

        double currentCost = calcRouteCost(currentOrder, timeMatrix, costMatrix, timeWeight);
        List<Integer> bestOrder = new ArrayList<>(currentOrder);
        double bestCost = currentCost;

        double temp = normalizeInitialTemp(algorithmProps.getInitialTemp(), algorithmProps.getMinTemp());
        double minTemp = normalizeMinTemp(algorithmProps.getMinTemp());
        double coolingRate = normalizeCoolingRate(algorithmProps.getCoolingRate());
        int iterationsPerTemp = Math.max(algorithmProps.getIterationsPerTemp(), 1);

        Random random = new Random();
        while (temp > minTemp) {
            for (int i = 0; i < iterationsPerTemp; i++) {
                List<Integer> neighbor = randomNeighbor(currentOrder, random);
                double neighborCost = calcRouteCost(neighbor, timeMatrix, costMatrix, timeWeight);
                double delta = neighborCost - currentCost;

                if (delta < 0 || random.nextDouble() < Math.exp(-delta / temp)) {
                    currentOrder = neighbor;
                    currentCost = neighborCost;
                    if (currentCost < bestCost) {
                        bestCost = currentCost;
                        bestOrder = new ArrayList<>(currentOrder);
                    }
                }
            }
            temp *= coolingRate;
        }

        return bestOrder.stream().mapToInt(Integer::intValue).toArray();
    }

    private double calcRouteCost(List<Integer> order,
                                 double[][] timeMatrix,
                                 double[][] costMatrix,
                                 double timeWeight) {
        double costWeight = 1.0 - timeWeight;
        double total = 0.0;
        int prev = 0;
        for (int node : order) {
            total += timeWeight * timeMatrix[prev][node] + costWeight * costMatrix[prev][node];
            prev = node;
        }
        return total;
    }

    private List<Integer> randomNeighbor(List<Integer> order, Random random) {
        if (order.size() < 2) {
            return new ArrayList<>(order);
        }

        if (random.nextDouble() < 0.5) {
            List<Integer> copy = new ArrayList<>(order);
            int i = random.nextInt(copy.size());
            int j = random.nextInt(copy.size());
            while (j == i) {
                j = random.nextInt(copy.size());
            }
            Collections.swap(copy, i, j);
            return copy;
        }

        int i = random.nextInt(order.size());
        int j = random.nextInt(order.size());
        if (i > j) {
            int t = i;
            i = j;
            j = t;
        }

        List<Integer> copy = new ArrayList<>(order);
        while (i < j) {
            Collections.swap(copy, i, j);
            i++;
            j--;
        }
        return copy;
    }

    private double normalizeInitialTemp(double initialTemp, double minTemp) {
        if (initialTemp <= minTemp) {
            return Math.max(minTemp + 1.0, 10.0);
        }
        return initialTemp;
    }

    private double normalizeMinTemp(double minTemp) {
        return minTemp <= 0 ? 1.0 : minTemp;
    }

    private double normalizeCoolingRate(double coolingRate) {
        if (coolingRate <= 0 || coolingRate >= 1) {
            return 0.995;
        }
        return coolingRate;
    }

    /**
     * 降级策略：按顺序 1,2,3,...,n-1（不经过 index 0 的出发地，出发地在外层处理）
     */
    private int[] fallbackOrder(int nodeCount) {
        int[] order = new int[nodeCount - 1];
        for (int i = 0; i < order.length; i++) order[i] = i + 1;
        return order;
    }

    // ────────────────────── 组装返回结果 ─────────────────────────────────────

    private RoutePlanResult buildResult(
            RoutePlanRequest request,
            List<Attraction> attractions,
            List<double[]> coords,
            double[][] distMatrix,
            double[][] timeMatrix,
            double[][] costMatrix,
            int[] orderedIndexes) {

        Random rnd = new Random();
        LocalDate currentDate = LocalDate.parse(request.getDepartureDate(),
                DateTimeFormatter.ISO_LOCAL_DATE);

        List<RoutePlanResult.RouteStep> steps = new ArrayList<>();
        double totalCost = 0;
        long totalDuration = 0;
        double totalDist = 0;

        int prevIndex = 0; // 从出发地（index 0）出发
        for (int stepNum = 1; stepNum <= orderedIndexes.length; stepNum++) {
            int curIndex = orderedIndexes[stepNum - 1];
            Attraction a = attractions.get(curIndex - 1); // attractions 从 0 开始

            double travelCost  = costMatrix[prevIndex][curIndex];
            long   travelMin   = (long) timeMatrix[prevIndex][curIndex];
            double travelDist  = distMatrix[prevIndex][curIndex];
            double ticketPrice = a.getTicketPrice() != null ? a.getTicketPrice().doubleValue() : 0;

            totalCost     += travelCost + ticketPrice;
            totalDuration += travelMin;
            totalDist     += travelDist;

            // 每个景点游览结束后推进日期（简单按 estimatedPlayTime 估算，超过 8h 换天）
            int playMin = a.getEstimatedPlayTime() != null ? a.getEstimatedPlayTime() : 120;

            steps.add(RoutePlanResult.RouteStep.builder()
                    .order(stepNum)
                    .attractionId(a.getAttractionId())
                    .name(a.getName())
                    .address(a.getAddress() != null ? a.getAddress() : "")
                    .latitude(a.getLatitude() != null ? a.getLatitude().doubleValue() : coords.get(curIndex)[1])
                    .longitude(a.getLongitude() != null ? a.getLongitude().doubleValue() : coords.get(curIndex)[0])
                    .imageUrl(a.getMainImageUrl() != null ? a.getMainImageUrl() : "")
                    .ticketPrice(ticketPrice)
                    .averageRating(a.getAverageRating() != null ? a.getAverageRating().doubleValue() : 0)
                    .arrivalDate(currentDate.format(DateTimeFormatter.ISO_LOCAL_DATE))
                    .travelDurationMinutes(travelMin)
                    .travelDistanceKm(Math.round(travelDist * 10.0) / 10.0)
                    .travelCost(Math.round(travelCost * 10.0) / 10.0)
                    .estimatedPlayMinutes(playMin)
                    // 模拟天气/人流量（预留真实 API 替换点）
                    .weather(WEATHER_POOL[rnd.nextInt(WEATHER_POOL.length)])
                    .crowdLevel(CROWD_POOL[rnd.nextInt(CROWD_POOL.length)])
                    .build());

            // 累计游览时间超 8h 则跳到第二天
            if ((travelMin + playMin) > 480) currentDate = currentDate.plusDays(1);
            prevIndex = curIndex;
        }

        boolean overBudget = totalCost > request.getBudget();
        String tips = overBudget
                ? String.format("⚠ 预计总费用 ¥%.0f 已超出预算 ¥%.0f，建议减少景点或选择经济出行方式",
                totalCost, request.getBudget())
                : "路线规划完成，祝旅途愉快！";

        return RoutePlanResult.builder()
                .summary(RoutePlanResult.Summary.builder()
                        .totalCost(Math.round(totalCost * 10.0) / 10.0)
                        .totalDurationMinutes(totalDuration)
                        .totalDistanceKm(Math.round(totalDist * 10.0) / 10.0)
                        .preference(request.getTravelPreference())
                        .travelMode(request.getTravelMode())
                        .build())
                .steps(steps)
                .overBudget(overBudget)
                .tips(tips)
                .build();
    }
}
