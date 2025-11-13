package io.github.uchkun07.travelsystem.controller;

import io.github.uchkun07.travelsystem.annotation.OperationLog;
import io.github.uchkun07.travelsystem.annotation.RequireAdminPermission;
import io.github.uchkun07.travelsystem.dto.*;
import io.github.uchkun07.travelsystem.entity.UserCountTable;
import io.github.uchkun07.travelsystem.entity.UserTagDict;
import io.github.uchkun07.travelsystem.service.IUserAdminService;
import io.github.uchkun07.travelsystem.service.IUserTagDictService;
import io.github.uchkun07.travelsystem.service.IUserTagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理控制器（管理员侧）
 * 包含用户标签字典管理、用户标签绑定、用户列表查询等功能
 */
@Slf4j
@Tag(name = "用户管理(管理端)", description = "管理员侧用户及用户标签管理接口")
@RestController
@RequestMapping("/api/admin")
@RequireAdminPermission
public class UserAdminController {

    @Autowired
    private IUserTagDictService userTagDictService;

    @Autowired
    private IUserTagService userTagService;

    @Autowired
    private IUserAdminService userAdminService;

    // ==================== 用户标签字典管理 ====================

    @Operation(summary = "创建用户标签字典")
    @PostMapping("/user/tag-dict/create")
    @RequireAdminPermission(value = {"USER_TAG:CREATE", "SYSTEM:MANAGE"})
    @OperationLog(type = "创建", object = "用户标签字典")
    public ApiResponse<UserTagDict> createTagDict(@Validated @RequestBody UserTagDictRequest request) {
        try {
            UserTagDict tagDict = userTagDictService.createTagDict(request);
            return ApiResponse.success("创建成功", tagDict);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("创建用户标签字典失败", e);
            return ApiResponse.error(500, "创建失败");
        }
    }

