package io.github.uchkun07.travelsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "角色请求")
public class AdminRoleRequest {

    @Schema(description = "角色ID（修改时必填）")
    private Integer roleId;

    @NotBlank(message = "角色名称不能为空")
    @Schema(description = "角色名称", example = "内容管理员")
    private String roleName;

    @Schema(description = "角色描述", example = "负责内容审核和管理")
    private String roleDesc;

    @NotNull(message = "角色状态不能为空")
    @Schema(description = "角色状态（1=启用，0=禁用）", example = "1")
    private Integer status;
}
