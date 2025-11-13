package io.github.uchkun07.travelsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 管理员登录响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminLoginResponse {

    /**
     * JWT令牌
     */
    private String token;

    /**
     * 管理员ID
     */
    private Long adminId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String fullName;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 权限编码列表
     */
    private List<String> permissions;
}
