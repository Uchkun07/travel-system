package io.github.uchkun07.travelsystem.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * DeepSeek API 配置类
 */
@Data
@Component
@ConfigurationProperties(prefix = "deepseek.api")
public class DeepSeekConfig {

    /**
     * DeepSeek API URL
     */
    private String url;

    /**
     * DeepSeek API Key
     */
    private String key;

    /**
     * DeepSeek 模型名称
     */
    private String model;

    /**
     * 请求超时时间（秒）
     */
    private Integer timeout;
}
