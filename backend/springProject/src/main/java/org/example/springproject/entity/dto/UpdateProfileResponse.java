package org.example.springproject.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新用户资料响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileResponse {
    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 消息
     */
    private String message;

    /**
     * 新的JWT令牌
     */
    private String token;

    /**
     * 用户信息
     */
    private UserProfileData userInfo;

    /**
     * 用户资料数据
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserProfileData {
        private Integer userId;
        private String username;
        private String fullName;
        private String email;
        private String phone;
        private Byte gender;
        private String birthday;
        private String avatar;
    }

    /**
     * 成功响应
     */
    public static UpdateProfileResponse success(String token, UserProfileData userInfo) {
        return new UpdateProfileResponse(true, "资料更新成功", token, userInfo);
    }

    /**
     * 失败响应
     */
    public static UpdateProfileResponse error(String message) {
        return new UpdateProfileResponse(false, message, null, null);
    }
}
