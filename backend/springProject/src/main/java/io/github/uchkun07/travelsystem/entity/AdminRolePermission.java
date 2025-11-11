package io.github.uchkun07.travelsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 角色-权限关联表实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("admin_role_permission")
public class AdminRolePermission {

    /**
     * 角色-权限关联记录唯一标识
     */
    @TableId(value = "role_permission_id", type = IdType.AUTO)
    private Long rolePermissionId;

    /**
     * 关联管理员角色表
     */
    @TableField("role_id")
    private Integer roleId;

    /**
     * 关联管理员权限表
     */
    @TableField("permission_id")
    private Integer permissionId;

    /**
     * 关联记录创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
