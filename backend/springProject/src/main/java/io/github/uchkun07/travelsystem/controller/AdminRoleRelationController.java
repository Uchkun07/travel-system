package io.github.uchkun07.travelsystem.controller;

import io.github.uchkun07.travelsystem.annotation.RequireAdminPermission;
import io.github.uchkun07.travelsystem.dto.AdminRoleBindRequest;
import io.github.uchkun07.travelsystem.dto.ApiResponse;
import io.github.uchkun07.travelsystem.entity.AdminRole;
import io.github.uchkun07.travelsystem.service.IAdminRoleRelationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "管理员角色绑定", description = "管理员与角色的绑定和解绑接口")
@RestController
@RequestMapping("/admin/admin-role")
@RequireAdminPermission
public class AdminRoleRelationController {

    @Autowired
    private IAdminRoleRelationService adminRoleRelationService;

    @Operation(summary = "绑定角色到管理员")
    @PostMapping("/bind")
    @RequireAdminPermission(value = {"ADMIN_ROLE:BIND", "SYSTEM:MANAGE"})
    @io.github.uchkun07.travelsystem.annotation.OperationLog(type = "绑定", object = "管理员角色")
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
    @PostMapping("/unbind")
    @RequireAdminPermission(value = {"ADMIN_ROLE:UNBIND", "SYSTEM:MANAGE"})
    @io.github.uchkun07.travelsystem.annotation.OperationLog(type = "解绑", object = "管理员角色")
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
    @PostMapping("/unbind-all/{adminId}")
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
    @GetMapping("/list/{adminId}")
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
}
