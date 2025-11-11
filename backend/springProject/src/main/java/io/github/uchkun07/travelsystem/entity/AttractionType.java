package io.github.uchkun07.travelsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 景点类型表实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("attraction_type")
public class AttractionType {

    /**
     * 类型唯一标识
     */
    @TableId(value = "type_id", type = IdType.AUTO)
    private Integer typeId;

    /**
     * 类型名称（不可重复）
     */
    @TableField("type_name")
    private String typeName;

    /**
     * 父类型ID（0=一级类型）
     */
    @TableField("parent_id")
    private Integer parentId;

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
