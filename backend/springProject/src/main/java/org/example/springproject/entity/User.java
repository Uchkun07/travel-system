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

    @TableField("password")
    private String password;

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

    @TableField(fill = FieldFill.INSERT)
    private Date registerTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date lastLoginTime;

    @TableField("status")
    private Byte status;

    @TableField("preference_type")
    private String preferenceType;

    @TableField("preference_destination")
    private String preferenceDestination;

    @TableField("budget_range")
    private String budgetRange;
}
