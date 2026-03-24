package io.github.uchkun07.travelsystem.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 高德地图 API 配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "amap")
public class AMapProperties {

    /** 高德开放平台 Web 服务 Key */
    private String key;

    /** 地理编码接口地址 */
    private String geocodeUrl;

    /** 驾车路径规划接口地址（高德 v5） */
    private String drivingUrl;

    /** 公交/火车路径规划接口地址（高德 v5） */
    private String transitUrl;

    /** HTTP 请求超时时间（毫秒） */
    private int timeout = 8000;
}
