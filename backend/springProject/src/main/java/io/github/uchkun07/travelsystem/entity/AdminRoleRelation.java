package io.github.uchkun07.travelsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 管理员-角色关联表实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("admin_role_relation")
public class AdminRoleRelation {

    /**
     * 管理员-角色关联记录唯一标识
     */
    @TableId(value = "admin_role_id", type = IdType.AUTO)
    private Long adminRoleId;

    /**
     * 关联管理员表
     */
    @TableField("admin_id")
    private Long adminId;

    /**
     * 关联管理员角色表
     */
    @TableField("role_id")
    private Integer roleId;

    /**
     * 关联记录创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
