package io.github.uchkun07.travelsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 景点类型查询请求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "景点类型查询请求")
public class AttractionTypeQueryRequest {

    /**
     * 当前页码
     */
    @Builder.Default
    @Schema(description = "当前页码", example = "1")
    private Long pageNum = 1L;

    /**
     * 每页条数
     */
    @Builder.Default
    @Schema(description = "每页条数", example = "10")
    private Long pageSize = 10L;

    /**
     * 类型ID（可选，用于精确查询）
     */
    @Schema(description = "类型ID", example = "1")
    private Integer typeId;

    /**
     * 类型名称（可选，用于模糊查询）
     */
    @Schema(description = "类型名称（模糊查询）", example = "自然")
    private String typeName;

    /**
     * 状态（可选，用于筛选）
     */
    @Schema(description = "状态（1=启用，0=禁用）", example = "1")
    private Integer status;
}
