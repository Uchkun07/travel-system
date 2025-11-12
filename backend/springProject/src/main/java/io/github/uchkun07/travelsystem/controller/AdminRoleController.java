package io.github.uchkun07.travelsystem.controller;

import io.github.uchkun07.travelsystem.annotation.RequireAdminPermission;
import io.github.uchkun07.travelsystem.dto.*;
import io.github.uchkun07.travelsystem.entity.AdminRole;
import io.github.uchkun07.travelsystem.service.IAdminRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "角色管理", description = "管理员角色的增删改查接口")
@RestController
@RequestMapping("/admin/role")
@RequireAdminPermission
public class AdminRoleController {

    @Autowired
    private IAdminRoleService roleService;

    @Operation(summary = "创建角色")
    @PostMapping("/create")
    @RequireAdminPermission(value = {"ROLE:CREATE", "SYSTEM:MANAGE"})
    @io.github.uchkun07.travelsystem.annotation.OperationLog(type = "创建", object = "角色")
    public ApiResponse<AdminRole> createRole(@Validated @RequestBody AdminRoleRequest request) {
        try {
            AdminRole role = roleService.createRole(request);
            return ApiResponse.success("创建成功", role);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("创建角色失败", e);
            return ApiResponse.error(500, "创建失败");
        }
    }

    @Operation(summary = "删除角色")
    @DeleteMapping("/delete/{roleId}")
    @RequireAdminPermission(value = {"ROLE:DELETE", "SYSTEM:MANAGE"})
    @io.github.uchkun07.travelsystem.annotation.OperationLog(type = "删除", object = "角色")
    public ApiResponse<Void> deleteRole(@PathVariable Integer roleId) {
        try {
            roleService.deleteRole(roleId);
            return ApiResponse.success("删除成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("删除角色失败", e);
            return ApiResponse.error(500, "删除失败");
        }
    }

    @Operation(summary = "批量删除角色")
    @DeleteMapping("/batch-delete")
    @RequireAdminPermission(value = {"ROLE:DELETE", "SYSTEM:MANAGE"})
    @io.github.uchkun07.travelsystem.annotation.OperationLog(type = "批量删除", object = "角色")
    public ApiResponse<Void> batchDeleteRoles(@RequestBody List<Integer> roleIds) {
        try {
            roleService.batchDeleteRoles(roleIds);
            return ApiResponse.success("批量删除成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("批量删除角色失败", e);
            return ApiResponse.error(500, "批量删除失败");
        }
    }

    @Operation(summary = "修改角色")
    @PutMapping("/update")
    @RequireAdminPermission(value = {"ROLE:UPDATE", "SYSTEM:MANAGE"})
    @io.github.uchkun07.travelsystem.annotation.OperationLog(type = "修改", object = "角色")
    public ApiResponse<AdminRole> updateRole(@Validated @RequestBody AdminRoleRequest request) {
        try {
            AdminRole role = roleService.updateRole(request);
            return ApiResponse.success("修改成功", role);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("修改角色失败", e);
            return ApiResponse.error(500, "修改失败");
        }
    }

    @Operation(summary = "分页查询角色")
    @GetMapping("/list")
    @RequireAdminPermission(value = {"ROLE:VIEW", "SYSTEM:MANAGE"})
    public ApiResponse<PageResponse<AdminRole>> queryRoles(
            @RequestParam(defaultValue = "1") Long pageNum,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) String roleName,
            @RequestParam(required = false) Integer status) {
        try {
            RoleQueryRequest request = RoleQueryRequest.builder()
                    .pageNum(pageNum)
                    .pageSize(pageSize)
                    .roleName(roleName)
                    .status(status)
                    .build();
            PageResponse<AdminRole> result = roleService.queryRoles(request);
            return ApiResponse.success("查询成功", result);
        } catch (Exception e) {
            log.error("查询角色失败", e);
            return ApiResponse.error(500, "查询失败");
        }
    }

    @Operation(summary = "查询角色详情")
    @GetMapping("/detail/{roleId}")
    @RequireAdminPermission(value = {"ROLE:VIEW", "SYSTEM:MANAGE"})
    public ApiResponse<AdminRole> getRoleById(@PathVariable Integer roleId) {
        try {
            AdminRole role = roleService.getRoleById(roleId);
            return ApiResponse.success("查询成功", role);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("查询角色详情失败", e);
            return ApiResponse.error(500, "查询失败");
        }
    }

    @Operation(summary = "获取所有角色")
    @GetMapping("/all")
    @RequireAdminPermission(value = {"ROLE:VIEW", "SYSTEM:MANAGE"})
    public ApiResponse<List<AdminRole>> getAllRoles() {
        try {
            List<AdminRole> roles = roleService.getAllRoles();
            return ApiResponse.success("查询成功", roles);
        } catch (Exception e) {
            log.error("获取所有角色失败", e);
            return ApiResponse.error(500, "查询失败");
        }
    }
}
