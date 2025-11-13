package io.github.uchkun07.travelsystem.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 创建管理员请求
 */
@Data
public class AdminCreateRequest {

    /**
     * 管理员用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_]{4,20}$", message = "用户名只能包含字母、数字和下划线，长度4-20位")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = "^.{6,30}$", message = "密码长度必须在6-30位之间")
    private String password;

    /**
     * 管理员真实姓名
     */
    @NotBlank(message = "真实姓名不能为空")
    private String fullName;

    /**
     * 管理员手机号
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 管理员邮箱
     */
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 账号状态（1=正常，0=禁用）
     */
    private Integer status;
}
