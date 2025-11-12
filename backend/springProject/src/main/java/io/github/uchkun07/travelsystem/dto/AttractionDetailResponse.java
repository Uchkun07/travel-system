package io.github.uchkun07.travelsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 景点详情响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "景点详情响应")
public class AttractionDetailResponse {

    @Schema(description = "景点ID")
    private Long attractionId;

    @Schema(description = "景点名称")
    private String name;

    @Schema(description = "景点副标题")
    private String subtitle;

    @Schema(description = "景点类型ID")
    private Integer typeId;

    @Schema(description = "景点类型名称")
    private String typeName;

    @Schema(description = "城市ID")
    private Integer cityId;

    @Schema(description = "城市名称")
    private String cityName;

    @Schema(description = "详细地址")
    private String address;

    @Schema(description = "纬度")
    private BigDecimal latitude;

    @Schema(description = "经度")
    private BigDecimal longitude;

    @Schema(description = "主图URL")
    private String mainImageUrl;

    @Schema(description = "多图URL（JSON格式）")
    private String multiImageUrls;

    @Schema(description = "平均评分")
    private BigDecimal averageRating;

    @Schema(description = "评分人数")
    private Integer ratingCount;

    @Schema(description = "浏览量")
    private Integer browseCount;

    @Schema(description = "收藏数")
    private Integer favoriteCount;

    @Schema(description = "人气指数")
    private Integer popularity;

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

    @Schema(description = "创建人ID")
    private Long creatorId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "绑定的标签列表")
    private List<TagInfo> tags;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "标签信息")
    public static class TagInfo {
        @Schema(description = "标签ID")
        private Integer tagId;

        @Schema(description = "标签名称")
        private String tagName;
    }
}
