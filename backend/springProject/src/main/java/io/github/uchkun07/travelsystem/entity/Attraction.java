package io.github.uchkun07.travelsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 景点表实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("attraction")
public class Attraction {

    /**
     * 景点唯一标识
     */
    @TableId(value = "attraction_id", type = IdType.AUTO)
    private Long attractionId;

    /**
     * 景点名称（不可重复）
     */
    @TableField("name")
    private String name;

    /**
     * 景点副标题
     */
    @TableField("subtitle")
    private String subtitle;

    /**
     * 景点详细描述
     */
    @TableField("description")
    private String description;

    /**
     * 关联景点类型表
     */
    @TableField("type_id")
    private Integer typeId;

    /**
     * 关联城市表
     */
    @TableField("city_id")
    private Integer cityId;

    /**
     * 详细地址
     */
    @TableField("address")
    private String address;

    /**
     * 纬度（范围：-90~90）
     */
    @TableField("latitude")
    private BigDecimal latitude;

    /**
     * 经度（范围：-180~180）
     */
    @TableField("longitude")
    private BigDecimal longitude;

    /**
     * 主图URL
     */
    @TableField("main_image_url")
    private String mainImageUrl;

    /**
     * 多图URL（JSON格式）
     */
    @TableField("multi_image_urls")
    private String multiImageUrls;

    /**
     * 平均评分（0.00~5.00）
     */
    @TableField("average_rating")
    private BigDecimal averageRating;

    /**
     * 评分人数
     */
    @TableField("rating_count")
    private Integer ratingCount;

    /**
     * 浏览量
     */
    @TableField("browse_count")
    private Integer browseCount;

    /**
     * 收藏量（冗余字段）
     */
    @TableField("favorite_count")
    private Integer favoriteCount;

    /**
     * 人气指数（综合排序依据）
     */
    @TableField("popularity")
    private Integer popularity;

    /**
     * 建议游览时间（单位：分钟）
     */
    @TableField("estimated_play_time")
    private Integer estimatedPlayTime;

    /**
     * 门票价格（单位：元）
     */
    @TableField("ticket_price")
    private BigDecimal ticketPrice;

    /**
     * 门票说明
     */
    @TableField("ticket_description")
    private String ticketDescription;

    /**
     * 开放时间
     */
    @TableField("opening_hours")
    private String openingHours;

    /**
     * 最佳观光季节
     */
    @TableField("best_season")
    private String bestSeason;

    /**
     * 历史底蕴
     */
    @TableField("historical_context")
    private String historicalContext;

    /**
     * 安全提示
     */
    @TableField("safety_tips")
    private String safetyTips;

    /**
     * 官方网站
     */
    @TableField("official_website")
    private String officialWebsite;

    /**
     * 附近美食（JSON格式）
     */
    @TableField("nearby_food")
    private String nearbyFood;

    /**
     * 景点状态（1=正常，0=下架）
     */
    @TableField("status")
    private Integer status;

    /**
     * 审核状态（1=待审核，2=已通过，3=已驳回）
     */
    @TableField("audit_status")
    private Integer auditStatus;

    /**
     * 创建人ID（关联管理员表）
     */
    @TableField("creator_id")
    private Long creatorId;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
