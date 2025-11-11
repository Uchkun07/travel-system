package io.github.uchkun07.travelsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 景点标签表实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("attraction_tag")
public class AttractionTag {

    /**
     * 标签唯一标识
     */
    @TableId(value = "tag_id", type = IdType.AUTO)
    private Integer tagId;

    /**
     * 标签名称（不可重复）
     */
    @TableField("tag_name")
    private String tagName;

    /**
     * 标签描述
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
