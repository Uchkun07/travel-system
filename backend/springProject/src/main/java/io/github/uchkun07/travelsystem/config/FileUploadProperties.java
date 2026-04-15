package io.github.uchkun07.travelsystem.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 文件上传配置属性
 */
@Data
@ConfigurationProperties(prefix = "file.upload")
public class FileUploadProperties {

    /**
     * 文件上传根目录
     */
    private String baseDir = "/opt/travel-system/uploads";

    /**
     * 头像上传目录
     */
    private String avatarDir = "/opt/travel-system/uploads/avatars";

    /**
     * 最大文件大小（字节）
     */
    private Long maxSize = 5242880L; // 5MB
}
