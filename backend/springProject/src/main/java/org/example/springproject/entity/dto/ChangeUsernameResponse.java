package org.example.springproject.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 修改用户名响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeUsernameResponse {
    private Boolean success;
    private String message;
    private String token;
    private String username;

    public static ChangeUsernameResponse success(String message, String token, String username) {
        return new ChangeUsernameResponse(true, message, token, username);
    }

    public static ChangeUsernameResponse error(String message) {
        return new ChangeUsernameResponse(false, message, null, null);
    }
}
