package io.github.uchkun07.travelsystem.controller;

import io.github.uchkun07.travelsystem.dto.AdminLoginRequest;
import io.github.uchkun07.travelsystem.dto.AdminLoginResponse;
import io.github.uchkun07.travelsystem.dto.ApiResponse;
import io.github.uchkun07.travelsystem.service.IAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理员控制器
 */
@Slf4j
@Tag(name = "管理员管理", description = "管理员登录、权限等相关接口")
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private IAdminService adminService;

    /**
     * 管理员登录
     */
    @Operation(summary = "管理员登录", description = "管理员使用用户名和密码登录,返回包含权限的JWT令牌")
    @PostMapping("/login")
    public ApiResponse<AdminLoginResponse> login(@Validated @RequestBody AdminLoginRequest request) {
        try {
            AdminLoginResponse response = adminService.login(request);
            
            log.info("管理员登录成功: username={}, adminId={}, permissions={}", 
                    response.getUsername(), response.getAdminId(), response.getPermissions());
            
            return ApiResponse.success("登录成功", response);
            
        } catch (IllegalArgumentException e) {
            log.warn("管理员登录失败: username={}, error={}", request.getUsername(), e.getMessage());
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("管理员登录失败: username={}, error={}", request.getUsername(), e.getMessage());
            return ApiResponse.error(500, "登录失败，请稍后重试");
        }
    }

    /**
     * 获取当前登录管理员的权限列表
     */
    @Operation(summary = "获取权限列表", description = "获取当前登录管理员的权限编码列表")
    @GetMapping("/permissions")
    public ApiResponse<Map<String, Object>> getPermissions(@RequestHeader("Authorization") String token) {
        try {
            // 这里可以从token中直接解析权限，或者从数据库重新查询
            // 建议从token中解析，避免额外的数据库查询
            Map<String, Object> data = new HashMap<>();
            // 实际项目中可以通过拦截器注入当前管理员信息
            
            return ApiResponse.success("获取权限成功", data);
            
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }
}
