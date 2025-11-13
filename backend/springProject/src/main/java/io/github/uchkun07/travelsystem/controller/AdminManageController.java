package io.github.uchkun07.travelsystem.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.uchkun07.travelsystem.annotation.OperationLog;
import io.github.uchkun07.travelsystem.annotation.RequireAdminPermission;
import io.github.uchkun07.travelsystem.dto.*;
import io.github.uchkun07.travelsystem.entity.*;
import io.github.uchkun07.travelsystem.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理端综合管理控制器
 * 整合管理员、角色、权限、角色权限绑定、管理员角色绑定、操作日志等相关功能
 */
@Slf4j
@Tag(name = "管理端综合管理", description = "管理员及相关资源的统一管理接口")
@RestController
@RequestMapping("/api/admin")
public class AdminManageController {

    @Autowired
    private IAdminService adminService;
    
    @Autowired
    private IAdminRoleService adminRoleService;
    
    @Autowired
    private IAdminPermissionService adminPermissionService;
    
    @Autowired
    private IRolePermissionService rolePermissionService;
    
    @Autowired
    private IAdminRoleRelationService adminRoleRelationService;
    
    @Autowired
    private IOperationLogService operationLogService;
    
    // ==================== 管理员登录登出 ====================
    
    @Operation(summary = "管理员登录", description = "管理员使用用户名和密码登录,返回包含权限的JWT令牌")
    @PostMapping("/login")
    public ApiResponse<AdminLoginResponse> login(@Validated @RequestBody AdminLoginRequest request) {
        try {
            AdminLoginResponse response = adminService.login(request);
            log.info("管理员登录成功: username={}, adminId={}", response.getUsername(), response.getAdminId());
            return ApiResponse.success("登录成功", response);
        } catch (IllegalArgumentException e) {
            log.warn("管理员登录失败: username={}, error={}", request.getUsername(), e.getMessage());
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("管理员登录失败", e);
            return ApiResponse.error(500, "登录失败，请稍后重试");
        }
    }

