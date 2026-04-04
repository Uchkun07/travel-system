package io.github.uchkun07.travelsystem.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 高并发性能参数配置
 */
@Data
@ConfigurationProperties(prefix = "performance")
public class PerformanceProperties {

    /** 推荐接口超时阈值(ms) */
    private long recommendTimeoutMs = 1200;

    /** 路线规划接口超时阈值(ms) */
    private long routePlanTimeoutMs = 10000;

    private Cache cache = new Cache();
    private Executor executor = new Executor();

    @Data
    public static class Cache {
        /** 景点列表缓存TTL(秒) */
        private long attractionListTtlSec = 90;

        /** 景点详情缓存TTL(秒) */
        private long attractionDetailTtlSec = 300;

        /** 热门景点缓存TTL(秒) */
        private long topAttractionTtlSec = 120;

        /** 推荐结果缓存TTL(秒) */
        private long recommendTtlSec = 45;

        /** 路线规划缓存TTL(秒) */
        private long routePlanTtlSec = 180;

        /** 空值缓存TTL(秒) */
        private long nullValueTtlSec = 30;

        /** 缓存TTL抖动上限(秒)，用于缓解雪崩 */
        private long ttlJitterSec = 30;
    }

    @Data
    public static class Executor {
        /** 通用异步线程池核心线程数 */
        private int generalCorePoolSize = 8;

        /** 通用异步线程池最大线程数 */
        private int generalMaxPoolSize = 32;

        /** 路线规划线程池核心线程数 */
        private int routeCorePoolSize = 4;

        /** 路线规划线程池最大线程数 */
        private int routeMaxPoolSize = 16;

        /** 队列容量 */
        private int queueCapacity = 1024;

        /** 线程空闲保活时间(秒) */
        private int keepAliveSeconds = 60;
    }
}
