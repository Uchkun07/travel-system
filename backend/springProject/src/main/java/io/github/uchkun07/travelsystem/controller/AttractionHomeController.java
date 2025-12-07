package io.github.uchkun07.travelsystem.controller;

import io.github.uchkun07.travelsystem.dto.ApiResponse;
import io.github.uchkun07.travelsystem.dto.AttractionCardResponse;
import io.github.uchkun07.travelsystem.dto.AttractionDetailResponse;
import io.github.uchkun07.travelsystem.dto.AttractionQueryRequest;
import io.github.uchkun07.travelsystem.dto.PageResponse;
import io.github.uchkun07.travelsystem.service.IAttractionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 景点C端控制器
 */
@Slf4j
@Tag(name = "景点(C端)", description = "景点前台展示接口")
@RestController
@RequestMapping("/api/attraction")
public class AttractionHomeController {

    @Autowired
    private IAttractionService attractionService;

    @Operation(summary = "分页获取景点卡片数据", description = "前台景点列表展示，支持分页和筛选")
    @PostMapping("/list")
    public ApiResponse<PageResponse<AttractionCardResponse>> getAttractionList(
            @RequestBody AttractionQueryRequest request) {
        try {
            // 只返回已审核通过的景点
            request.setAuditStatus(2); // 2=已通过
            request.setStatus(1); // 1=正常
            
            PageResponse<AttractionCardResponse> page = attractionService.getAttractionCards(request);
            return ApiResponse.success("获取成功", page);
        } catch (Exception e) {
            log.error("获取景点列表失败", e);
            return ApiResponse.error(500, "获取失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取景点详情", description = "根据景点ID获取详细信息")
    @GetMapping("/detail/{attractionId}")
    public ApiResponse<AttractionDetailResponse> getAttractionDetail(@PathVariable Long attractionId) {
        try {
            AttractionDetailResponse detail = attractionService.getAttractionCardById(attractionId);
            return ApiResponse.success("获取成功", detail);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(404, e.getMessage());
        } catch (Exception e) {
            log.error("获取景点详情失败", e);
            return ApiResponse.error(500, "获取失败: " + e.getMessage());
        }
    }

    @Operation(summary = "增加景点浏览量")
    @PostMapping("/view/{attractionId}")
    public ApiResponse<Void> incrementViewCount(@PathVariable Long attractionId) {
        try {
            attractionService.incrementBrowseCount(attractionId);
            return ApiResponse.success("记录成功", null);
        } catch (Exception e) {
            log.error("增加浏览量失败", e);
            return ApiResponse.error(500, "记录失败: " + e.getMessage());
        }
    }
}
