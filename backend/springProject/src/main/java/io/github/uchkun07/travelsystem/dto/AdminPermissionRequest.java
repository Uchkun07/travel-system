package io.github.uchkun07.travelsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "权限请求")
public class AdminPermissionRequest {

    @Schema(description = "权限ID（修改时必填）")
    private Integer permissionId;

    @NotBlank(message = "权限编码不能为空")
    @Schema(description = "权限编码", example = "USER:VIEW")
    private String permissionCode;

    @NotBlank(message = "权限名称不能为空")
    @Schema(description = "权限名称", example = "查看用户")
    private String permissionName;

    @Schema(description = "资源类型", example = "用户管理")
    private String resourceType;

    @Schema(description = "资源路径", example = "/admin/user/**")
    private String resourcePath;

    @Schema(description = "是否敏感权限（1=是，0=否）", example = "0")
    private Integer isSensitive;

    @Schema(description = "排序序号", example = "1")
    private Integer sortOrder;
}
