package io.github.uchkun07.travelsystem.task;

import io.github.uchkun07.travelsystem.config.PerformanceProperties;
import io.github.uchkun07.travelsystem.dto.RecommendHomeRequest;
import io.github.uchkun07.travelsystem.dto.RecommendHomeResponse;
import io.github.uchkun07.travelsystem.mapper.UserBrowseRecordMapper;
import io.github.uchkun07.travelsystem.service.IRecommendationService;
import io.github.uchkun07.travelsystem.util.CacheClient;
import io.github.uchkun07.travelsystem.util.CacheConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 推荐结果预计算任务
 * 定期为活跃用户预热首页推荐缓存，降低请求实时计算压力。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendationPrecomputeTask {

    private final UserBrowseRecordMapper userBrowseRecordMapper;
    private final IRecommendationService recommendationService;
    private final CacheClient cacheClient;
    private final PerformanceProperties performanceProperties;

    @Scheduled(
            fixedDelayString = "${performance.monitor.recommend-precompute-interval-ms:300000}",
            initialDelayString = "${performance.monitor.recommend-precompute-initial-delay-ms:45000}")
    public void precompute() {
        PerformanceProperties.Monitor monitor = performanceProperties.getMonitor();
        if (!monitor.isRecommendPrecomputeEnabled()) {
            return;
        }

        int activeWindowHours = Math.max(1, monitor.getPrecomputeActiveWindowHours());
        int userLimit = Math.max(1, monitor.getPrecomputeUserLimit());
        long pageNum = Math.max(1, monitor.getPrecomputePageNum());
        long pageSize = Math.max(1, monitor.getPrecomputePageSize());

        List<Long> activeUserIds = userBrowseRecordMapper.selectActiveUserIds(activeWindowHours, userLimit);
        List<Long> targetUserIds = new ArrayList<>(activeUserIds == null ? List.of() : activeUserIds);
        if (monitor.isIncludeGuestPrecompute()) {
            targetUserIds.add(0L);
        }

        if (targetUserIds.isEmpty()) {
            return;
        }

        long start = System.currentTimeMillis();
        int success = 0;
        int failed = 0;

        for (Long userId : targetUserIds) {
            Long actualUserId = (userId == null || userId <= 0) ? null : userId;
            try {
                RecommendHomeRequest request = new RecommendHomeRequest();
                request.setPageNum(pageNum);
                request.setPageSize(pageSize);
                RecommendHomeResponse response = recommendationService.getHomeRecommendations(actualUserId, request);

                String cacheKey = CacheConstants.RECOMMEND_HOME_KEY
                        + (actualUserId == null ? 0L : actualUserId)
                        + ":" + pageNum
                        + ":" + pageSize;
                cacheClient.set(
                        cacheKey,
                        response,
                        performanceProperties.getCache().getRecommendTtlSec(),
                        performanceProperties.getCache().getTtlJitterSec());
                success++;
            } catch (Exception ex) {
                failed++;
                log.warn("推荐预计算失败 userId={}", userId, ex);
            }
        }

        long costMs = System.currentTimeMillis() - start;
        log.info("推荐预计算完成 users={}, success={}, failed={}, pageNum={}, pageSize={}, costMs={}",
                targetUserIds.size(), success, failed, pageNum, pageSize, costMs);
    }
}
