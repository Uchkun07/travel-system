package io.github.uchkun07.travelsystem.controller;

import io.github.uchkun07.travelsystem.dto.ApiResponse;
import io.github.uchkun07.travelsystem.dto.RoutePlanRequest;
import io.github.uchkun07.travelsystem.dto.RoutePlanResult;
import io.github.uchkun07.travelsystem.service.IRoutePlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

<<<<<<< HEAD
=======
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

>>>>>>> parent of f9d89de (feat: fix bug)
/**
 * 路线规划控制器
 * POST /api/route/plan — 提交规划参数，返回最优路线结果
 */
@Slf4j
@Tag(name = "路线规划", description = "模拟退火算法 + 高德路径规划")
@RestController
@RequestMapping("/api/route")
@RequiredArgsConstructor
public class RoutePlanController {

    private final IRoutePlanService routePlanService;

    /**
     * 执行路线规划
     *
     * @param request 出发地、预算、日期、出行方式/人群/偏好、景点 ID 列表
     * @return 包含各景点顺序及费用/耗时的规划结果
     */
    @Operation(summary = "路线规划", description = "提交景点列表，返回模拟退火最优路线")
    @PostMapping("/plan")
    public ApiResponse<RoutePlanResult> plan(@Valid @RequestBody RoutePlanRequest request) {
        try {
            log.info("路线规划请求：departure={}, attractions={}",
                    request.getDeparture(), request.getAttractionIds());
            RoutePlanResult result = routePlanService.plan(request);
            return ApiResponse.success("路线规划成功", result);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("路线规划失败", e);
            return ApiResponse.error(500, "路线规划失败：" + e.getMessage());
        }
    }
<<<<<<< HEAD
=======

    private RoutePlanResult executeWithTimeout(RoutePlanRequest request) {
        try {
            return CompletableFuture
                    .supplyAsync(() -> routePlanService.plan(request), routePlanExecutor)
                    .get(performanceProperties.getRoutePlanTimeoutMs(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            throw new RuntimeException("路线规划计算超时或执行失败", e);
        }
    }

    private String buildRoutePlanCacheKey(RoutePlanRequest request) {
        List<Long> ids = new ArrayList<>(request.getAttractionIds());
        Collections.sort(ids);
        String raw = request.getDeparture() + "|" + request.getBudget() + "|"
                + request.getDepartureDate() + "|" + request.getTravelMode() + "|"
                + request.getTravelGroup() + "|" + request.getTravelPreference() + "|" + ids;
        return CacheConstants.ROUTE_PLAN_KEY + DigestUtils.md5DigestAsHex(raw.getBytes(StandardCharsets.UTF_8));
    }
>>>>>>> parent of f9d89de (feat: fix bug)
}
