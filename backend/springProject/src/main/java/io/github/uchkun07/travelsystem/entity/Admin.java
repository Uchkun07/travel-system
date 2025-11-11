package io.github.uchkun07.travelsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 管理员表实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("admin")
public class Admin {

    /**
     * 管理员唯一标识（企业级管理端用户核心ID）
     */
    @TableId(value = "admin_id", type = IdType.AUTO)
    private Long adminId;

    /**
     * 管理员用户名（管理端登录凭证）
     */
    @TableField("username")
    private String username;

    /**
     * 加密密码（PBKDF2算法加盐存储）
     */
    @TableField("password")
    private String password;

    /**
     * 密码盐值（随机字符串）
     */
    @TableField("password_salt")
    private String passwordSalt;

    /**
     * PBKDF2迭代次数
     */
    @TableField("pbkdf2_iterations")
    private Integer pbkdf2Iterations;

    /**
     * 管理员真实姓名
     */
    @TableField("full_name")
    private String fullName;

    /**
     * 管理员手机号
     */
    @TableField("phone")
    private String phone;

    /**
     * 管理员邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 账号状态（1=正常，0=禁用）
     */
    @TableField("status")
    private Integer status;

    /**
     * 最后登录时间
     */
    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;

    /**
     * 最后登录IP地址
     */
    @TableField("last_login_ip")
    private String lastLoginIp;

    /**
     * 累计登录次数
     */
    @TableField("login_count")
    private Integer loginCount;

    /**
     * 账号创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 账号信息更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
