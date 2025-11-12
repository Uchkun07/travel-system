package io.github.uchkun07.travelsystem.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 更新管理员请求
 */
@Data
public class AdminUpdateRequest {

    /**
     * 管理员ID
     */
    @NotNull(message = "管理员ID不能为空")
    private Long adminId;

    /**
     * 管理员真实姓名
     */
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
