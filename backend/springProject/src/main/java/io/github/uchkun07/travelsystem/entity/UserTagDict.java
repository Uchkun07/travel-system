package io.github.uchkun07.travelsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户标签字典表实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("user_tag_dict")
public class UserTagDict {

    /**
     * 标签字典唯一标识
     */
    @TableId(value = "tag_dict_id", type = IdType.AUTO)
    private Integer tagDictId;

    /**
     * 标签名称（系统唯一）
     */
    @TableField("tag_name")
    private String tagName;

    /**
     * 标签编码（代码中统一引用）
     */
    @TableField("tag_code")
    private String tagCode;

    /**
     * 标签等级（1=初级，2=中级，3=高级）
     */
    @TableField("tag_level")
    private Integer tagLevel;

    /**
     * 标签触发条件（JSON格式）
     */
    @TableField("trigger_condition")
    private String triggerCondition;

    /**
     * 标签描述
     */
    @TableField("description")
    private String description;

    /**
     * 标签图标URL
     */
    @TableField("icon_url")
    private String iconUrl;

    /**
     * 标签状态（1=启用，0=禁用）
     */
    @TableField("status")
    private Integer status;

    /**
     * 展示排序序号
     */
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 标签创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 标签更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
