package org.example.springproject.entity.dto;

import lombok.Data;

/**
 * 发送验证码请求DTO
 */
@Data
public class SendCodeRequest {
    /**
     * 邮箱地址
     */
    private String email;
}
