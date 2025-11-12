package io.github.uchkun07.travelsystem.controller;

import io.github.uchkun07.travelsystem.annotation.RequireAdminPermission;
import io.github.uchkun07.travelsystem.dto.ApiResponse;
import io.github.uchkun07.travelsystem.dto.RolePermissionBindRequest;
import io.github.uchkun07.travelsystem.entity.AdminPermission;
import io.github.uchkun07.travelsystem.service.IRolePermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "角色权限绑定", description = "角色与权限的绑定和解绑接口")
@RestController
@RequestMapping("/admin/role-permission")
@RequireAdminPermission
public class RolePermissionController {

    @Autowired
    private IRolePermissionService rolePermissionService;

    @Operation(summary = "绑定权限到角色")
    @PostMapping("/bind")
    @RequireAdminPermission(value = {"ROLE_PERMISSION:BIND", "SYSTEM:MANAGE"})
    @io.github.uchkun07.travelsystem.annotation.OperationLog(type = "绑定", object = "角色权限")
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
    @PostMapping("/unbind")
    @RequireAdminPermission(value = {"ROLE_PERMISSION:UNBIND", "SYSTEM:MANAGE"})
    @io.github.uchkun07.travelsystem.annotation.OperationLog(type = "解绑", object = "角色权限")
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
    @PostMapping("/unbind-all/{roleId}")
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
    @GetMapping("/list/{roleId}")
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
}
