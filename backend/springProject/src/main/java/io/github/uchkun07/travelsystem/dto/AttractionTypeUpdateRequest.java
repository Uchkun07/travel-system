package io.github.uchkun07.travelsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 修改景点类型请求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "修改景点类型请求")
public class AttractionTypeUpdateRequest {

    /**
     * 类型唯一标识
     */
    @NotNull(message = "类型ID不能为空")
    @Schema(description = "类型唯一标识", example = "1")
    private Integer typeId;

    /**
     * 类型名称（不可重复）
     */
    @Schema(description = "类型名称", example = "自然风光")
    private String typeName;

    /**
     * 展示排序序号
     */
    @Schema(description = "展示排序序号", example = "1")
    private Integer sortOrder;

    /**
     * 状态（1=启用，0=禁用）
     */
    @Schema(description = "状态（1=启用，0=禁用）", example = "1")
    private Integer status;
}
