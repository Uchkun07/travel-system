package io.github.uchkun07.travelsystem.dto;

import io.github.uchkun07.travelsystem.entity.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户详情响应DTO
 * 包含用户基本信息、Profile、Preference和标签列表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户详情响应")
public class UserDetailResponse {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "头像URL")
    private String avatarUrl;

    @Schema(description = "用户状态")
    private Integer status;

    @Schema(description = "注册时间")
    private LocalDateTime createTime;

    @Schema(description = "最后登录时间")
    private LocalDateTime lastLoginTime;

    @Schema(description = "用户基本信息")
    private UserProfile profile;

    @Schema(description = "用户偏好设置")
    private UserPreference preference;

    @Schema(description = "用户标签列表")
    private List<UserTagDict> tags;
}
