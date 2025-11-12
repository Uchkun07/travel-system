package io.github.uchkun07.travelsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建景点类型请求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "创建景点类型请求")
public class AttractionTypeCreateRequest {

    /**
     * 类型名称（不可重复）
     */
    @NotBlank(message = "类型名称不能为空")
    @Schema(description = "类型名称", example = "自然风光")
    private String typeName;

    /**
     * 展示排序序号
     */
    @NotNull(message = "排序序号不能为空")
    @Schema(description = "展示排序序号", example = "1")
    private Integer sortOrder;

    /**
     * 状态（1=启用，0=禁用）
     */
    @NotNull(message = "状态不能为空")
    @Schema(description = "状态（1=启用，0=禁用）", example = "1")
    private Integer status;
}
