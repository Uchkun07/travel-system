package io.github.uchkun07.travelsystem.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.uchkun07.travelsystem.annotation.RequireAdminPermission;
import io.github.uchkun07.travelsystem.dto.*;
import io.github.uchkun07.travelsystem.entity.Admin;
import io.github.uchkun07.travelsystem.service.IAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员控制器
 */
@Slf4j
@Tag(name = "管理员管理", description = "管理员登录、权限等相关接口")
@RestController
@RequestMapping("/api/admin")
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
     * 管理员登出
     * 将token加入黑名单实现登出功能
     */
    @Operation(summary = "管理员登出", description = "管理员登出接口,将token加入黑名单")
    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestHeader("Authorization") String token) {
        try {
            // 去掉 Bearer 前缀
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            // 调用服务层登出方法
            adminService.logout(token);
            
            log.info("管理员登出成功: token={}", token);
            return ApiResponse.success("登出成功", null);
        } catch (IllegalArgumentException e) {
            log.warn("管理员登出失败: token={}, error={}", token, e.getMessage());
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("管理员登出失败: token={}, error={}", token, e.getMessage());
            return ApiResponse.error(500, "登出失败，请稍后重试");
        }
    }

    /**
     * 获取当前登录管理员的权限列表
     */
    @Operation(summary = "获取权限列表", description = "获取当前登录管理员的权限编码列表")
    @GetMapping("/permissions")
    public ApiResponse<Map<String, Object>> getPermissions(@RequestHeader("Authorization") String token) {
        try {
            // 这里可以从token中直接解析权限,或者从数据库重新查询
            // 建议从token中解析,避免额外的数据库查询
            Map<String, Object> data = new HashMap<>();
            // 实际项目中可以通过拦截器注入当前管理员信息
            
            return ApiResponse.success("获取权限成功", data);
            
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }

    /**
     * 创建管理员
     */
    @Operation(summary = "创建管理员", description = "创建新的管理员账号")
    @PostMapping("/create")
    @RequireAdminPermission(value = {"ADMIN:CREATE", "SYSTEM:MANAGE"})
    @io.github.uchkun07.travelsystem.annotation.OperationLog(type = "创建", object = "管理员")
    public ApiResponse<Admin> createAdmin(@Validated @RequestBody AdminCreateRequest request) {
        try {
            Admin admin = adminService.createAdmin(request);
            // 清空密码等敏感信息
            admin.setPassword(null);
            admin.setPasswordSalt(null);
            admin.setPbkdf2Iterations(null);
            
            log.info("创建管理员成功: adminId={}, username={}", admin.getAdminId(), admin.getUsername());
            return ApiResponse.success("创建成功", admin);
        } catch (IllegalArgumentException e) {
            log.warn("创建管理员失败: {}", e.getMessage());
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("创建管理员失败", e);
            return ApiResponse.error(500, "创建失败");
        }
    }

    /**
     * 删除管理员
     */
    @Operation(summary = "删除管理员", description = "根据ID删除管理员")
    @DeleteMapping("/delete/{adminId}")
    @RequireAdminPermission(value = {"ADMIN:DELETE", "SYSTEM:MANAGE"})
    @io.github.uchkun07.travelsystem.annotation.OperationLog(type = "删除", object = "管理员")
    public ApiResponse<Void> deleteAdmin(@PathVariable Long adminId) {
        try {
            adminService.deleteAdmin(adminId);
            log.info("删除管理员成功: adminId={}", adminId);
            return ApiResponse.success("删除成功", null);
        } catch (IllegalArgumentException e) {
            log.warn("删除管理员失败: {}", e.getMessage());
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("删除管理员失败", e);
            return ApiResponse.error(500, "删除失败");
        }
    }

    /**
     * 批量删除管理员
     */
    @Operation(summary = "批量删除管理员", description = "批量删除管理员")
    @DeleteMapping("/batch-delete")
    @RequireAdminPermission(value = {"ADMIN:DELETE", "SYSTEM:MANAGE"})
    @io.github.uchkun07.travelsystem.annotation.OperationLog(type = "批量删除", object = "管理员")
    public ApiResponse<Void> batchDeleteAdmins(@RequestBody List<Long> adminIds) {
        try {
            adminService.batchDeleteAdmins(adminIds);
            log.info("批量删除管理员成功: count={}", adminIds.size());
            return ApiResponse.success("批量删除成功", null);
        } catch (IllegalArgumentException e) {
            log.warn("批量删除管理员失败: {}", e.getMessage());
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("批量删除管理员失败", e);
            return ApiResponse.error(500, "批量删除失败");
        }
    }

    /**
     * 更新管理员信息
     */
    @Operation(summary = "更新管理员", description = "更新管理员基本信息")
    @PutMapping("/update")
    @RequireAdminPermission(value = {"ADMIN:UPDATE", "SYSTEM:MANAGE"})
    @io.github.uchkun07.travelsystem.annotation.OperationLog(type = "修改", object = "管理员")
    public ApiResponse<Admin> updateAdmin(@Validated @RequestBody AdminUpdateRequest request) {
        try {
            Admin admin = adminService.updateAdmin(request);
            // 清空密码等敏感信息
            admin.setPassword(null);
            admin.setPasswordSalt(null);
            admin.setPbkdf2Iterations(null);
            
            log.info("更新管理员成功: adminId={}", admin.getAdminId());
            return ApiResponse.success("更新成功", admin);
        } catch (IllegalArgumentException e) {
            log.warn("更新管理员失败: {}", e.getMessage());
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("更新管理员失败", e);
            return ApiResponse.error(500, "更新失败");
        }
    }

    /**
     * 修改管理员密码
     */
    @Operation(summary = "修改密码", description = "修改管理员密码")
    @PutMapping("/update-password")
    @RequireAdminPermission(value = {"ADMIN:UPDATE", "SYSTEM:MANAGE"})
    @io.github.uchkun07.travelsystem.annotation.OperationLog(type = "修改密码", object = "管理员")
    public ApiResponse<Void> updatePassword(@Validated @RequestBody AdminPasswordUpdateRequest request) {
        try {
            adminService.updatePassword(request);
            log.info("修改管理员密码成功: adminId={}", request.getAdminId());
            return ApiResponse.success("密码修改成功", null);
        } catch (IllegalArgumentException e) {
            log.warn("修改管理员密码失败: {}", e.getMessage());
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("修改管理员密码失败", e);
            return ApiResponse.error(500, "密码修改失败");
        }
    }

    /**
     * 分页查询管理员
     */
    @Operation(summary = "分页查询管理员", description = "分页查询管理员列表,支持多条件筛选")
    @GetMapping("/list")
    @RequireAdminPermission(value = {"ADMIN:VIEW", "SYSTEM:MANAGE"})
    public ApiResponse<PageResponse<Admin>> queryAdmins(AdminQueryRequest request) {
        try {
            Page<Admin> page = adminService.queryAdmins(request);
            
            // 清空所有管理员的密码等敏感信息
            page.getRecords().forEach(admin -> {
                admin.setPassword(null);
                admin.setPasswordSalt(null);
                admin.setPbkdf2Iterations(null);
            });
            
            PageResponse<Admin> response = PageResponse.<Admin>builder()
                    .records(page.getRecords())
                    .total(page.getTotal())
                    .pageNum(page.getCurrent())
                    .pageSize(page.getSize())
                    .totalPages(page.getPages())
                    .build();
            
            return ApiResponse.success("查询成功", response);
        } catch (Exception e) {
            log.error("查询管理员列表失败", e);
            return ApiResponse.error(500, "查询失败");
        }
    }

    /**
     * 查询管理员详情
     */
    @Operation(summary = "查询管理员详情", description = "根据ID查询管理员详细信息")
    @GetMapping("/detail/{adminId}")
    @RequireAdminPermission(value = {"ADMIN:VIEW", "SYSTEM:MANAGE"})
    public ApiResponse<Admin> getAdminDetail(@PathVariable Long adminId) {
        try {
            Admin admin = adminService.getAdminById(adminId);
            log.info("查询管理员详情成功: adminId={}", adminId);
            return ApiResponse.success("查询成功", admin);
        } catch (IllegalArgumentException e) {
            log.warn("查询管理员详情失败: {}", e.getMessage());
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("查询管理员详情失败", e);
            return ApiResponse.error(500, "查询失败");
        }
    }
}
