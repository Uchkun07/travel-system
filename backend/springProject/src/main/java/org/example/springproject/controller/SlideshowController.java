package org.example.springproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.springproject.entity.Slideshow;
import org.example.springproject.dto.ApiResponse;
import org.example.springproject.service.ISlideshowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/slideshow")
@CrossOrigin
@Tag(name = "轮播图管理", description = "轮播图相关接口,包括前台展示和后台管理功能")
public class SlideshowController {

    @Autowired
    private ISlideshowService slideshowService;

    /**
     * 获取所有启用的轮播图(前台展示)
     * GET /slideshow/active
     */
    @Operation(summary = "获取启用的轮播图", description = "获取所有启用且在有效时间范围内的轮播图,按显示顺序排序,用于前台展示")
    @GetMapping("/active")
    public ApiResponse<List<Slideshow>> getActiveSlideshow() {
        try {
            List<Slideshow> slideshows = slideshowService.getActiveSlideshow();
            return ApiResponse.success("获取轮播图成功", slideshows);
        } catch (Exception e) {
            return ApiResponse.error("获取轮播图失败: " + e.getMessage());
        }
    }

    /**
     * 记录轮播图点击
     * POST /slideshow/click/{id}
     */
    @Operation(summary = "记录轮播图点击", description = "记录用户点击轮播图,增加点击统计数")
    @PostMapping("/click/{id}")
    public ApiResponse<String> recordClick(
            @Parameter(description = "轮播图ID", required = true) @PathVariable Integer id) {
        try {
            boolean success = slideshowService.recordClick(id);
            if (success) {
                return ApiResponse.success("点击记录成功", null);
            } else {
                return ApiResponse.error("点击记录失败");
            }
        } catch (Exception e) {
            return ApiResponse.error("点击记录失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有轮播图(管理后台)
     * GET /slideshow/list
     */
    @Operation(summary = "获取所有轮播图", description = "获取所有轮播图(包括已禁用的),按显示顺序排序,用于管理后台")
    @GetMapping("/list")
    public ApiResponse<List<Slideshow>> getAllSlideshow() {
        try {
            List<Slideshow> slideshows = slideshowService.getAllSlideshow();
            return ApiResponse.success("获取轮播图列表成功", slideshows);
        } catch (Exception e) {
            return ApiResponse.error("获取轮播图列表失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取轮播图详情
     * GET /slideshow/{id}
     */
    @Operation(summary = "获取轮播图详情", description = "根据ID获取单个轮播图的详细信息")
    @GetMapping("/{id}")
    public ApiResponse<Slideshow> getSlideshowById(
            @Parameter(description = "轮播图ID", required = true) @PathVariable Integer id) {
        try {
            Slideshow slideshow = slideshowService.getById(id);
            if (slideshow != null) {
                return ApiResponse.success("获取轮播图详情成功", slideshow);
            } else {
                return ApiResponse.error("轮播图不存在");
            }
        } catch (Exception e) {
            return ApiResponse.error("获取轮播图详情失败: " + e.getMessage());
        }
    }

    /**
     * 添加轮播图
     * POST /slideshow/add
     */
    @Operation(summary = "添加轮播图", description = "添加新的轮播图,需要提供标题、图片URL等信息")
    @PostMapping("/add")
    public ApiResponse<String> addSlideshow(
            @Parameter(description = "轮播图对象", required = true) @RequestBody Slideshow slideshow) {
        try {
            boolean success = slideshowService.save(slideshow);
            if (success) {
                return ApiResponse.success("添加轮播图成功", null);
            } else {
                return ApiResponse.error("添加轮播图失败");
            }
        } catch (Exception e) {
            return ApiResponse.error("添加轮播图失败: " + e.getMessage());
        }
    }

    /**
     * 更新轮播图
     * PUT /slideshow/update
     */
    @Operation(summary = "更新轮播图", description = "更新轮播图信息,需要提供完整的轮播图对象(包含ID)")
    @PutMapping("/update")
    public ApiResponse<String> updateSlideshow(
            @Parameter(description = "轮播图对象", required = true) @RequestBody Slideshow slideshow) {
        try {
            boolean success = slideshowService.updateById(slideshow);
            if (success) {
                return ApiResponse.success("更新轮播图成功", null);
            } else {
                return ApiResponse.error("更新轮播图失败");
            }
        } catch (Exception e) {
            return ApiResponse.error("更新轮播图失败: " + e.getMessage());
        }
    }

    /**
     * 更新轮播图状态(启用/禁用)
     * PUT /slideshow/status/{id}
     */
    @Operation(summary = "更新轮播图状态", description = "启用或禁用指定的轮播图")
    @PutMapping("/status/{id}")
    public ApiResponse<String> updateStatus(
            @Parameter(description = "轮播图ID", required = true) @PathVariable Integer id,
            @Parameter(description = "状态值(0=禁用,1=启用)", required = true) @RequestParam Integer status) {
        try {
            boolean success = slideshowService.updateStatus(id, status);
            if (success) {
                return ApiResponse.success("更新状态成功", null);
            } else {
                return ApiResponse.error("更新状态失败");
            }
        } catch (Exception e) {
            return ApiResponse.error("更新状态失败: " + e.getMessage());
        }
    }

    /**
     * 批量更新显示顺序
     * PUT /slideshow/order
     */
    @Operation(summary = "批量更新显示顺序", description = "批量更新多个轮播图的显示顺序,用于拖拽排序功能")
    @PutMapping("/order")
    public ApiResponse<String> updateDisplayOrder(
            @Parameter(description = "轮播图列表(包含ID和displayOrder)", required = true) @RequestBody List<Slideshow> slideshows) {
        try {
            boolean success = slideshowService.updateDisplayOrder(slideshows);
            if (success) {
                return ApiResponse.success("更新显示顺序成功", null);
            } else {
                return ApiResponse.error("更新显示顺序失败");
            }
        } catch (Exception e) {
            return ApiResponse.error("更新显示顺序失败: " + e.getMessage());
        }
    }

    /**
     * 删除轮播图
     * DELETE /slideshow/delete/{id}
     */
    @Operation(summary = "删除轮播图", description = "删除指定的轮播图")
    @DeleteMapping("/delete/{id}")
    public ApiResponse<String> deleteSlideshow(
            @Parameter(description = "轮播图ID", required = true) @PathVariable Integer id) {
        try {
            boolean success = slideshowService.removeById(id);
            if (success) {
                return ApiResponse.success("删除轮播图成功", null);
            } else {
                return ApiResponse.error("删除轮播图失败");
            }
        } catch (Exception e) {
            return ApiResponse.error("删除轮播图失败: " + e.getMessage());
        }
    }
}
