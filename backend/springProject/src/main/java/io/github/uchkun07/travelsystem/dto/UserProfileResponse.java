package io.github.uchkun07.travelsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户基本信息响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 真实姓名
     */
    private String fullName;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 性别（0=未知，1=男，2=女）
     */
    private Integer gender;

    /**
     * 出生日期（YYYY-MM-DD）
     */
    private String birthday;

    /**
     * 常驻地址
     */
    private String residentAddress;
}
