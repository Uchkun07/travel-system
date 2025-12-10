package io.github.uchkun07.travelsystem.config;

import lombok.Data;
import org.springframework.context.annotation.Configuration;

/**
 * 文件上传配置属性
 */
@Data
@Configuration
public class FileUploadProperties {
    
    /**
     * 文件上传路径
     */
    private String path = "public/img/avatars";

    /**
     * 头像上传目录
     */
    private String avatarDir = "src/main/resources/static/avatars";

    /**
     * 最大文件大小（字节）
     */
    private Long maxSize = 5242880L; // 5MB
}
