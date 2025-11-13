package io.github.uchkun07.travelsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 旅游路线表实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("travel_route")
public class TravelRoute {

    /**
     * 路线唯一标识
     */
    @TableId(value = "travel_route_id", type = IdType.AUTO)
    private Long travelRouteId;

    /**
     * 关联用户账号表（创建者）
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 路线名称（如"北京3日游"）
     */
    @TableField("route_name")
    private String routeName;

    /**
     * 出发日期
     */
    @TableField("start_date")
    private LocalDate startDate;

    /**
     * 结束日期
     */
    @TableField("end_date")
    private LocalDate endDate;

    /**
     * 总时长（单位：天）
     */
    @TableField("total_duration")
    private Integer totalDuration;

    /**
     * 总距离（单位：公里）
     */
    @TableField("total_distance")
    private BigDecimal totalDistance;

    /**
     * 路线总预计花费（单位：元）
     */
    @TableField("total_cost")
    private BigDecimal totalCost;

    /**
     * 算法输入参数（JSON格式）
     */
    @TableField("algorithm_params")
    private String algorithmParams;

    /**
     * 路线整体气候总结
     */
    @TableField("weather_summary")
    private String weatherSummary;

    /**
     * 是否收藏（0=否，1=是）
     */
    @TableField("is_favorite")
    private Integer isFavorite;

    /**
     * 查看次数
     */
    @TableField("view_count")
    private Integer viewCount;

    /**
     * 逻辑删除标记（0=未删除，1=已删除）
     */
    @TableField("is_deleted")
    @TableLogic
    private Integer isDeleted;

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
