package io.github.uchkun07.travelsystem.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.uchkun07.travelsystem.entity.Attraction;
import io.github.uchkun07.travelsystem.mapper.AttractionMapper;
import io.github.uchkun07.travelsystem.util.CacheConstants;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 景点ID过滤集定时预热
 * 用于降低恶意ID请求造成的缓存穿透。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AttractionCacheWarmupTask {

    private final AttractionMapper attractionMapper;
    private final StringRedisTemplate stringRedisTemplate;

    @PostConstruct
    public void init() {
        refreshAttractionIdFilter();
    }

    @Scheduled(cron = "0 */30 * * * ?")
    @SuppressWarnings("null")
    public void refreshAttractionIdFilter() {
        List<Attraction> attractions = attractionMapper.selectList(
            new LambdaQueryWrapper<Attraction>()
                .select(Attraction::getAttractionId)
                .eq(Attraction::getStatus, 1)
                .eq(Attraction::getAuditStatus, 2));
        Set<String> ids = attractions.stream()
                .map(Attraction::getAttractionId)
                .filter(id -> id != null && id > 0)
                .map(String::valueOf)
                .collect(Collectors.toSet());

        if (ids.isEmpty()) {
            return;
        }

        stringRedisTemplate.delete(CacheConstants.ATTR_ID_FILTER_KEY);
        stringRedisTemplate.opsForSet().add(CacheConstants.ATTR_ID_FILTER_KEY, ids.toArray(new String[0]));
        stringRedisTemplate.expire(CacheConstants.ATTR_ID_FILTER_KEY, 2, TimeUnit.HOURS);
        log.info("景点ID过滤集刷新完成, size={}", ids.size());
    }
}
