package io.github.uchkun07.travelsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户浏览记录表实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("user_browse_record")
public class UserBrowseRecord {

    /**
     * 浏览记录唯一标识
     */
    @TableId(value = "browse_record_id", type = IdType.AUTO)
    private Long browseRecordId;

    /**
     * 关联用户账号表
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 关联景点表
     */
    @TableField("attraction_id")
    private Long attractionId;

    /**
     * 浏览次数
     */
    @TableField("browse_duration")
    private Integer browseDuration;

    /**
     * 浏览时间
     */
    @TableField("browse_time")
    private LocalDateTime browseTime;

    /**
     * 浏览设备
     */
    @TableField("device_info")
    private String deviceInfo;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
