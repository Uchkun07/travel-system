package io.github.uchkun07.travelsystem.controller;

import io.github.uchkun07.travelsystem.dto.ApiResponse;
import io.github.uchkun07.travelsystem.dto.AttractionCardResponse;
import io.github.uchkun07.travelsystem.dto.AttractionDetailResponse;
import io.github.uchkun07.travelsystem.dto.AttractionQueryRequest;
import io.github.uchkun07.travelsystem.dto.PageResponse;
import io.github.uchkun07.travelsystem.entity.AttractionType;
import io.github.uchkun07.travelsystem.service.IAttractionService;
import io.github.uchkun07.travelsystem.service.IAttractionTypeService;
import io.github.uchkun07.travelsystem.service.IUserCollectionService;
import io.github.uchkun07.travelsystem.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private IUserCollectionService userCollectionService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private IAttractionTypeService attractionTypeService;

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

    @Operation(summary = "收藏景点", description = "根据Token识别用户，收藏指定景点")
    @PostMapping("/collection/{attractionId}")
    public ApiResponse<Map<String, Object>> collectAttraction(
            HttpServletRequest request,
            @Parameter(description = "景点ID", required = true) @PathVariable Long attractionId) {
        try {
            // 从请求头获取 Token
            String token = request.getHeader("Authorization");
            if (token == null || token.isEmpty()) {
                return ApiResponse.error(401, "未认证或令牌无效");
            }

            // 去除 "Bearer " 前缀
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            // 从 Token 解析用户ID
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error(401, "令牌无效");
            }

            boolean success = userCollectionService.collectAttraction(userId, attractionId);
            Map<String, Object> data = new HashMap<>();
            data.put("collected", success);
            return success ? ApiResponse.success("收藏成功", data) : ApiResponse.error(500, "收藏失败");
        } catch (Exception e) {
            log.error("收藏景点失败", e);
            return ApiResponse.error(500, "收藏失败: " + e.getMessage());
        }
    }

    @Operation(summary = "取消收藏景点", description = "根据Token识别用户，取消收藏指定景点")
    @DeleteMapping("/collection/{attractionId}")
    public ApiResponse<Map<String, Object>> uncollectAttraction(
            HttpServletRequest request,
            @Parameter(description = "景点ID", required = true) @PathVariable Long attractionId) {
        try {
            // 从请求头获取 Token
            String token = request.getHeader("Authorization");
            if (token == null || token.isEmpty()) {
                return ApiResponse.error(401, "未认证或令牌无效");
            }

            // 去除 "Bearer " 前缀
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            // 从 Token 解析用户ID
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error(401, "令牌无效");
            }

            boolean success = userCollectionService.uncollectAttraction(userId, attractionId);
            Map<String, Object> data = new HashMap<>();
            data.put("uncollected", success);
            return success ? ApiResponse.success("取消收藏成功", data) : ApiResponse.error(500, "取消收藏失败");
        } catch (Exception e) {
            log.error("取消收藏景点失败", e);
            return ApiResponse.error(500, "取消收藏失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取我的收藏景点ID列表", description = "根据Token识别用户，返回当前用户收藏的所有景点ID")
    @GetMapping("/collection/ids")
    public ApiResponse<List<Long>> getCollectedAttractionIds(HttpServletRequest request) {
        try {
            // 从请求头获取 Token
            String token = request.getHeader("Authorization");
            if (token == null || token.isEmpty()) {
                return ApiResponse.error(401, "未认证或令牌无效");
            }

            // 去除 "Bearer " 前缀
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            // 从 Token 解析用户ID
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error(401, "令牌无效");
            }

            List<Long> ids = userCollectionService.getCollectedAttractionIds(userId);
            return ApiResponse.success("获取成功", ids);
        } catch (Exception e) {
            log.error("获取收藏列表失败", e);
            return ApiResponse.error(500, "获取失败: " + e.getMessage());
        }
    }

    @Operation(summary = "检查景点是否已收藏", description = "根据Token识别用户，检查某景点是否被收藏")
    @GetMapping("/collection/{attractionId}/status")
    public ApiResponse<Map<String, Object>> isAttractionCollected(
            HttpServletRequest request,
            @Parameter(description = "景点ID", required = true) @PathVariable Long attractionId) {
        try {
            // 从请求头获取 Token
            String token = request.getHeader("Authorization");
            if (token == null || token.isEmpty()) {
                return ApiResponse.error(401, "未认证或令牌无效");
            }

            // 去除 "Bearer " 前缀
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            // 从 Token 解析用户ID
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error(401, "令牌无效");
            }

            boolean collected = userCollectionService.isCollected(userId, attractionId);
            Map<String, Object> data = new HashMap<>();
            data.put("collected", collected);
            return ApiResponse.success(data);
        } catch (Exception e) {
            log.error("检查收藏状态失败", e);
            return ApiResponse.error(500, "检查失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取所有的景点类型")
    @GetMapping("/type/all")
    public ApiResponse<List<AttractionType>> getAllAttractionType() {
        try {
            List<AttractionType> types = attractionTypeService.getAllAttractionTypes();
            return ApiResponse.success("查询成功", types);
        } catch (Exception e) {
            log.error("获取所有景点类型失败", e);
            return ApiResponse.error(500, "查询失败");
        }
    }

    @Operation(summary = "根据ID列表获取景点卡片", description = "批量获取景点信息，用于收藏列表展示")
    @PostMapping("/batch")
    public ApiResponse<List<AttractionCardResponse>> getAttractionsByIds(
            @RequestBody List<Long> attractionIds) {
        try {
            if (attractionIds == null || attractionIds.isEmpty()) {
                return ApiResponse.success("获取成功", List.of());
            }

            List<AttractionCardResponse> attractions = attractionService.getAttractionCardsByIds(attractionIds);
            return ApiResponse.success("获取成功", attractions);
        } catch (Exception e) {
            log.error("批量获取景点失败", e);
            return ApiResponse.error(500, "获取失败: " + e.getMessage());
        }
    }
}
