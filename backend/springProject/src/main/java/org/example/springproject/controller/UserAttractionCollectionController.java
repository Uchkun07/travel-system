package org.example.springproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.example.springproject.entity.dto.ApiResponse;
import org.example.springproject.service.IUserAttractionCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/collections/attractions")
@Tag(name = "景点收藏", description = "基于Token的景点收藏/取消收藏/查询接口")
public class UserAttractionCollectionController {

    @Autowired
    private IUserAttractionCollectionService collectionService;

    @Operation(summary = "收藏景点", description = "根据Token识别用户，收藏指定景点，幂等")
    @PostMapping("/{attractionId}")
    public ApiResponse<Map<String, Object>> collect(
            HttpServletRequest request,
            @Parameter(description = "景点ID", required = true)
            @PathVariable Integer attractionId) {
        Long userIdAttr = (Long) request.getAttribute("userId");
        if (userIdAttr == null) {
            return ApiResponse.error(401, "未认证或令牌无效");
        }
        Integer userId = userIdAttr.intValue();

        boolean ok = collectionService.collect(userId, attractionId);
        Map<String, Object> data = new HashMap<>();
        data.put("collected", ok);
        return ok ? ApiResponse.success("收藏成功", data) : ApiResponse.error("收藏失败");
    }

    @Operation(summary = "取消收藏景点", description = "根据Token识别用户，取消收藏指定景点，幂等")
    @DeleteMapping("/{attractionId}")
    public ApiResponse<Map<String, Object>> uncollect(
            HttpServletRequest request,
            @Parameter(description = "景点ID", required = true)
            @PathVariable Integer attractionId) {
        Long userIdAttr = (Long) request.getAttribute("userId");
        if (userIdAttr == null) {
            return ApiResponse.error(401, "未认证或令牌无效");
        }
        Integer userId = userIdAttr.intValue();

        boolean ok = collectionService.uncollect(userId, attractionId);
        Map<String, Object> data = new HashMap<>();
        data.put("uncollected", ok);
        return ok ? ApiResponse.success("取消收藏成功", data) : ApiResponse.error("取消收藏失败");
    }

    @Operation(summary = "获取我的收藏景点ID列表", description = "根据Token识别用户，返回当前用户收藏的所有景点ID，按收藏时间倒序")
    @GetMapping("/ids")
    public ApiResponse<List<Integer>> getCollectedIds(HttpServletRequest request) {
        Long userIdAttr = (Long) request.getAttribute("userId");
        if (userIdAttr == null) {
            return ApiResponse.error(401, "未认证或令牌无效");
        }
        Integer userId = userIdAttr.intValue();

        List<Integer> ids = collectionService.getCollectedAttractionIds(userId);
        return ApiResponse.success("获取成功", ids);
    }

    @Operation(summary = "是否已收藏", description = "根据Token识别用户，检查某景点是否被收藏")
    @GetMapping("/{attractionId}/status")
    public ApiResponse<Map<String, Object>> isCollected(
            HttpServletRequest request,
            @Parameter(description = "景点ID", required = true)
            @PathVariable Integer attractionId) {
        Long userIdAttr = (Long) request.getAttribute("userId");
        if (userIdAttr == null) {
            return ApiResponse.error(401, "未认证或令牌无效");
        }
        Integer userId = userIdAttr.intValue();
        boolean collected = collectionService.isCollected(userId, attractionId);
        Map<String, Object> data = new HashMap<>();
        data.put("collected", collected);
        return ApiResponse.success(data);
    }
}


