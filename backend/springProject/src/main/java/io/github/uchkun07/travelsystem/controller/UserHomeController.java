package io.github.uchkun07.travelsystem.controller;

import io.github.uchkun07.travelsystem.dto.*;
import io.github.uchkun07.travelsystem.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户C端控制器
 */
@Slf4j
@Tag(name = "用户(C端)", description = "用户登录注册等前台接口")
@RestController
@RequestMapping("/api/user")
public class UserHomeController {

    @Autowired
    private IUserService userService;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public ApiResponse<UserLoginResponse> register(@Validated @RequestBody UserRegisterRequest request) {
        try {
            UserLoginResponse response = userService.register(request);
            return ApiResponse.success("注册成功", response);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("用户注册失败", e);
            return ApiResponse.error(500, "注册失败: " + e.getMessage());
        }
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public ApiResponse<UserLoginResponse> login(@Validated @RequestBody UserLoginRequest request) {
        try {
            UserLoginResponse response = userService.login(request);
            return ApiResponse.success("登录成功", response);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(401, e.getMessage());
        } catch (Exception e) {
            log.error("用户登录失败", e);
            return ApiResponse.error(500, "登录失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取用户信息")
    @GetMapping("/info")
    public ApiResponse<UserInfoResponse> getUserInfo(@RequestHeader("Authorization") String token) {
        try {
            UserInfoResponse response = userService.getUserInfo(token);
            return ApiResponse.success("获取成功", response);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(401, e.getMessage());
        } catch (Exception e) {
            log.error("获取用户信息失败", e);
            return ApiResponse.error(500, "获取失败: " + e.getMessage());
        }
    }

    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestHeader("Authorization") String token) {
        try {
            userService.logout(token);
            return ApiResponse.success("登出成功", null);
        } catch (Exception e) {
            log.error("用户登出失败", e);
            return ApiResponse.error(500, "登出失败: " + e.getMessage());
        }
    }

    @Operation(summary = "检查用户名是否可用")
    @GetMapping("/check/username")
    public ApiResponse<Boolean> checkUsername(@RequestParam String username) {
        try {
            boolean available = userService.checkUsernameAvailable(username);
            return ApiResponse.success(available ? "用户名可用" : "用户名已存在", available);
        } catch (Exception e) {
            log.error("检查用户名失败", e);
            return ApiResponse.error(500, "检查失败: " + e.getMessage());
        }
    }

    @Operation(summary = "检查邮箱是否可用")
    @GetMapping("/check/email")
    public ApiResponse<Boolean> checkEmail(@RequestParam String email) {
        try {
            boolean available = userService.checkEmailAvailable(email);
            return ApiResponse.success(available ? "邮箱可用" : "邮箱已被使用", available);
        } catch (Exception e) {
            log.error("检查邮箱失败", e);
            return ApiResponse.error(500, "检查失败: " + e.getMessage());
        }
    }
}
