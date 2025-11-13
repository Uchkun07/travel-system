package io.github.uchkun07.travelsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 用户标签批量绑定请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户标签批量绑定请求")
public class UserTagBatchBindRequest {

    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    @NotEmpty(message = "标签字典ID列表不能为空")
    @Schema(description = "标签字典ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Integer> tagDictIds;
}
