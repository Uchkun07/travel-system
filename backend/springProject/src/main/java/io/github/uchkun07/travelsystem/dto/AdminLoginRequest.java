package io.github.uchkun07.travelsystem.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 管理员登录请求DTO
 */
@Data
public class AdminLoginRequest {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 是否记住我
     */
    private Boolean rememberMe = false;
}
