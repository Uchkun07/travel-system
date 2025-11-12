package io.github.uchkun07.travelsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 管理员密码修改请求
 */
@Data
public class AdminPasswordUpdateRequest {

    /**
     * 管理员ID
     */
    @NotNull(message = "管理员ID不能为空")
    private Long adminId;

    /**
     * 新密码
     */
    @NotBlank(message = "新密码不能为空")
    @Pattern(regexp = "^.{6,30}$", message = "密码长度必须在6-30位之间")
    private String newPassword;
}
