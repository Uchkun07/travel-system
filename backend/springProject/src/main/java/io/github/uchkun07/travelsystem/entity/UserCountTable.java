package io.github.uchkun07.travelsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户计数表实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("user_count_table")
public class UserCountTable {

    /**
     * 计数唯一标识
     */
    @TableId(value = "count_id", type = IdType.AUTO)
    private Long countId;

    /**
     * 用户id
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 收藏数
     */
    @TableField("collect_count")
    private Integer collectCount;

    /**
     * 浏览数
     */
    @TableField("browsing_count")
    private Integer browsingCount;

    /**
     * 路线规划数
     */
    @TableField("planning_count")
    private Integer planningCount;

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
