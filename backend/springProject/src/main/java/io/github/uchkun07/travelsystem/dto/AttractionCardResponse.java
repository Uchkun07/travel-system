package io.github.uchkun07.travelsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 景点卡片响应DTO(C端列表展示)
 * 只包含卡片展示所需的核心字段
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "景点卡片响应")
public class AttractionCardResponse {

    @Schema(description = "景点ID")
    private Long attractionId;

    @Schema(description = "景点名称")
    private String name;

    @Schema(description = "景点描述")
    private String description;

    @Schema(description = "景点类型")
    private String type;

    @Schema(description = "城市名称")
    private String location;

    @Schema(description = "主图URL")
    private String imageUrl;

    @Schema(description = "平均评分")
    private BigDecimal averageRating;

    @Schema(description = "浏览量")
    private Integer viewCount;

    @Schema(description = "人气指数")
    private Integer popularity;

    @Schema(description = "门票价格")
    private BigDecimal ticketPrice;
}
