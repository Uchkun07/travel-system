package io.github.uchkun07.travelsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 城市表实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("city")
public class City {

    /**
     * 城市唯一标识
     */
    @TableId(value = "city_id", type = IdType.AUTO)
    private Integer cityId;

    /**
     * 城市名称（不可重复）
     */
    @TableField("city_name")
    private String cityName;

    /**
     * 所属国家
     */
    @TableField("country")
    private String country;

    /**
     * 城市图片
     */
    @TableField("city_url")
    private String cityUrl;

    /**
     * 平均气温（单位：℃）
     */
    @TableField("average_temperature")
    private BigDecimal averageTemperature;

    /**
     * 城市内景点数量
     */
    @TableField("attraction_count")
    private Integer attractionCount;

    /**
     * 热度值（热门城市排序依据）
     */
    @TableField("popularity")
    private Integer popularity;

    /**
     * 城市简介
     */
    @TableField("description")
    private String description;

    /**
     * 展示排序序号
     */
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 状态（1=启用，0=禁用）
     */
    @TableField("status")
    private Integer status;

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
