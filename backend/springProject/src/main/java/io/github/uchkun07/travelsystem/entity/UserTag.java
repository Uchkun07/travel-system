package io.github.uchkun07.travelsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户标签表实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("user_tag")
public class UserTag {

    /**
     * 用户-标签关联唯一标识
     */
    @TableId(value = "user_tag_id", type = IdType.AUTO)
    private Long userTagId;

    /**
     * 关联用户账号表
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 关联用户标签字典表
     */
    @TableField("tag_dict_id")
    private Integer tagDictId;

    /**
     * 标签获取时间
     */
    @TableField("obtain_time")
    private LocalDateTime obtainTime;

    /**
     * 关联记录创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 关联记录更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
