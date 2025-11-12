package io.github.uchkun07.travelsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 轮播图查询请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "轮播图查询请求")
public class SlideshowQueryRequest {

    @Schema(description = "页码", example = "1")
    private Long pageNum;

    @Schema(description = "每页大小", example = "10")
    private Long pageSize;

    @Schema(description = "标题(模糊查询)")
    private String title;

    @Schema(description = "状态(0=禁用,1=启用)")
    private Integer status;

    @Schema(description = "关联的景点ID")
    private Long attractionId;
}
