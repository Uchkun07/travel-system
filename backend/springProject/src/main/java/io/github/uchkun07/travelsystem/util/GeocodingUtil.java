package io.github.uchkun07.travelsystem.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.uchkun07.travelsystem.config.AMapProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * 高德地理编码工具
 * 将中文地址转换为经纬度坐标（GCJ-02 坐标系），失败时降级返回模拟坐标。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GeocodingUtil {

    private final AMapProperties amapProperties;
    private final ObjectMapper objectMapper;

    /**
     * 地址 → 经纬度
     *
     * @param address 中文地址，如"北京市朝阳区"
     * @return double[]{经度, 纬度}；异常时返回 [116.4074, 39.9042]（北京近似坐标）
     */
    public double[] geocode(String address) {
        try {
            String encodedAddr = URLEncoder.encode(address, StandardCharsets.UTF_8);
            String url = amapProperties.getGeocodeUrl()
                    + "?key=" + amapProperties.getKey()
                    + "&address=" + encodedAddr
                    + "&output=JSON";

            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofMillis(amapProperties.getTimeout()))
                    .build();
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofMillis(amapProperties.getTimeout()))
                    .GET()
                    .build();

            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            JsonNode root = objectMapper.readTree(resp.body());

            // 高德地理编码返回格式：{"status":"1","geocodes":[{"location":"116.397499,39.908722",...}]}
            if ("1".equals(root.path("status").asText())) {
                JsonNode geocodes = root.path("geocodes");
                if (geocodes.isArray() && !geocodes.isEmpty()) {
                    String location = geocodes.get(0).path("location").asText();
                    String[] parts = location.split(",");
                    return new double[]{
                            Double.parseDouble(parts[0]),  // 经度
                            Double.parseDouble(parts[1])   // 纬度
                    };
                }
            }
            log.warn("高德地理编码未找到结果，address={}, 降级使用默认坐标", address);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("高德地理编码请求被中断，address={}, 原因={}，降级使用默认坐标", address, e.getMessage());
        } catch (IOException e) {
            log.warn("高德地理编码请求失败，address={}, 原因={}，降级使用默认坐标", address, e.getMessage());
        } catch (Exception e) {
            log.warn("高德地理编码解析失败，address={}，降级使用默认坐标", address, e);
        }
        // 降级：返回北京近似坐标（代码层不崩溃，业务层继续）
        return new double[]{116.4074, 39.9042};
    }
}
