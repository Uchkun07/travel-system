package org.example.springproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 修改密码响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordResponse {
    private Boolean success;
    private String message;

    public static ChangePasswordResponse success(String message) {
        return new ChangePasswordResponse(true, message);
    }

    public static ChangePasswordResponse error(String message) {
        return new ChangePasswordResponse(false, message);
    }
}
