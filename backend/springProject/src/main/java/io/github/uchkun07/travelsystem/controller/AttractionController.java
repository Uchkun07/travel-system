package io.github.uchkun07.travelsystem.controller;

import io.github.uchkun07.travelsystem.annotation.OperationLog;
import io.github.uchkun07.travelsystem.annotation.RequireAdminPermission;
import io.github.uchkun07.travelsystem.dto.*;
import io.github.uchkun07.travelsystem.service.IAttractionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 景点控制器
 */
@Slf4j
@Tag(name = "景点管理", description = "景点的增删改查接口")
@RestController
@RequestMapping("/admin/attraction")
@RequireAdminPermission
public class AttractionController {

    @Autowired
    private IAttractionService attractionService;

    @Operation(summary = "创建景点")
    @PostMapping("/create")
    @RequireAdminPermission(value = {"ATTRACTION:CREATE", "SYSTEM:MANAGE"})
    @OperationLog(type = "创建", object = "景点")
    public ApiResponse<Long> createAttraction(@Validated @RequestBody AttractionRequest request) {
        try {
            Long attractionId = attractionService.createAttraction(request);
            return ApiResponse.success("创建成功", attractionId);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("创建景点失败", e);
            return ApiResponse.error(500, "创建失败: " + e.getMessage());
        }
    }

    @Operation(summary = "删除景点")
    @DeleteMapping("/delete/{attractionId}")
    @RequireAdminPermission(value = {"ATTRACTION:DELETE", "SYSTEM:MANAGE"})
    @OperationLog(type = "删除", object = "景点")
    public ApiResponse<Void> deleteAttraction(@PathVariable Long attractionId) {
        try {
            attractionService.deleteAttraction(attractionId);
            return ApiResponse.success("删除成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("删除景点失败", e);
            return ApiResponse.error(500, "删除失败: " + e.getMessage());
        }
    }

    @Operation(summary = "更新景点")
    @PutMapping("/update")
    @RequireAdminPermission(value = {"ATTRACTION:UPDATE", "SYSTEM:MANAGE"})
    @OperationLog(type = "更新", object = "景点")
    public ApiResponse<Void> updateAttraction(@Validated @RequestBody AttractionRequest request) {
        try {
            attractionService.updateAttraction(request);
            return ApiResponse.success("更新成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("更新景点失败", e);
            return ApiResponse.error(500, "更新失败: " + e.getMessage());
        }
    }

    @Operation(summary = "分页查询景点列表", description = "获取景点列表(景点ID、名称、类型、城市、浏览量、收藏数、人气指数)")
    @PostMapping("/list")
    @RequireAdminPermission(value = {"ATTRACTION:LIST", "SYSTEM:MANAGE"})
    public ApiResponse<PageResponse<AttractionListResponse>> listAttractions(@Validated @RequestBody AttractionQueryRequest request) {
        try {
            PageResponse<AttractionListResponse> page = attractionService.listAttractions(request);
            return ApiResponse.success("查询成功", page);
        } catch (Exception e) {
            log.error("查询景点列表失败", e);
            return ApiResponse.error(500, "查询失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取景点详情", description = "获取景点的完整信息,包括绑定的标签")
    @GetMapping("/detail/{attractionId}")
    @RequireAdminPermission(value = {"ATTRACTION:DETAIL", "SYSTEM:MANAGE"})
    public ApiResponse<AttractionDetailResponse> getAttractionDetail(@PathVariable Long attractionId) {
        try {
            AttractionDetailResponse detail = attractionService.getAttractionDetail(attractionId);
            return ApiResponse.success("查询成功", detail);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("获取景点详情失败", e);
            return ApiResponse.error(500, "查询失败: " + e.getMessage());
        }
    }
}
