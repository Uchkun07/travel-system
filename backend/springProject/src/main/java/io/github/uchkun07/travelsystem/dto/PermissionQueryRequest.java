package io.github.uchkun07.travelsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "权限查询请求")
public class PermissionQueryRequest {

    @Builder.Default
    @Schema(description = "当前页码", example = "1")
    private Long pageNum = 1L;

    @Builder.Default
    @Schema(description = "每页条数", example = "10")
    private Long pageSize = 10L;

    @Schema(description = "权限编码（模糊查询）", example = "USER")
    private String permissionCode;

    @Schema(description = "权限名称（模糊查询）", example = "用户")
    private String permissionName;

    @Schema(description = "资源类型", example = "用户管理")
    private String resourceType;

    @Schema(description = "是否敏感权限", example = "1")
    private Integer isSensitive;
}
