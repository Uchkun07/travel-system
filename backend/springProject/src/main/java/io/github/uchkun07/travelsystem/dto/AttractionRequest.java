package io.github.uchkun07.travelsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 景点创建/更新请求DTO
 */
@Data
@Schema(description = "景点创建/更新请求")
public class AttractionRequest {

    @Schema(description = "景点ID(更新时必填)")
    private Long attractionId;

    @Schema(description = "景点名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "景点副标题")
    private String subtitle;

    @Schema(description = "景点类型ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer typeId;

    @Schema(description = "城市ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer cityId;

    @Schema(description = "详细地址")
    private String address;

    @Schema(description = "纬度（范围：-90~90）")
    private BigDecimal latitude;

    @Schema(description = "经度（范围：-180~180）")
    private BigDecimal longitude;

    @Schema(description = "主图URL")
    private String mainImageUrl;

    @Schema(description = "多图URL（JSON格式）")
    private String multiImageUrls;

    @Schema(description = "建议游览时间（单位：分钟）")
    private Integer estimatedPlayTime;

    @Schema(description = "门票价格（单位：元）")
    private BigDecimal ticketPrice;

    @Schema(description = "门票说明")
    private String ticketDescription;

    @Schema(description = "开放时间")
    private String openingHours;

    @Schema(description = "最佳观光季节")
    private String bestSeason;

    @Schema(description = "历史底蕴")
    private String historicalContext;

    @Schema(description = "安全提示")
    private String safetyTips;

    @Schema(description = "官方网站")
    private String officialWebsite;

    @Schema(description = "附近美食（JSON格式）")
    private String nearbyFood;

    @Schema(description = "景点状态（1=正常，0=下架）")
    private Integer status;

    @Schema(description = "审核状态（1=待审核，2=已通过，3=已驳回）")
    private Integer auditStatus;
}
