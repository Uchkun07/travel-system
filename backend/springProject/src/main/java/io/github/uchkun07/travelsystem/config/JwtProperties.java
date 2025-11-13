package io.github.uchkun07.travelsystem.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT配置属性类
 * 用于绑定application.yml中的jwt配置
 */
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * JWT密钥
     */
    private String secret = "travel-system-secret-key-for-jwt-token-generation-and-validation-2024";

    /**
     * JWT过期时间(毫秒) - 默认7天
     */
    private Long expiration = 604800000L;

    /**
     * 短期JWT过期时间(毫秒) - 默认1天
     */
    private Long expirationShort = 86400000L;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Long getExpiration() {
        return expiration;
    }

    public void setExpiration(Long expiration) {
        this.expiration = expiration;
    }

    public Long getExpirationShort() {
        return expirationShort;
    }

    public void setExpirationShort(Long expirationShort) {
        this.expirationShort = expirationShort;
    }
}
