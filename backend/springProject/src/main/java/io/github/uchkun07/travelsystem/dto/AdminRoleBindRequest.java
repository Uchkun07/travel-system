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
@Schema(description = "管理员角色绑定请求")
public class AdminRoleBindRequest {

    @NotNull(message = "管理员ID不能为空")
    @Schema(description = "管理员ID", example = "1")
    private Long adminId;

    @NotEmpty(message = "角色ID列表不能为空")
    @Schema(description = "角色ID列表", example = "[1, 2]")
    private List<Integer> roleIds;
}
