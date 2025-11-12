package io.github.uchkun07.travelsystem.controller;

import io.github.uchkun07.travelsystem.annotation.RequireAdminPermission;
import io.github.uchkun07.travelsystem.dto.*;
import io.github.uchkun07.travelsystem.entity.AdminPermission;
import io.github.uchkun07.travelsystem.service.IAdminPermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "权限管理", description = "管理员权限的增删改查接口")
@RestController
@RequestMapping("/admin/permission")
@RequireAdminPermission
public class AdminPermissionController {

    @Autowired
    private IAdminPermissionService permissionService;

    @Operation(summary = "创建权限")
    @PostMapping("/create")
    @RequireAdminPermission(value = {"PERMISSION:CREATE", "SYSTEM:MANAGE"})
    @io.github.uchkun07.travelsystem.annotation.OperationLog(type = "创建", object = "权限")
    public ApiResponse<AdminPermission> createPermission(@Validated @RequestBody AdminPermissionRequest request) {
        try {
            AdminPermission permission = permissionService.createPermission(request);
            return ApiResponse.success("创建成功", permission);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("创建权限失败", e);
            return ApiResponse.error(500, "创建失败");
        }
    }

    @Operation(summary = "删除权限")
    @DeleteMapping("/delete/{permissionId}")
    @RequireAdminPermission(value = {"PERMISSION:DELETE", "SYSTEM:MANAGE"})
    @io.github.uchkun07.travelsystem.annotation.OperationLog(type = "删除", object = "权限")
    public ApiResponse<Void> deletePermission(@PathVariable Integer permissionId) {
        try {
            permissionService.deletePermission(permissionId);
            return ApiResponse.success("删除成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("删除权限失败", e);
            return ApiResponse.error(500, "删除失败");
        }
    }

    @Operation(summary = "批量删除权限")
    @DeleteMapping("/batch-delete")
    @RequireAdminPermission(value = {"PERMISSION:DELETE", "SYSTEM:MANAGE"})
    @io.github.uchkun07.travelsystem.annotation.OperationLog(type = "批量删除", object = "权限")
    public ApiResponse<Void> batchDeletePermissions(@RequestBody List<Integer> permissionIds) {
        try {
            permissionService.batchDeletePermissions(permissionIds);
            return ApiResponse.success("批量删除成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("批量删除权限失败", e);
            return ApiResponse.error(500, "批量删除失败");
        }
    }

    @Operation(summary = "修改权限")
    @PutMapping("/update")
    @RequireAdminPermission(value = {"PERMISSION:UPDATE", "SYSTEM:MANAGE"})
    @io.github.uchkun07.travelsystem.annotation.OperationLog(type = "修改", object = "权限")
    public ApiResponse<AdminPermission> updatePermission(@Validated @RequestBody AdminPermissionRequest request) {
        try {
            AdminPermission permission = permissionService.updatePermission(request);
            return ApiResponse.success("修改成功", permission);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("修改权限失败", e);
            return ApiResponse.error(500, "修改失败");
        }
    }

    @Operation(summary = "分页查询权限")
    @GetMapping("/list")
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
                    .pageNum(pageNum)
                    .pageSize(pageSize)
                    .permissionCode(permissionCode)
                    .permissionName(permissionName)
                    .resourceType(resourceType)
                    .isSensitive(isSensitive)
                    .build();
            PageResponse<AdminPermission> result = permissionService.queryPermissions(request);
            return ApiResponse.success("查询成功", result);
        } catch (Exception e) {
            log.error("查询权限失败", e);
            return ApiResponse.error(500, "查询失败");
        }
    }

    @Operation(summary = "查询权限详情")
    @GetMapping("/detail/{permissionId}")
    @RequireAdminPermission(value = {"PERMISSION:VIEW", "SYSTEM:MANAGE"})
    public ApiResponse<AdminPermission> getPermissionById(@PathVariable Integer permissionId) {
        try {
            AdminPermission permission = permissionService.getPermissionById(permissionId);
            return ApiResponse.success("查询成功", permission);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("查询权限详情失败", e);
            return ApiResponse.error(500, "查询失败");
        }
    }

    @Operation(summary = "获取所有权限")
    @GetMapping("/all")
    @RequireAdminPermission(value = {"PERMISSION:VIEW", "SYSTEM:MANAGE"})
    public ApiResponse<List<AdminPermission>> getAllPermissions() {
        try {
            List<AdminPermission> permissions = permissionService.getAllPermissions();
            return ApiResponse.success("查询成功", permissions);
        } catch (Exception e) {
            log.error("获取所有权限失败", e);
            return ApiResponse.error(500, "查询失败");
        }
    }
}