    @Operation(summary = "删除用户标签字典")
    @DeleteMapping("/user/tag-dict/delete/{tagDictId}")
    @RequireAdminPermission(value = {"USER_TAG:DELETE", "SYSTEM:MANAGE"})
    @OperationLog(type = "删除", object = "用户标签字典")
    public ApiResponse<Void> deleteTagDict(@PathVariable Integer tagDictId) {
        try {
            userTagDictService.deleteTagDict(tagDictId);
            return ApiResponse.success("删除成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("删除用户标签字典失败", e);
            return ApiResponse.error(500, "删除失败");
        }
    }

    @Operation(summary = "批量删除用户标签字典")
    @DeleteMapping("/user/tag-dict/batch-delete")
    @RequireAdminPermission(value = {"USER_TAG:DELETE", "SYSTEM:MANAGE"})
    @OperationLog(type = "批量删除", object = "用户标签字典")
    public ApiResponse<Void> batchDeleteTagDicts(@RequestBody List<Integer> tagDictIds) {
        try {
            userTagDictService.batchDeleteTagDicts(tagDictIds);
            return ApiResponse.success("批量删除成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("批量删除用户标签字典失败", e);
            return ApiResponse.error(500, "批量删除失败");
        }
    }

    @Operation(summary = "修改用户标签字典")
    @PutMapping("/user/tag-dict/update")
    @RequireAdminPermission(value = {"USER_TAG:UPDATE", "SYSTEM:MANAGE"})
    @OperationLog(type = "修改", object = "用户标签字典")
    public ApiResponse<UserTagDict> updateTagDict(@Validated @RequestBody UserTagDictRequest request) {
        try {
            UserTagDict tagDict = userTagDictService.updateTagDict(request);
            return ApiResponse.success("修改成功", tagDict);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("修改用户标签字典失败", e);
            return ApiResponse.error(500, "修改失败");
        }
    }

    @Operation(summary = "分页查询用户标签字典")
    @GetMapping("/user/tag-dict/list")
    @RequireAdminPermission(value = {"USER_TAG:VIEW", "SYSTEM:MANAGE"})
    public ApiResponse<PageResponse<UserTagDict>> queryTagDicts(
            @RequestParam(defaultValue = "1") Long pageNum,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) String tagName,
            @RequestParam(required = false) String tagCode,
            @RequestParam(required = false) Integer tagLevel,
            @RequestParam(required = false) Integer status) {
        try {
            UserTagDictQueryRequest request = UserTagDictQueryRequest.builder()
                    .pageNum(pageNum)
                    .pageSize(pageSize)
                    .tagName(tagName)
                    .tagCode(tagCode)
                    .tagLevel(tagLevel)
                    .status(status)
                    .build();
            PageResponse<UserTagDict> result = userTagDictService.queryTagDicts(request);
            return ApiResponse.success("查询成功", result);
        } catch (Exception e) {
            log.error("查询用户标签字典失败", e);
            return ApiResponse.error(500, "查询失败");
        }
    }

    @Operation(summary = "查询用户标签字典详情")
    @GetMapping("/user/tag-dict/detail/{tagDictId}")
    @RequireAdminPermission(value = {"USER_TAG:VIEW", "SYSTEM:MANAGE"})
    public ApiResponse<UserTagDict> getTagDictById(@PathVariable Integer tagDictId) {
        try {
            UserTagDict tagDict = userTagDictService.getTagDictById(tagDictId);
            return ApiResponse.success("查询成功", tagDict);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("查询用户标签字典详情失败", e);
            return ApiResponse.error(500, "查询失败");
        }
    }

    @Operation(summary = "获取所有启用的标签字典")
    @GetMapping("/user/tag-dict/all")
    @RequireAdminPermission(value = {"USER_TAG:VIEW", "SYSTEM:MANAGE"})
    public ApiResponse<List<UserTagDict>> getAllActiveTagDicts() {
        try {
            List<UserTagDict> tagDicts = userTagDictService.getAllActiveTagDicts();
            return ApiResponse.success("查询成功", tagDicts);
        } catch (Exception e) {
            log.error("获取所有标签字典失败", e);
            return ApiResponse.error(500, "查询失败");
        }
    }

    // ==================== 用户标签绑定管理 ====================

    @Operation(summary = "用户绑定标签")
    @PostMapping("/user/tag/bind")
    @RequireAdminPermission(value = {"USER_TAG:BIND", "SYSTEM:MANAGE"})
    @OperationLog(type = "绑定", object = "用户标签")
    public ApiResponse<Void> bindTag(@Validated @RequestBody UserTagBindRequest request) {
        try {
            userTagService.bindTag(request);
            return ApiResponse.success("绑定成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("用户绑定标签失败", e);
            return ApiResponse.error(500, "绑定失败");
        }
    }

    @Operation(summary = "用户解绑标签")
    @PostMapping("/user/tag/unbind")
    @RequireAdminPermission(value = {"USER_TAG:UNBIND", "SYSTEM:MANAGE"})
    @OperationLog(type = "解绑", object = "用户标签")
    public ApiResponse<Void> unbindTag(@Validated @RequestBody UserTagBindRequest request) {
        try {
            userTagService.unbindTag(request);
            return ApiResponse.success("解绑成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("用户解绑标签失败", e);
            return ApiResponse.error(500, "解绑失败");
        }
    }

    @Operation(summary = "用户批量绑定标签")
    @PostMapping("/user/tag/batch-bind")
    @RequireAdminPermission(value = {"USER_TAG:BIND", "SYSTEM:MANAGE"})
    @OperationLog(type = "批量绑定", object = "用户标签")
    public ApiResponse<Void> batchBindTags(@Validated @RequestBody UserTagBatchBindRequest request) {
        try {
            userTagService.batchBindTags(request);
            return ApiResponse.success("批量绑定成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("用户批量绑定标签失败", e);
            return ApiResponse.error(500, "批量绑定失败");
        }
    }

    @Operation(summary = "用户批量解绑标签")
    @PostMapping("/user/tag/batch-unbind")
    @RequireAdminPermission(value = {"USER_TAG:UNBIND", "SYSTEM:MANAGE"})
    @OperationLog(type = "批量解绑", object = "用户标签")
    public ApiResponse<Void> batchUnbindTags(@Validated @RequestBody UserTagBatchBindRequest request) {
        try {
            userTagService.batchUnbindTags(request);
            return ApiResponse.success("批量解绑成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("用户批量解绑标签失败", e);
            return ApiResponse.error(500, "批量解绑失败");
        }
    }

    @Operation(summary = "查询用户的所有标签")
    @GetMapping("/user/tag/list/{userId}")
    @RequireAdminPermission(value = {"USER_TAG:VIEW", "SYSTEM:MANAGE"})
    public ApiResponse<List<UserTagDict>> getUserTags(@PathVariable Long userId) {
        try {
            List<UserTagDict> tags = userTagService.getUserTags(userId);
            return ApiResponse.success("查询成功", tags);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("查询用户标签失败", e);
            return ApiResponse.error(500, "查询失败");
        }
    }

    // ==================== 用户管理 ====================

    @Operation(summary = "分页查询用户列表")
    @GetMapping("/user/list")
    @RequireAdminPermission(value = {"USER:VIEW", "SYSTEM:MANAGE"})
    public ApiResponse<PageResponse<UserDetailResponse>> queryUsers(
            @RequestParam(defaultValue = "1") Long pageNum,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer tagDictId) {
        try {
            UserQueryRequest request = UserQueryRequest.builder()
                    .pageNum(pageNum)
                    .pageSize(pageSize)
                    .username(username)
                    .nickname(nickname)
                    .email(email)
                    .phone(phone)
                    .status(status)
                    .tagDictId(tagDictId)
                    .build();
            PageResponse<UserDetailResponse> result = userAdminService.queryUsers(request);
            return ApiResponse.success("查询成功", result);
        } catch (Exception e) {
            log.error("查询用户列表失败", e);
            return ApiResponse.error(500, "查询失败");
        }
    }

    @Operation(summary = "获取用户完整详情", description = "包括Profile、Preference和标签列表")
    @GetMapping("/user/detail/{userId}")
    @RequireAdminPermission(value = {"USER:VIEW", "SYSTEM:MANAGE"})
    public ApiResponse<UserDetailResponse> getUserDetail(@PathVariable Long userId) {
        try {
            UserDetailResponse detail = userAdminService.getUserDetail(userId);
            return ApiResponse.success("查询成功", detail);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("获取用户详情失败", e);
            return ApiResponse.error(500, "查询失败");
        }
    }

    @Operation(summary = "获取用户计数表数据")
    @GetMapping("/user/count/{userId}")
    @RequireAdminPermission(value = {"USER:VIEW", "SYSTEM:MANAGE"})
    public ApiResponse<UserCountTable> getUserCountTable(@PathVariable Long userId) {
        try {
            UserCountTable countTable = userAdminService.getUserCountTable(userId);
            return ApiResponse.success("查询成功", countTable);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("获取用户计数表数据失败", e);
            return ApiResponse.error(500, "查询失败");
        }
    }
}
