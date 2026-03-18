package io.github.uchkun07.travelsystem.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.uchkun07.travelsystem.config.AMapProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * 高德路径规划工具（API v5）
 * 支持：自驾（driving）/ 公交&火车 / 飞机（模拟）
 * 结果集成 Redis 缓存，缓存 TTL 24 小时。
 *
 * <p>返回对象 {@link PathCost}：距离（km）、耗时（分钟）、通行费（元）</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RoutePathUtil {

    private final AMapProperties amapProperties;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final long CACHE_TTL_HOURS = 24;
    private static final String CACHE_PREFIX = "route:path:";

    /**
     * 路径成本
     */
    public record PathCost(double distanceKm, long durationMinutes, double cost) {
        /** 降级模拟成本（直线距离估算） */
        static PathCost mock(double[] from, double[] to, String mode) {
            double dist = haversineKm(from[1], from[0], to[1], to[0]);
            return switch (mode) {
                case "飞机" -> new PathCost(dist, (long) (dist / 700 * 60 + 60),
                        dist * 0.5 + 200);         // 模拟机票
                case "火车" -> new PathCost(dist, (long) (dist / 120 * 60),
                        dist * 0.35);               // 模拟车票
                default ->    new PathCost(dist, (long) (dist / 60 * 60),
                        dist * 0.7 + dist / 100 * 15); // 油费+过路费
            };
        }

        /** 哈弗辛公式计算两点直线距离（单位：km） */
        static double haversineKm(double lat1, double lon1, double lat2, double lon2) {
            final double R = 6371.0;
            double dLat = Math.toRadians(lat2 - lat1);
            double dLon = Math.toRadians(lon2 - lon1);
            double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                    + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                    * Math.sin(dLon / 2) * Math.sin(dLon / 2);
            return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        }
    }

    /**
     * 计算两点间的路径成本，优先从 Redis 缓存读取。
     *
     * @param from   起点经纬度 [经度, 纬度]
     * @param to     终点经纬度 [经度, 纬度]
     * @param mode   出行方式（自驾 / 火车 / 飞机）
     * @return PathCost（失败时降级返回模拟值）
     */
    public PathCost calcCost(double[] from, double[] to, String mode) {
        String cacheKey = buildCacheKey(from, to, mode);

        // 1. 读缓存
        String cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            try {
                return objectMapper.readValue(cached, PathCost.class);
            } catch (Exception e) {
                log.warn("Redis 路径缓存解析失败，重新请求", e);
            }
        }

        // 2. 飞机模式高德无 API，直接模拟
        if ("飞机".equals(mode)) {
            PathCost cost = PathCost.mock(from, to, mode);
            writeCache(cacheKey, cost);
            return cost;
        }

        // 3. 调用高德 API
        try {
            PathCost cost = "火车".equals(mode)
                    ? callTransitApi(from, to)
                    : callDrivingApi(from, to);
            writeCache(cacheKey, cost);
            return cost;
        } catch (Exception e) {
            log.warn("高德路径规划请求失败，降级模拟，原因={}", e.getMessage());
            return PathCost.mock(from, to, mode);
        }
    }

    // ────────────────────── 私有方法 ─────────────────────────────────────────

    /** 调用高德驾车路径规划 v5 */
    private PathCost callDrivingApi(double[] from, double[] to)
            throws IOException, InterruptedException {
        String url = amapProperties.getDrivingUrl()
                + "?key=" + amapProperties.getKey()
                + "&origin=" + from[0] + "," + from[1]
                + "&destination=" + to[0] + "," + to[1]
                + "&show_fields=cost"
                + "&output=JSON";

        String body = httpGet(url);
        JsonNode root = objectMapper.readTree(body);

        if (!"1".equals(root.path("status").asText())) {
            throw new IOException("高德 API 返回异常: " + root.path("infocode").asText());
        }

        JsonNode path = root.path("route").path("paths").get(0);
        double distKm = path.path("distance").asDouble() / 1000.0;
        long durationMin = path.path("duration").asLong(0) / 60;
        // 路费 = 过路费 + 油费（高德 v5 cost 字段）
        double tolls = path.path("cost").path("tolls").asDouble();
        double taxiFee = path.path("cost").path("taxi_cost").asDouble();
        double cost = tolls + taxiFee * 0.8; // taxi_cost 是出租车费，换算油费约 0.8 倍
        if (cost == 0) cost = distKm * 0.7;  // 兜底按 0.7 元/km

        return new PathCost(distKm, durationMin, cost);
    }

    /** 调用高德公交路径规划 v5（火车近似） */
    private PathCost callTransitApi(double[] from, double[] to)
            throws IOException, InterruptedException {
        String url = amapProperties.getTransitUrl()
                + "?key=" + amapProperties.getKey()
                + "&origin=" + from[0] + "," + from[1]
                + "&destination=" + to[0] + "," + to[1]
                + "&show_fields=cost"
                + "&output=JSON";

        String body = httpGet(url);
        JsonNode root = objectMapper.readTree(body);

        if (!"1".equals(root.path("status").asText())) {
            throw new IOException("高德公交 API 返回异常: " + root.path("infocode").asText());
        }

        JsonNode transit = root.path("route").path("transits").get(0);
        double distKm = root.path("route").path("distance").asDouble() / 1000.0;
        long durationMin = transit.path("duration").asLong() / 60;
        double cost = transit.path("cost").path("transit_fee").asDouble();
        if (cost == 0) cost = distKm * 0.35;

        return new PathCost(distKm, durationMin, cost);
    }

    private String httpGet(String url) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(amapProperties.getTimeout()))
                .build();
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofMillis(amapProperties.getTimeout()))
                .GET().build();
        return client.send(req, HttpResponse.BodyHandlers.ofString()).body();
    }

    private String buildCacheKey(double[] from, double[] to, String mode) {
        return CACHE_PREFIX + String.format("%.4f,%.4f:%.4f,%.4f:%s",
                from[0], from[1], to[0], to[1], mode);
    }

    private void writeCache(String key, PathCost cost) {
        try {
            redisTemplate.opsForValue().set(
                    key,
                    objectMapper.writeValueAsString(cost),
                    CACHE_TTL_HOURS, TimeUnit.HOURS
            );
        } catch (Exception e) {
            log.warn("路径成本写入 Redis 缓存失败", e);
        }
    }
}
