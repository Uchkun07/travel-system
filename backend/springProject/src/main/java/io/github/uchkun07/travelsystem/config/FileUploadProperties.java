package io.github.uchkun07.travelsystem.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 文件上传配置属性
 */
@Configuration
@ConfigurationProperties(prefix = "file.upload")
public class FileUploadProperties {
    
    /**
     * 文件上传路径
     */
    private String path = "public/img/avatars";

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
