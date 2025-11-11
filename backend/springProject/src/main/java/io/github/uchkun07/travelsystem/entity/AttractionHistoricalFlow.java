package io.github.uchkun07.travelsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 景点历史人流数据表实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("attraction_historical_flow")
public class AttractionHistoricalFlow {

    /**
     * 记录唯一标识
     */
    @TableId(value = "historical_flow_id", type = IdType.AUTO)
    private Long historicalFlowId;

    /**
     * 关联景点表
     */
    @TableField("attraction_id")
    private Long attractionId;

    /**
     * 日期（精确到天）
     */
    @TableField("date")
    private LocalDate date;

    /**
     * 当日人流数量
     */
    @TableField("flow_count")
    private Integer flowCount;

    /**
     * 当日天气
     */
    @TableField("weather_condition")
    private String weatherCondition;

    /**
     * 是否节假日（0=否，1=是）
     */
    @TableField("holiday_flag")
    private Integer holidayFlag;

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
