package io.github.uchkun07.travelsystem.controller;

import io.github.uchkun07.travelsystem.dto.ApiResponse;
import io.github.uchkun07.travelsystem.dto.LoginRequest;
import io.github.uchkun07.travelsystem.dto.LoginResponse;
import io.github.uchkun07.travelsystem.service.IAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/admin")
@Tag(name = "管理员接口", description = "管理员相关的API接口")
public class AdminController {

    @Autowired
    private IAdminService adminService;

    /**
     * 管理员登录
     *
     * @param loginRequest 登录请求
     * @return 包含token和用户信息的响应
     */
    @PostMapping("/login")
    @Operation(summary = "管理员登录", description = "管理员通过用户名和密码登录系统")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            log.info("管理员登录请求: username={}", loginRequest.getUsername());
            
            // 调用服务层登录方法
            LoginResponse loginResponse = adminService.login(loginRequest);
            
            log.info("管理员登录成功: username={}", loginRequest.getUsername());
            return ApiResponse.success("登录成功", loginResponse);
            
        } catch (IllegalArgumentException e) {
            log.warn("管理员登录失败: {}", e.getMessage());
            return ApiResponse.error(400, e.getMessage());
            
        } catch (IllegalStateException e) {
            log.warn("管理员账号状态异常: {}", e.getMessage());
            return ApiResponse.error(403, e.getMessage());
            
        } catch (Exception e) {
            log.error("管理员登录异常", e);
            return ApiResponse.error("登录失败，请稍后重试");
        }
    }

    /**
     * 管理员登出（可选实现）
     *
     * @return 响应
     */
    @PostMapping("/logout")
    @Operation(summary = "管理员登出", description = "管理员退出登录")
    public ApiResponse<Void> logout() {
        // JWT是无状态的，登出可以由前端清除token实现
        // 如果需要服务端管理token黑名单，可以在这里实现
        log.info("管理员登出");
        return ApiResponse.success("登出成功", null);
    }
}
