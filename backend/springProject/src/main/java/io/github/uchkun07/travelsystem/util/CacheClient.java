package io.github.uchkun07.travelsystem.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Redis 缓存客户端
 * 支持缓存穿透、击穿、雪崩等常见问题防护。
 */
@Slf4j
@Component
@RequiredArgsConstructor
@SuppressWarnings("null")
public class CacheClient {

    private static final long LOCK_TTL_SECONDS = 10;
    private static final long RETRY_SLEEP_MILLIS = 50;

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    public void set(String key, Object value, long ttlSeconds, long jitterSeconds) {
        try {
            long ttl = ttlSeconds + randomJitter(jitterSeconds);
            String json = objectMapper.writeValueAsString(value);
            stringRedisTemplate.opsForValue().set(key, json, ttl, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("写入缓存失败 key={}", key, e);
        }
    }

    public void setNull(String key, long ttlSeconds) {
        stringRedisTemplate.opsForValue().set(key, CacheConstants.NULL_VALUE, ttlSeconds, TimeUnit.SECONDS);
    }

    public <T> T queryWithPassThrough(String key,
                                      TypeReference<T> type,
                                      Supplier<T> dbFallback,
                                      long ttlSeconds,
                                      long nullValueTtlSeconds,
                                      long jitterSeconds) {
        String cacheVal = null;
        try {
            cacheVal = stringRedisTemplate.opsForValue().get(key);
            if (cacheVal != null) {
                if (CacheConstants.NULL_VALUE.equals(cacheVal)) {
                    return null;
                }
                return deserialize(cacheVal, type);
            }
        } catch (Exception e) {
            log.warn("缓存透传读取失败，继续执行回源查询 key={}", key, e);
        }

        T dbValue = dbFallback.get();
        if (dbValue == null) {
            try {
                setNull(key, nullValueTtlSeconds);
            } catch (Exception e) {
                log.warn("缓存空值写入失败 key={}", key, e);
            }
            return null;
        }

        try {
            set(key, dbValue, ttlSeconds, jitterSeconds);
        } catch (Exception e) {
            log.warn("缓存写入失败，不影响本次返回 key={}", key, e);
        }
        return dbValue;
    }

    public <T> T queryWithMutex(String key,
                                String lockKey,
                                Class<T> type,
                                Supplier<T> dbFallback,
                                long ttlSeconds,
                                long nullValueTtlSeconds,
                                long jitterSeconds) {
        try {
            while (true) {
                String cacheVal = stringRedisTemplate.opsForValue().get(key);
                if (cacheVal != null) {
                    if (CacheConstants.NULL_VALUE.equals(cacheVal)) {
                        return null;
                    }
                    return deserialize(cacheVal, type);
                }

                boolean locked = tryLock(lockKey);
                if (!locked) {
                    sleepBriefly();
                    continue;
                }

                try {
                    // double check
                    cacheVal = stringRedisTemplate.opsForValue().get(key);
                    if (cacheVal != null) {
                        if (CacheConstants.NULL_VALUE.equals(cacheVal)) {
                            return null;
                        }
                        return deserialize(cacheVal, type);
                    }

                    T dbValue = dbFallback.get();
                    if (dbValue == null) {
                        setNull(key, nullValueTtlSeconds);
                        return null;
                    }

                    set(key, dbValue, ttlSeconds, jitterSeconds);
                    return dbValue;
                } finally {
                    unlock(lockKey);
                }
            }
        } catch (Exception e) {
            log.warn("缓存互斥读取失败，降级查询数据库 key={}", key, e);
            return dbFallback.get();
        }
    }

    public boolean existsInAttractionIdFilter(Long attractionId) {
        if (attractionId == null || attractionId <= 0) {
            return false;
        }
        try {
            Boolean keyExists = stringRedisTemplate.hasKey(CacheConstants.ATTR_ID_FILTER_KEY);
            // 过滤器未初始化时直接放行，避免误判404
            if (!Boolean.TRUE.equals(keyExists)) {
                return true;
            }
            return Boolean.TRUE.equals(
                    stringRedisTemplate.opsForSet().isMember(CacheConstants.ATTR_ID_FILTER_KEY, String.valueOf(attractionId)));
        } catch (Exception e) {
            log.warn("景点ID过滤器读取失败，降级放行 attractionId={}", attractionId, e);
            return true;
        }
    }

    private boolean tryLock(String lockKey) {
        Boolean ok = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, "1", LOCK_TTL_SECONDS, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(ok);
    }

    private void unlock(String lockKey) {
        stringRedisTemplate.delete(lockKey);
    }

    private long randomJitter(long jitterSeconds) {
        if (jitterSeconds <= 0) {
            return 0;
        }
        return ThreadLocalRandom.current().nextLong(jitterSeconds + 1);
    }

    private void sleepBriefly() {
        try {
            Thread.sleep(RETRY_SLEEP_MILLIS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private <T> T deserialize(String cacheVal, Class<T> type) {
        try {
            return objectMapper.readValue(cacheVal, type);
        } catch (Exception e) {
            throw new RuntimeException("缓存反序列化失败", e);
        }
    }

    private <T> T deserialize(String cacheVal, TypeReference<T> type) {
        try {
            return objectMapper.readValue(cacheVal, type);
        } catch (Exception e) {
            throw new RuntimeException("缓存反序列化失败", e);
        }
    }
}
