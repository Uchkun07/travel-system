package io.github.uchkun07.travelsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 管理员角色表实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("admin_role")
public class AdminRole {

    /**
     * 角色唯一标识（企业级权限分级核心）
     */
    @TableId(value = "role_id", type = IdType.AUTO)
    private Integer roleId;

    /**
     * 角色名称（不可重复）
     */
    @TableField("role_name")
    private String roleName;

    /**
     * 角色描述
     */
    @TableField("role_desc")
    private String roleDesc;

    /**
     * 角色状态（1=启用，0=禁用）
     */
    @TableField("status")
    private Integer status;

    /**
     * 角色创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 角色更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
