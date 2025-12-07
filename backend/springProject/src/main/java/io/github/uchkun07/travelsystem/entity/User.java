package io.github.uchkun07.travelsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户账号表实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("user")
public class User {

    /**
     * 用户唯一标识
     */
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    /**
     * 用户名（登录账号）
     */
    @TableField("username")
    private String username;

    /**
     * 用户密码（加密存储）
     */
    @TableField("password")
    private String password;

    /**
     * 密码盐值（BCrypt 已包含盐值，此字段保留以兼容数据库结构）
     */
    @TableField(value = "password_salt", fill = FieldFill.INSERT)
    private String passwordSalt;

    /**
     * 用户昵称（数据库无此字段，仅用于应用层，不插入数据库）
     */
    @TableField(exist = false)
    private String nickname;

    /**
     * 用户邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 头像URL
     */
    @TableField("avatar_url")
    private String avatarUrl;

    /**
     * 用户状态（1=正常，0=禁用）
     */
    @TableField("status")
    private Integer status;

    /**
     * 最后登录时间
     */
    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;

    /**
     * 账号创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 账号更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
