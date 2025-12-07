package io.github.uchkun07.travelsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户登录请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户登录请求")
public class UserLoginRequest {

    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "用户名不能为空")
    private String username;

    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "密码不能为空")
    private String password;

    @Schema(description = "记住我")
    private Boolean rememberMe;
}
