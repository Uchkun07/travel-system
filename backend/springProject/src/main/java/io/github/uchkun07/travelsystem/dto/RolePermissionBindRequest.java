package io.github.uchkun07.travelsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "角色权限绑定请求")
public class RolePermissionBindRequest {

    @NotNull(message = "角色ID不能为空")
    @Schema(description = "角色ID", example = "1")
    private Integer roleId;

    @NotEmpty(message = "权限ID列表不能为空")
    @Schema(description = "权限ID列表", example = "[1, 2, 3]")
    private List<Integer> permissionIds;
}
