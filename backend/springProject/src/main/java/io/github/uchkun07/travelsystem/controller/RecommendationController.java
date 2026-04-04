package io.github.uchkun07.travelsystem.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.uchkun07.travelsystem.config.PerformanceProperties;
import io.github.uchkun07.travelsystem.dto.ApiResponse;
import io.github.uchkun07.travelsystem.dto.RecommendHomeRequest;
import io.github.uchkun07.travelsystem.dto.RecommendHomeResponse;
import io.github.uchkun07.travelsystem.dto.RecommendTrackRequest;
import io.github.uchkun07.travelsystem.service.IRecommendationService;
import io.github.uchkun07.travelsystem.util.CacheClient;
import io.github.uchkun07.travelsystem.util.CacheConstants;
import io.github.uchkun07.travelsystem.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/api/recommend")
@Tag(name = "推荐系统", description = "首页个性化推荐与埋点接口")
@RequiredArgsConstructor
public class RecommendationController {

    private static final TypeReference<RecommendHomeResponse> RECOMMEND_TYPE =
            new TypeReference<>() {
            };

    private final IRecommendationService recommendationService;
    private final JwtUtil jwtUtil;
    private final CacheClient cacheClient;
    private final PerformanceProperties performanceProperties;

    @Qualifier("generalAsyncExecutor")
    private final Executor generalAsyncExecutor;

    @Operation(summary = "首页个性化推荐")
    @PostMapping("/home")
    public ApiResponse<RecommendHomeResponse> homeRecommend(HttpServletRequest request,
                                                            @RequestBody(required = false) RecommendHomeRequest body) {
        try {
            Long userId = resolveUserIdIfPresent(request);
            RecommendHomeRequest req = body == null ? new RecommendHomeRequest() : body;
            String cacheKey = CacheConstants.RECOMMEND_HOME_KEY
                + (userId == null ? 0L : userId)
                + ":" + normalizePageNum(req.getPageNum())
                + ":" + normalizePageSize(req.getPageSize());

            RecommendHomeResponse response = cacheClient.queryWithPassThrough(
                cacheKey,
                RECOMMEND_TYPE,
                () -> executeWithTimeout(userId, req),
                performanceProperties.getCache().getRecommendTtlSec(),
                performanceProperties.getCache().getNullValueTtlSec(),
                performanceProperties.getCache().getTtlJitterSec());
            return ApiResponse.success("获取成功", response);
        } catch (Exception e) {
            log.error("获取首页推荐失败", e);
            return ApiResponse.error(500, "获取推荐失败: " + e.getMessage());
        }
    }

    @Operation(summary = "推荐行为埋点")
    @PostMapping("/track")
    public ApiResponse<Void> track(HttpServletRequest request,
                                   @Valid @RequestBody RecommendTrackRequest body) {
        try {
            Long userId = resolveUserIdIfPresent(request);
            recommendationService.track(userId, body, request.getHeader("User-Agent"));
            return ApiResponse.success("埋点成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("推荐埋点失败", e);
            return ApiResponse.error(500, "推荐埋点失败");
        }
    }

    private Long resolveUserIdIfPresent(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || token.isBlank()) {
            return null;
        }
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        try {
            return jwtUtil.getUserIdFromToken(token);
        } catch (Exception e) {
            return null;
        }
    }

    private RecommendHomeResponse executeWithTimeout(Long userId, RecommendHomeRequest request) {
        try {
            return java.util.concurrent.CompletableFuture
                    .supplyAsync(() -> recommendationService.getHomeRecommendations(userId, request), generalAsyncExecutor)
                    .get(performanceProperties.getRecommendTimeoutMs(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            throw new RuntimeException("推荐接口超时或执行失败", e);
        }
    }

    private long normalizePageNum(Long pageNum) {
        return pageNum == null || pageNum < 1 ? 1 : pageNum;
    }

    private long normalizePageSize(Long pageSize) {
        if (pageSize == null || pageSize < 1) {
            return 20;
        }
        return Math.min(pageSize, 100);
    }
}
