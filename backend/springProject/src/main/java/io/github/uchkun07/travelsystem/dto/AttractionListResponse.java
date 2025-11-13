package io.github.uchkun07.travelsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 景点列表响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "景点列表响应")
public class AttractionListResponse {

    @Schema(description = "景点ID")
    private Long attractionId;

    @Schema(description = "景点名称")
    private String name;

    @Schema(description = "景点类型名称")
    private String typeName;

    @Schema(description = "城市名称")
    private String cityName;

    @Schema(description = "浏览量")
    private Integer browseCount;

    @Schema(description = "收藏数")
    private Integer favoriteCount;

    @Schema(description = "人气指数")
    private Integer popularity;

    @Schema(description = "主图URL")
    private String mainImageUrl;

    @Schema(description = "景点状态（1=正常，0=下架）")
    private Integer status;

    @Schema(description = "审核状态（1=待审核，2=已通过，3=已驳回）")
    private Integer auditStatus;
}
