
package org.example.springproject.entity;

import com.baomidou.mybatisplus.annotation.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户景点收藏表实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("user_attraction_collection")
public class UserAttractionCollection  {
    /** 收藏记录唯一ID */
    @TableId(value = "collection_id", type = IdType.AUTO)
    private Long collectionId;

    /** 用户ID（关联用户表user的user_id） */
    @TableField("user_id")
    private Integer userId;

    /** 单个收藏景点ID（关联景点表attraction的attraction_id） */
    @TableField("attraction_id")
    private Integer attractionId;

    /** 收藏时间（支撑排序需求） */
    @TableField(value = "collect_time", fill = FieldFill.INSERT)
    private LocalDateTime collectTime;

    /** 逻辑删除标记（支撑取消收藏需求） */
    @TableField("is_deleted")
    // @TableLogic  这个千万不能加，气死我了，找了半天结果bug在这里
    private Integer isDeleted;
}
