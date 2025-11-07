package org.example.springproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 修改邮箱响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeEmailResponse {
    private Boolean success;
    private String message;
    private String email;

    public static ChangeEmailResponse success(String message, String email) {
        return new ChangeEmailResponse(true, message, email);
    }

    public static ChangeEmailResponse error(String message) {
        return new ChangeEmailResponse(false, message, null);
    }
}
