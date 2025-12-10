package io.github.uchkun07.travelsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 头像上传响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvatarUploadResponse {

    /**
     * 头像URL
     */
    private String avatarUrl;

    /**
     * 新的JWT令牌（包含更新后的头像信息）
     */
    private String token;
}
