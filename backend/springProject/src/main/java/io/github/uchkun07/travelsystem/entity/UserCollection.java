package io.github.uchkun07.travelsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户收藏记录表实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("user_collection")
public class UserCollection {

    /**
     * 收藏记录唯一标识
     */
    @TableId(value = "collection_id", type = IdType.AUTO)
    private Long collectionId;

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
     * 收藏时间
     */
    @TableField("collection_time")
    private LocalDateTime collectionTime;

    /**
     * 逻辑删除标记（0=未删除，1=已删除）
     */
    @TableField("is_deleted")
    // @TableLogic
    private Integer isDeleted;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
