package org.example.springproject.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import java.util.Date;

/**
 * 用户资料更新请求DTO
 */
@Data
public class UpdateProfileRequest {
    /**
     * 全名
     */
    private String fullName;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 手机号
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 性别 (0=保密, 1=男, 2=女)
     */
    private Byte gender;

    /**
     * 生日
     */
    private Date birthday;

    /**
     * 头像URL
     */
    private String avatar;
}
