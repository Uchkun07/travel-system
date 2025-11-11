package io.github.uchkun07.travelsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户偏好表实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("user_preference")
public class UserPreference {

    /**
     * 偏好唯一标识
     */
    @TableId(value = "preference_id", type = IdType.AUTO)
    private Long preferenceId;

    /**
     * 关联用户账号表
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 偏好景点类型ID（关联attraction_type表）
     */
    @TableField("prefer_attraction_type_id")
    private Integer preferAttractionTypeId;

    /**
     * 预算下限
     */
    @TableField("budget_floor")
    private Integer budgetFloor;

    /**
     * 预算上限
     */
    @TableField("budget_range")
    private Integer budgetRange;

    /**
     * 出行人群（如"独自出行""情侣""亲子"）
     */
    @TableField("travel_crowd")
    private String travelCrowd;

    /**
     * 偏好出行季节
     */
    @TableField("prefer_season")
    private String preferSeason;

    /**
     * 偏好创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 偏好更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