    @Operation(summary = "管理员登出", description = "管理员登出接口,将token加入黑名单")
    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestHeader("Authorization") String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            adminService.logout(token);
            log.info("管理员登出成功");
            return ApiResponse.success("登出成功", null);
        } catch (Exception e) {
            log.error("管理员登出失败", e);
            return ApiResponse.error(500, "登出失败");
        }
    }

    @Operation(summary = "获取权限列表", description = "获取当前登录管理员的权限编码列表")
    @GetMapping("/permissions")
    public ApiResponse<Map<String, Object>> getPermissions(@RequestHeader("Authorization") String token) {
        try {
            Map<String, Object> data = new HashMap<>();
            return ApiResponse.success("获取权限成功", data);
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }
    
    // ==================== 管理员管理 ====================
    
    @Operation(summary = "创建管理员")
    @PostMapping("/create")
    @RequireAdminPermission(value = {"ADMIN:CREATE", "SYSTEM:MANAGE"})
    @OperationLog(type = "创建", object = "管理员")
    public ApiResponse<Admin> createAdmin(@Validated @RequestBody AdminCreateRequest request) {
        try {
            Admin admin = adminService.createAdmin(request);
            admin.setPassword(null);
            admin.setPasswordSalt(null);
            admin.setPbkdf2Iterations(null);
            log.info("创建管理员成功: adminId={}", admin.getAdminId());
            return ApiResponse.success("创建成功", admin);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("创建管理员失败", e);
            return ApiResponse.error(500, "创建失败");
        }
    }

    @Operation(summary = "删除管理员")
    @DeleteMapping("/delete/{adminId}")
    @RequireAdminPermission(value = {"ADMIN:DELETE", "SYSTEM:MANAGE"})
    @OperationLog(type = "删除", object = "管理员")
    public ApiResponse<Void> deleteAdmin(@PathVariable Long adminId) {
        try {
            adminService.deleteAdmin(adminId);
            return ApiResponse.success("删除成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("删除管理员失败", e);
            return ApiResponse.error(500, "删除失败");
        }
    }

    @Operation(summary = "批量删除管理员")
    @DeleteMapping("/batch-delete")
    @RequireAdminPermission(value = {"ADMIN:DELETE", "SYSTEM:MANAGE"})
    @OperationLog(type = "批量删除", object = "管理员")
    public ApiResponse<Void> batchDeleteAdmins(@RequestBody List<Long> adminIds) {
        try {
            adminService.batchDeleteAdmins(adminIds);
            return ApiResponse.success("批量删除成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("批量删除管理员失败", e);
            return ApiResponse.error(500, "批量删除失败");
        }
    }

    @Operation(summary = "更新管理员")
    @PutMapping("/update")
    @RequireAdminPermission(value = {"ADMIN:UPDATE", "SYSTEM:MANAGE"})
    @OperationLog(type = "修改", object = "管理员")
    public ApiResponse<Admin> updateAdmin(@Validated @RequestBody AdminUpdateRequest request) {
        try {
            Admin admin = adminService.updateAdmin(request);
            admin.setPassword(null);
            admin.setPasswordSalt(null);
            admin.setPbkdf2Iterations(null);
            return ApiResponse.success("更新成功", admin);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("更新管理员失败", e);
            return ApiResponse.error(500, "更新失败");
        }
    }

    @Operation(summary = "修改密码")
    @PutMapping("/update-password")
    @RequireAdminPermission(value = {"ADMIN:UPDATE", "SYSTEM:MANAGE"})
    @OperationLog(type = "修改密码", object = "管理员")
    public ApiResponse<Void> updatePassword(@Validated @RequestBody AdminPasswordUpdateRequest request) {
        try {
            adminService.updatePassword(request);
            return ApiResponse.success("密码修改成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("修改管理员密码失败", e);
            return ApiResponse.error(500, "密码修改失败");
        }
    }

    @Operation(summary = "分页查询管理员")
    @GetMapping("/list")
    @RequireAdminPermission(value = {"ADMIN:VIEW", "SYSTEM:MANAGE"})
    public ApiResponse<PageResponse<Admin>> queryAdmins(AdminQueryRequest request) {
        try {
            Page<Admin> page = adminService.queryAdmins(request);
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

    @Operation(summary = "查询管理员详情")
    @GetMapping("/detail/{adminId}")
    @RequireAdminPermission(value = {"ADMIN:VIEW", "SYSTEM:MANAGE"})
    public ApiResponse<Admin> getAdminDetail(@PathVariable Long adminId) {
        try {
            Admin admin = adminService.getAdminById(adminId);
            return ApiResponse.success("查询成功", admin);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("查询管理员详情失败", e);
            return ApiResponse.error(500, "查询失败");
        }
    }
    
    // ==================== 角色管理 ====================
    
    @Operation(summary = "创建角色")
    @PostMapping("/role/create")
    @RequireAdminPermission(value = {"ROLE:CREATE", "SYSTEM:MANAGE"})
    @OperationLog(type = "创建", object = "角色")
    public ApiResponse<AdminRole> createRole(@Validated @RequestBody AdminRoleRequest request) {
        try {
            AdminRole role = adminRoleService.createRole(request);
            return ApiResponse.success("创建成功", role);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("创建角色失败", e);
            return ApiResponse.error(500, "创建失败");
        }
    }

    @Operation(summary = "删除角色")
    @DeleteMapping("/role/delete/{roleId}")
    @RequireAdminPermission(value = {"ROLE:DELETE", "SYSTEM:MANAGE"})
    @OperationLog(type = "删除", object = "角色")
    public ApiResponse<Void> deleteRole(@PathVariable Integer roleId) {
        try {
            adminRoleService.deleteRole(roleId);
            return ApiResponse.success("删除成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("删除角色失败", e);
            return ApiResponse.error(500, "删除失败");
        }
    }

    @Operation(summary = "批量删除角色")
    @DeleteMapping("/role/batch-delete")
    @RequireAdminPermission(value = {"ROLE:DELETE", "SYSTEM:MANAGE"})
    @OperationLog(type = "批量删除", object = "角色")
    public ApiResponse<Void> batchDeleteRoles(@RequestBody List<Integer> roleIds) {
        try {
            adminRoleService.batchDeleteRoles(roleIds);
            return ApiResponse.success("批量删除成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("批量删除角色失败", e);
            return ApiResponse.error(500, "批量删除失败");
        }
    }

    @Operation(summary = "修改角色")
    @PutMapping("/role/update")
    @RequireAdminPermission(value = {"ROLE:UPDATE", "SYSTEM:MANAGE"})
    @OperationLog(type = "修改", object = "角色")
    public ApiResponse<AdminRole> updateRole(@Validated @RequestBody AdminRoleRequest request) {
        try {
            AdminRole role = adminRoleService.updateRole(request);
            return ApiResponse.success("修改成功", role);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("修改角色失败", e);
            return ApiResponse.error(500, "修改失败");
        }
    }

    @Operation(summary = "分页查询角色")
    @GetMapping("/role/list")
    @RequireAdminPermission(value = {"ROLE:VIEW", "SYSTEM:MANAGE"})
    public ApiResponse<PageResponse<AdminRole>> queryRoles(
            @RequestParam(defaultValue = "1") Long pageNum,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) String roleName,
            @RequestParam(required = false) Integer status) {
        try {
            RoleQueryRequest request = RoleQueryRequest.builder()
                    .pageNum(pageNum).pageSize(pageSize).roleName(roleName).status(status).build();
            PageResponse<AdminRole> result = adminRoleService.queryRoles(request);
            return ApiResponse.success("查询成功", result);
        } catch (Exception e) {
            log.error("查询角色失败", e);
            return ApiResponse.error(500, "查询失败");
        }
    }

    @Operation(summary = "查询角色详情")
    @GetMapping("/role/detail/{roleId}")
    @RequireAdminPermission(value = {"ROLE:VIEW", "SYSTEM:MANAGE"})
    public ApiResponse<AdminRole> getRoleById(@PathVariable Integer roleId) {
        try {
            AdminRole role = adminRoleService.getRoleById(roleId);
            return ApiResponse.success("查询成功", role);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("查询角色详情失败", e);
            return ApiResponse.error(500, "查询失败");
        }
    }

    @Operation(summary = "获取所有角色")
    @GetMapping("/role/all")
    @RequireAdminPermission(value = {"ROLE:VIEW", "SYSTEM:MANAGE"})
    public ApiResponse<List<AdminRole>> getAllRoles() {
        try {
            List<AdminRole> roles = adminRoleService.getAllRoles();
            return ApiResponse.success("查询成功", roles);
        } catch (Exception e) {
            log.error("获取所有角色失败", e);
            return ApiResponse.error(500, "查询失败");
        }
    }
    
    // ==================== 权限管理 ====================
    
    @Operation(summary = "创建权限")
    @PostMapping("/permission/create")
    @RequireAdminPermission(value = {"PERMISSION:CREATE", "SYSTEM:MANAGE"})
    @OperationLog(type = "创建", object = "权限")
    public ApiResponse<AdminPermission> createPermission(@Validated @RequestBody AdminPermissionRequest request) {
        try {
            AdminPermission permission = adminPermissionService.createPermission(request);
            return ApiResponse.success("创建成功", permission);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("创建权限失败", e);
            return ApiResponse.error(500, "创建失败");
        }
    }

    @Operation(summary = "删除权限")
    @DeleteMapping("/permission/delete/{permissionId}")
    @RequireAdminPermission(value = {"PERMISSION:DELETE", "SYSTEM:MANAGE"})
    @OperationLog(type = "删除", object = "权限")
    public ApiResponse<Void> deletePermission(@PathVariable Integer permissionId) {
        try {
            adminPermissionService.deletePermission(permissionId);
            return ApiResponse.success("删除成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("删除权限失败", e);
            return ApiResponse.error(500, "删除失败");
        }
    }

    @Operation(summary = "批量删除权限")
    @DeleteMapping("/permission/batch-delete")
    @RequireAdminPermission(value = {"PERMISSION:DELETE", "SYSTEM:MANAGE"})
    @OperationLog(type = "批量删除", object = "权限")
    public ApiResponse<Void> batchDeletePermissions(@RequestBody List<Integer> permissionIds) {
        try {
            adminPermissionService.batchDeletePermissions(permissionIds);
            return ApiResponse.success("批量删除成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("批量删除权限失败", e);
            return ApiResponse.error(500, "批量删除失败");
        }
    }

    @Operation(summary = "修改权限")
    @PutMapping("/permission/update")
    @RequireAdminPermission(value = {"PERMISSION:UPDATE", "SYSTEM:MANAGE"})
    @OperationLog(type = "修改", object = "权限")
    public ApiResponse<AdminPermission> updatePermission(@Validated @RequestBody AdminPermissionRequest request) {
        try {
            AdminPermission permission = adminPermissionService.updatePermission(request);
            return ApiResponse.success("修改成功", permission);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("修改权限失败", e);
            return ApiResponse.error(500, "修改失败");
        }
    }

    @Operation(summary = "分页查询权限")
    @GetMapping("/permission/list")
    @RequireAdminPermission(value = {"PERMISSION:VIEW", "SYSTEM:MANAGE"})
    public ApiResponse<PageResponse<AdminPermission>> queryPermissions(
            @RequestParam(defaultValue = "1") Long pageNum,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) String permissionCode,
            @RequestParam(required = false) String permissionName,
            @RequestParam(required = false) String resourceType,
            @RequestParam(required = false) Integer isSensitive) {
        try {
            PermissionQueryRequest request = PermissionQueryRequest.builder()
                    .pageNum(pageNum).pageSize(pageSize).permissionCode(permissionCode)
                    .permissionName(permissionName).resourceType(resourceType).isSensitive(isSensitive).build();
            PageResponse<AdminPermission> result = adminPermissionService.queryPermissions(request);
            return ApiResponse.success("查询成功", result);
        } catch (Exception e) {
            log.error("查询权限失败", e);
            return ApiResponse.error(500, "查询失败");
        }
    }

    @Operation(summary = "查询权限详情")
    @GetMapping("/permission/detail/{permissionId}")
    @RequireAdminPermission(value = {"PERMISSION:VIEW", "SYSTEM:MANAGE"})
    public ApiResponse<AdminPermission> getPermissionById(@PathVariable Integer permissionId) {
        try {
            AdminPermission permission = adminPermissionService.getPermissionById(permissionId);
            return ApiResponse.success("查询成功", permission);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("查询权限详情失败", e);
            return ApiResponse.error(500, "查询失败");
        }
    }

    @Operation(summary = "获取所有权限")
    @GetMapping("/permission/all")
    @RequireAdminPermission(value = {"PERMISSION:VIEW", "SYSTEM:MANAGE"})
    public ApiResponse<List<AdminPermission>> getAllPermissions() {
        try {
            List<AdminPermission> permissions = adminPermissionService.getAllPermissions();
            return ApiResponse.success("查询成功", permissions);
        } catch (Exception e) {
            log.error("获取所有权限失败", e);
            return ApiResponse.error(500, "查询失败");
        }
    }
    
    // ==================== 角色权限绑定 ====================
    
    @Operation(summary = "绑定权限到角色")
    @PostMapping("/role-permission/bind")
    @RequireAdminPermission(value = {"ROLE_PERMISSION:BIND", "SYSTEM:MANAGE"})
    @OperationLog(type = "绑定", object = "角色权限")
    public ApiResponse<Void> bindPermissionsToRole(@Validated @RequestBody RolePermissionBindRequest request) {
        try {
            rolePermissionService.bindPermissionsToRole(request.getRoleId(), request.getPermissionIds());
            return ApiResponse.success("绑定成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("角色绑定权限失败", e);
            return ApiResponse.error(500, "绑定失败");
        }
    }

    @Operation(summary = "角色解绑权限")
    @PostMapping("/role-permission/unbind")
    @RequireAdminPermission(value = {"ROLE_PERMISSION:UNBIND", "SYSTEM:MANAGE"})
    @OperationLog(type = "解绑", object = "角色权限")
    public ApiResponse<Void> unbindPermissionsFromRole(@Validated @RequestBody RolePermissionBindRequest request) {
        try {
            rolePermissionService.unbindPermissionsFromRole(request.getRoleId(), request.getPermissionIds());
            return ApiResponse.success("解绑成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("角色解绑权限失败", e);
            return ApiResponse.error(500, "解绑失败");
        }
    }

    @Operation(summary = "角色解绑所有权限")
    @PostMapping("/role-permission/unbind-all/{roleId}")
    @RequireAdminPermission(value = {"ROLE_PERMISSION:UNBIND", "SYSTEM:MANAGE"})
    public ApiResponse<Void> unbindAllPermissionsFromRole(@PathVariable Integer roleId) {
        try {
            rolePermissionService.unbindAllPermissionsFromRole(roleId);
            return ApiResponse.success("解绑成功", null);
        } catch (Exception e) {
            log.error("角色解绑所有权限失败", e);
            return ApiResponse.error(500, "解绑失败");
        }
    }

    @Operation(summary = "查询角色的所有权限")
    @GetMapping("/role-permission/list/{roleId}")
    @RequireAdminPermission(value = {"ROLE_PERMISSION:VIEW", "SYSTEM:MANAGE"})
    public ApiResponse<List<AdminPermission>> getRolePermissions(@PathVariable Integer roleId) {
        try {
            List<AdminPermission> permissions = rolePermissionService.getRolePermissions(roleId);
            return ApiResponse.success("查询成功", permissions);
        } catch (Exception e) {
            log.error("查询角色权限失败", e);
            return ApiResponse.error(500, "查询失败");
        }
    }
    
    // ==================== 管理员角色绑定 ====================
    
    @Operation(summary = "绑定角色到管理员")
    @PostMapping("/admin-role/bind")
    @RequireAdminPermission(value = {"ADMIN_ROLE:BIND", "SYSTEM:MANAGE"})
    @OperationLog(type = "绑定", object = "管理员角色")
    public ApiResponse<Void> bindRolesToAdmin(@Validated @RequestBody AdminRoleBindRequest request) {
        try {
            adminRoleRelationService.bindRolesToAdmin(request.getAdminId(), request.getRoleIds());
            return ApiResponse.success("绑定成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("管理员绑定角色失败", e);
            return ApiResponse.error(500, "绑定失败");
        }
    }

    @Operation(summary = "管理员解绑角色")
    @PostMapping("/admin-role/unbind")
    @RequireAdminPermission(value = {"ADMIN_ROLE:UNBIND", "SYSTEM:MANAGE"})
    @OperationLog(type = "解绑", object = "管理员角色")
    public ApiResponse<Void> unbindRolesFromAdmin(@Validated @RequestBody AdminRoleBindRequest request) {
        try {
            adminRoleRelationService.unbindRolesFromAdmin(request.getAdminId(), request.getRoleIds());
            return ApiResponse.success("解绑成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("管理员解绑角色失败", e);
            return ApiResponse.error(500, "解绑失败");
        }
    }

    @Operation(summary = "管理员解绑所有角色")
    @PostMapping("/admin-role/unbind-all/{adminId}")
    @RequireAdminPermission(value = {"ADMIN_ROLE:UNBIND", "SYSTEM:MANAGE"})
    public ApiResponse<Void> unbindAllRolesFromAdmin(@PathVariable Long adminId) {
        try {
            adminRoleRelationService.unbindAllRolesFromAdmin(adminId);
            return ApiResponse.success("解绑成功", null);
        } catch (Exception e) {
            log.error("管理员解绑所有角色失败", e);
            return ApiResponse.error(500, "解绑失败");
        }
    }

    @Operation(summary = "查询管理员的所有角色")
    @GetMapping("/admin-role/list/{adminId}")
    @RequireAdminPermission(value = {"ADMIN_ROLE:VIEW", "SYSTEM:MANAGE"})
    public ApiResponse<List<AdminRole>> getAdminRoles(@PathVariable Long adminId) {
        try {
            List<AdminRole> roles = adminRoleRelationService.getAdminRoles(adminId);
            return ApiResponse.success("查询成功", roles);
        } catch (Exception e) {
            log.error("查询管理员角色失败", e);
            return ApiResponse.error(500, "查询失败");
        }
    }
    
    // ==================== 操作日志管理 ====================
    
    @Operation(summary = "删除操作日志")
    @DeleteMapping("/operation-log/delete/{operationLogId}")
    @RequireAdminPermission(value = {"OPERATION_LOG:DELETE", "SYSTEM:MANAGE"})
    @OperationLog(type = "删除", object = "操作日志")
    public ApiResponse<Void> deleteLog(@PathVariable Long operationLogId) {
        try {
            operationLogService.deleteLog(operationLogId);
            return ApiResponse.success("删除成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("删除操作日志失败", e);
            return ApiResponse.error(500, "删除失败");
        }
    }

    @Operation(summary = "批量删除操作日志")
    @DeleteMapping("/operation-log/batch-delete")
    @RequireAdminPermission(value = {"OPERATION_LOG:DELETE", "SYSTEM:MANAGE"})
    @OperationLog(type = "批量删除", object = "操作日志")
    public ApiResponse<Void> batchDeleteLogs(@RequestBody List<Long> operationLogIds) {
        try {
            operationLogService.batchDeleteLogs(operationLogIds);
            return ApiResponse.success("批量删除成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("批量删除操作日志失败", e);
            return ApiResponse.error(500, "批量删除失败");
        }
    }

    @Operation(summary = "分页查询操作日志")
    @GetMapping("/operation-log/list")
    @RequireAdminPermission(value = {"OPERATION_LOG:VIEW", "SYSTEM:MANAGE"})
    public ApiResponse<PageResponse<io.github.uchkun07.travelsystem.entity.OperationLog>> queryLogs(
            @RequestParam(defaultValue = "1") Long pageNum,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) Long adminId,
            @RequestParam(required = false) String operationType,
            @RequestParam(required = false) String operationObject,
            @RequestParam(required = false) Long objectId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        try {
            OperationLogQueryRequest request = OperationLogQueryRequest.builder()
                    .pageNum(pageNum).pageSize(pageSize).adminId(adminId).operationType(operationType)
                    .operationObject(operationObject).objectId(objectId).startTime(startTime).endTime(endTime).build();
            PageResponse<io.github.uchkun07.travelsystem.entity.OperationLog> result = operationLogService.queryLogs(request);
            return ApiResponse.success("查询成功", result);
        } catch (Exception e) {
            log.error("查询操作日志失败", e);
            return ApiResponse.error(500, "查询失败");
        }
    }
}
