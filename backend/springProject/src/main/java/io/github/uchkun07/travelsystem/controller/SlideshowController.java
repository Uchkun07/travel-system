package io.github.uchkun07.travelsystem.controller;

import io.github.uchkun07.travelsystem.annotation.OperationLog;
import io.github.uchkun07.travelsystem.annotation.RequireAdminPermission;
import io.github.uchkun07.travelsystem.dto.*;
import io.github.uchkun07.travelsystem.entity.Slideshow;
import io.github.uchkun07.travelsystem.service.ISlideshowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 轮播图控制器
 */
@Slf4j
@Tag(name = "轮播图管理", description = "轮播图的增删改查接口")
@RestController
@RequestMapping("/admin/slideshow")
@RequireAdminPermission
public class SlideshowController {

    @Autowired
    private ISlideshowService slideshowService;

    @Operation(summary = "创建轮播图")
    @PostMapping("/create")
    @RequireAdminPermission(value = {"SLIDESHOW:CREATE", "SYSTEM:MANAGE"})
    @OperationLog(type = "创建", object = "轮播图")
    public ApiResponse<Slideshow> createSlideshow(@Validated @RequestBody SlideshowRequest request) {
        try {
            Slideshow slideshow = slideshowService.createSlideshow(request);
            return ApiResponse.success("创建成功", slideshow);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("创建轮播图失败", e);
            return ApiResponse.error(500, "创建失败");
        }
    }

    @Operation(summary = "删除轮播图")
    @DeleteMapping("/delete/{slideshowId}")
    @RequireAdminPermission(value = {"SLIDESHOW:DELETE", "SYSTEM:MANAGE"})
    @OperationLog(type = "删除", object = "轮播图")
    public ApiResponse<Void> deleteSlideshow(@PathVariable Integer slideshowId) {
        try {
            slideshowService.deleteSlideshow(slideshowId);
            return ApiResponse.success("删除成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("删除轮播图失败", e);
            return ApiResponse.error(500, "删除失败");
        }
    }

    @Operation(summary = "批量删除轮播图")
    @DeleteMapping("/batch-delete")
    @RequireAdminPermission(value = {"SLIDESHOW:DELETE", "SYSTEM:MANAGE"})
    @OperationLog(type = "批量删除", object = "轮播图")
    public ApiResponse<Void> batchDeleteSlideshows(@RequestBody List<Integer> slideshowIds) {
        try {
            slideshowService.batchDeleteSlideshows(slideshowIds);
            return ApiResponse.success("批量删除成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("批量删除轮播图失败", e);
            return ApiResponse.error(500, "批量删除失败");
        }
    }

    @Operation(summary = "修改轮播图")
    @PutMapping("/update")
    @RequireAdminPermission(value = {"SLIDESHOW:UPDATE", "SYSTEM:MANAGE"})
    @OperationLog(type = "修改", object = "轮播图")
    public ApiResponse<Slideshow> updateSlideshow(@Validated @RequestBody SlideshowRequest request) {
        try {
            Slideshow slideshow = slideshowService.updateSlideshow(request);
            return ApiResponse.success("修改成功", slideshow);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("修改轮播图失败", e);
            return ApiResponse.error(500, "修改失败");
        }
    }

    @Operation(summary = "分页查询轮播图")
    @GetMapping("/list")
    @RequireAdminPermission(value = {"SLIDESHOW:VIEW", "SYSTEM:MANAGE"})
    public ApiResponse<PageResponse<Slideshow>> querySlideshows(
            @RequestParam(defaultValue = "1") Long pageNum,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long attractionId) {
        try {
            SlideshowQueryRequest request = SlideshowQueryRequest.builder()
                    .pageNum(pageNum)
                    .pageSize(pageSize)
                    .title(title)
                    .status(status)
                    .attractionId(attractionId)
                    .build();
            PageResponse<Slideshow> result = slideshowService.querySlideshows(request);
            return ApiResponse.success("查询成功", result);
        } catch (Exception e) {
            log.error("查询轮播图失败", e);
            return ApiResponse.error(500, "查询失败");
        }
    }

    @Operation(summary = "查询轮播图详情")
    @GetMapping("/detail/{slideshowId}")
    @RequireAdminPermission(value = {"SLIDESHOW:VIEW", "SYSTEM:MANAGE"})
    public ApiResponse<Slideshow> getSlideshowById(@PathVariable Integer slideshowId) {
        try {
            Slideshow slideshow = slideshowService.getSlideshowById(slideshowId);
            return ApiResponse.success("查询成功", slideshow);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("查询轮播图详情失败", e);
            return ApiResponse.error(500, "查询失败");
        }
    }

    @Operation(summary = "获取启用的轮播图")
    @GetMapping("/active")
    public ApiResponse<List<Slideshow>> getActiveSlideshows() {
        try {
            List<Slideshow> slideshows = slideshowService.getActiveSlideshows();
            return ApiResponse.success("查询成功", slideshows);
        } catch (Exception e) {
            log.error("获取启用的轮播图失败", e);
            return ApiResponse.error(500, "查询失败");
        }
    }
}
