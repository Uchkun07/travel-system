package io.github.uchkun07.travelsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 景点-标签关联表实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("attraction_tag_relation")
public class AttractionTagRelation {

    /**
     * 景点-标签关联记录唯一标识
     */
    @TableId(value = "relation_id", type = IdType.AUTO)
    private Long relationId;

    /**
     * 关联景点表
     */
    @TableField("attraction_id")
    private Long attractionId;

    /**
     * 关联景点标签表
     */
    @TableField("tag_id")
    private Integer tagId;

    /**
     * 关联记录创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
