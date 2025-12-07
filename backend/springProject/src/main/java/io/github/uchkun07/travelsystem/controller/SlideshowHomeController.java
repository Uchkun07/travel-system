package io.github.uchkun07.travelsystem.controller;

import io.github.uchkun07.travelsystem.dto.ApiResponse;
import io.github.uchkun07.travelsystem.entity.Slideshow;
import io.github.uchkun07.travelsystem.service.ISlideshowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 轮播图C端控制器
 */
@Slf4j
@Tag(name = "轮播图(C端)", description = "轮播图前台展示接口")
@RestController
@RequestMapping("/api/home/slideshow")
public class SlideshowHomeController {

    @Autowired
    private ISlideshowService slideshowService;

    @Operation(summary = "获取轮播图列表", description = "获取前台展示的启用状态轮播图列表")
    @GetMapping("/list")
    public ApiResponse<List<Slideshow>> getSlideshowList() {
        try {
            List<Slideshow> slideshows = slideshowService.getActiveSlideshows();
            return ApiResponse.success("获取成功", slideshows);
        } catch (Exception e) {
            log.error("获取轮播图列表失败", e);
            return ApiResponse.error(500, "获取失败: " + e.getMessage());
        }
    }

    @Operation(summary = "记录轮播图点击", description = "增加轮播图点击次数")
    @PostMapping("/click/{slideshowId}")
    public ApiResponse<Void> recordClick(@PathVariable Integer slideshowId) {
        try {
            slideshowService.incrementClickCount(slideshowId);
            return ApiResponse.success("记录成功", null);
        } catch (Exception e) {
            log.error("记录轮播图点击失败", e);
            return ApiResponse.error(500, "记录失败: " + e.getMessage());
        }
    }
}
