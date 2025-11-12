package io.github.uchkun07.travelsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 轮播图请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "轮播图请求")
public class SlideshowRequest {

    @Schema(description = "轮播图ID(修改时必填)")
    private Integer slideshowId;

    @Schema(description = "轮播图标题", required = true)
    private String title;

    @Schema(description = "副标题/描述")
    private String subtitle;

    @Schema(description = "轮播图图片URL", required = true)
    private String imageUrl;

    @Schema(description = "关联的景点ID")
    private Long attractionId;

    @Schema(description = "显示顺序")
    private Integer displayOrder;

    @Schema(description = "状态(0=禁用,1=启用)")
    private Integer status;

    @Schema(description = "开始展示时间")
    private LocalDateTime startTime;

    @Schema(description = "结束展示时间")
    private LocalDateTime endTime;
}
