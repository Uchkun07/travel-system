package io.github.uchkun07.travelsystem.controller;

import io.github.uchkun07.travelsystem.dto.ApiResponse;
import io.github.uchkun07.travelsystem.dto.UserPreferenceRequest;
import io.github.uchkun07.travelsystem.dto.UserPreferenceResponse;
import io.github.uchkun07.travelsystem.service.IUserPreferenceService;
import io.github.uchkun07.travelsystem.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 用户偏好控制器(C端)
 */
@Slf4j
@Tag(name = "用户偏好(C端)", description = "用户偏好管理接口")
@RestController
@RequestMapping("/api/user/preference")
@RequiredArgsConstructor
public class UserPreferenceController {

    private final IUserPreferenceService userPreferenceService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "获取用户偏好", description = "获取当前登录用户的偏好设置")
    @GetMapping
    public ApiResponse<UserPreferenceResponse> getUserPreference(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            UserPreferenceResponse response = userPreferenceService.getUserPreference(userId);
            return ApiResponse.success("获取成功", response);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(404, e.getMessage());
        } catch (Exception e) {
            log.error("获取用户偏好失败", e);
            return ApiResponse.error(500, "获取失败: " + e.getMessage());
        }
    }

    @Operation(summary = "更新用户偏好", description = "更新当前登录用户的偏好设置")
    @PutMapping
    public ApiResponse<UserPreferenceResponse> updateUserPreference(
            HttpServletRequest request,
            @RequestBody UserPreferenceRequest preferenceRequest) {
        try {
            Long userId = getUserIdFromToken(request);
            UserPreferenceResponse response = userPreferenceService.updateUserPreference(userId, preferenceRequest);
            return ApiResponse.success("更新成功", response);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("更新用户偏好失败", e);
            return ApiResponse.error(500, "更新失败: " + e.getMessage());
        }
    }

    @Operation(summary = "删除用户偏好", description = "删除当前登录用户的偏好设置")
    @DeleteMapping
    public ApiResponse<Void> deleteUserPreference(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            userPreferenceService.deleteUserPreference(userId);
            return ApiResponse.success("删除成功", null);
        } catch (Exception e) {
            log.error("删除用户偏好失败", e);
            return ApiResponse.error(500, "删除失败: " + e.getMessage());
        }
    }

    /**
     * 从请求中提取用户ID
     */
    private Long getUserIdFromToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("未认证或令牌无效");
        }

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Long userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            throw new IllegalArgumentException("令牌无效");
        }

        return userId;
    }
}
