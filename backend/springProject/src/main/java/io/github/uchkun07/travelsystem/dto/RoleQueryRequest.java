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
@Schema(description = "角色查询请求")
public class RoleQueryRequest {

    @Builder.Default
    @Schema(description = "当前页码", example = "1")
    private Long pageNum = 1L;

    @Builder.Default
    @Schema(description = "每页条数", example = "10")
    private Long pageSize = 10L;

    @Schema(description = "角色名称（模糊查询）", example = "管理员")
    private String roleName;

    @Schema(description = "角色状态", example = "1")
    private Integer status;
}
