package org.example.springproject.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("user")
public class User {
    @TableId(value = "user_id", type = IdType.AUTO)
    private Integer userId;

    @TableField("username")
    private String username;

    @TableField("password_hash")
    private String passwordHash;

    @TableField("salt")
    private String salt;

    @TableField("iterations")
    private Integer iterations;

    @TableField("email")
    private String email;

    @TableField("phone")
    private String phone;

    @TableField("full_name")
    private String fullName;

    @TableField("gender")
    private Byte gender;

    @TableField("birthday")
    private Date birthday;

    @TableField("avatar")
    private String avatar;

    @TableField("address")
    private String address;

    @TableField("preference_type")
    private String preferenceType;

    @TableField("preference_destination")
    private String preferenceDestination;

    @TableField("budget_range")
    private String budgetRange;

    @TableField("last_login_time")
    private Date lastLoginTime;

    @TableField("status")
    private Byte status;

    @TableField(value = "register_time", fill = FieldFill.INSERT)
    private Date registerTime;

    @TableField("union_id")
    private String unionId;

    @TableField("register_source")
    private String registerSource;
}
