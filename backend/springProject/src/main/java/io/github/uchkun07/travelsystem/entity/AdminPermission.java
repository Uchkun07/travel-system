package io.github.uchkun07.travelsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 管理员权限表实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("admin_permission")
public class AdminPermission {

    /**
     * 权限唯一标识（精细化权限控制核心）
     */
    @TableId(value = "permission_id", type = IdType.AUTO)
    private Integer permissionId;

    /**
     * 权限编码（代码中统一引用）
     */
    @TableField("permission_code")
    private String permissionCode;

    /**
     * 权限名称
     */
    @TableField("permission_name")
    private String permissionName;

    /**
     * 资源类型（如"用户管理""景点管理"）
     */
    @TableField("resource_type")
    private String resourceType;

    /**
     * 关联接口路径（用于接口权限拦截）
     */
    @TableField("resource_path")
    private String resourcePath;

    /**
     * 是否敏感权限（1=是，0=否）
     */
    @TableField("is_sensitive")
    private Integer isSensitive;

    /**
     * 权限排序序号
     */
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 权限创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 权限更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
