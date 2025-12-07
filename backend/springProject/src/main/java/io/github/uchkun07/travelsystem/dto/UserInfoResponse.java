package io.github.uchkun07.travelsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户信息响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户信息响应")
public class UserInfoResponse {

    @Schema(description = "是否成功")
    private Boolean success;

    @Schema(description = "消息")
    private String message;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "昵称")
    private String fullName;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "状态")
    private Integer status;
}
