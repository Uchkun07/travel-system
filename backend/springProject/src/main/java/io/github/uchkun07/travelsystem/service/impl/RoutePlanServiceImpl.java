package io.github.uchkun07.travelsystem.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 路线规划服务实现
 * <p>流程：
 * 1. 批量地理编码（出发地 + 各景点地址）
 * 2. 计算 n×n 路径成本矩阵（高德 API + Redis 缓存）
 * 3. 序列化矩阵为 JSON，通过 ProcessBuilder 调用 Python 模拟退火脚本
 * 4. 解析 Python 输出，补充景点信息/天气/人流量，封装返回结果
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
    private final ObjectMapper objectMapper;

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

        // ── 5. 构建 Python 输入 JSON ────────────────────────────────────────────
        Map<String, Object> pyInput = new LinkedHashMap<>();
        pyInput.put("nodeCount", n);
        pyInput.put("distMatrix", distMatrix);
        pyInput.put("timeMatrix", timeMatrix);
        pyInput.put("costMatrix", costMatrix);
        pyInput.put("timeWeight", timeWeight);
        pyInput.put("initialTemp", algorithmProps.getInitialTemp());
        pyInput.put("coolingRate", algorithmProps.getCoolingRate());
        pyInput.put("minTemp", algorithmProps.getMinTemp());
        pyInput.put("iterationsPerTemp", algorithmProps.getIterationsPerTemp());

        // ── 6. 调用 Python 模拟退火算法 ─────────────────────────────────────────
        int[] orderedIndexes = callPythonSA(pyInput);

        // ── 7. 组装结果 ────────────────────────────────────────────────────────
        return buildResult(request, attractions, coords, distMatrix, timeMatrix,
                costMatrix, orderedIndexes);
    }

    // ────────────────────── 调用 Python 子进程 ────────────────────────────────

    private int[] callPythonSA(Map<String, Object> pyInput) {
        String scriptPath = resolveScriptPath();
        try {
            String inputJson = objectMapper.writeValueAsString(pyInput);
            int nodeCount = (int) pyInput.get("nodeCount");

            ProcessBuilder pb = new ProcessBuilder("python", scriptPath);
            pb.redirectErrorStream(false);
            Process process = pb.start();

            // 写入 JSON 到子进程 stdin
            try (OutputStream os = process.getOutputStream()) {
                os.write(inputJson.getBytes(StandardCharsets.UTF_8));
            }

            // 读取 stdout
            String stdout;
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
                stdout = sb.toString();
            }

            // 读取 stderr（用于日志）
            try (BufferedReader errReader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8))) {
                String errLine;
                while ((errLine = errReader.readLine()) != null) {
                    log.debug("[Python SA] {}", errLine);
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0 || stdout.isBlank()) {
                log.warn("Python SA 脚本异常（exitCode={}），降级使用贪心顺序", exitCode);
                return fallbackOrder(nodeCount);
            }

            // Python 返回格式：{"order": [0,2,1,3,...]}
            int[] order = objectMapper.readTree(stdout).path("order")
                    .traverse(objectMapper)
                    .readValueAs(int[].class);
            return order;

        } catch (Exception e) {
            log.warn("调用 Python SA 失败：{}，降级使用贪心顺序", e.getMessage());
            return fallbackOrder((int) pyInput.get("nodeCount"));
        }
    }

    /** 脚本路径：用户配置的绝对路径，或相对工作目录的默认路径 */
    private String resolveScriptPath() {
        String configured = algorithmProps.getScriptPath();
        if (configured != null && !configured.isBlank()) return configured;
        // 默认路径：后端工作目录向上两级找 backend/python/
        return Paths.get(System.getProperty("user.dir"))
                .getParent().resolve("python/routePlanningAlgorithm.py")
                .toAbsolutePath().toString();
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
