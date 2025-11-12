package io.github.uchkun07.travelsystem.dto;

import lombok.Data;

/**
 * 管理员查询请求
 */
@Data
public class AdminQueryRequest {

    /**
     * 用户名（模糊查询）
     */
    private String username;

    /**
     * 真实姓名（模糊查询）
     */
    private String fullName;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 账号状态（1=正常，0=禁用）
     */
    private Integer status;

    /**
     * 当前页码
     */
    private Integer current = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;
}
