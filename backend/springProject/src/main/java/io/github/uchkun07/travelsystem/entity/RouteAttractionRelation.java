package io.github.uchkun07.travelsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 路线-景点关联表实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("route_attraction_relation")
public class RouteAttractionRelation {

    /**
     * 路线-景点关联记录唯一标识
     */
    @TableId(value = "relation_id", type = IdType.AUTO)
    private Long relationId;

    /**
     * 关联旅游路线表
     */
    @TableField("travel_route_id")
    private Long travelRouteId;

    /**
     * 关联景点表
     */
    @TableField("attraction_id")
    private Long attractionId;

    /**
     * 景点顺序（前端渲染顺序依据）
     */
    @TableField("sequence")
    private Integer sequence;

    /**
     * 该景点停留天数
     */
    @TableField("stay_days")
    private Integer stayDays;

    /**
     * 该景点单独预计花费（单位：元）
     */
    @TableField("single_attraction_cost")
    private BigDecimal singleAttractionCost;

    /**
     * 停留当日气候
     */
    @TableField("daily_weather")
    private String dailyWeather;

    /**
     * 前往该景点的路费（单位：元）
     */
    @TableField("traffic_cost")
    private BigDecimal trafficCost;

    /**
     * 该景点行程小贴士
     */
    @TableField("route_tips")
    private String routeTips;

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
