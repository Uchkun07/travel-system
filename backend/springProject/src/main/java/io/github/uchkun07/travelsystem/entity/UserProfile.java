package io.github.uchkun07.travelsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户基本信息表实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("user_profile")
public class UserProfile {

    /**
     * 基本信息唯一标识
     */
    @TableId(value = "profile_id", type = IdType.AUTO)
    private Long profileId;

    /**
     * 关联用户账号表
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 用户真实姓名
     */
    @TableField("full_name")
    private String fullName;

    /**
     * 绑定手机号（用于安全验证）
     */
    @TableField("phone")
    private String phone;

    /**
     * 性别（0=未知，1=男，2=女）
     */
    @TableField("gender")
    private Integer gender;

    /**
     * 出生日期
     */
    @TableField("birthday")
    private LocalDate birthday;

    /**
     * 常驻地址
     */
    @TableField("resident_address")
    private String residentAddress;

    /**
     * 信息创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 信息更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
