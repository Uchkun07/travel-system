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
    private Monitor monitor = new Monitor();

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

    @Data
    public static class Monitor {
        /** 推荐结果预计算开关 */
        private boolean recommendPrecomputeEnabled = true;

        /** 推荐预计算执行间隔(ms) */
        private long recommendPrecomputeIntervalMs = 300000;

        /** 推荐预计算首次延迟(ms) */
        private long recommendPrecomputeInitialDelayMs = 45000;

        /** 推荐预计算活跃用户时间窗口(小时) */
        private int precomputeActiveWindowHours = 24;

        /** 推荐预计算用户上限 */
        private int precomputeUserLimit = 200;

        /** 推荐预计算页码 */
        private long precomputePageNum = 1;

        /** 推荐预计算每页数量 */
        private long precomputePageSize = 20;

        /** 是否包含游客缓存(匿名用户) */
        private boolean includeGuestPrecompute = true;

        /** 热点Key监控开关 */
        private boolean hotKeyEnabled = true;

        /** 热点Key采样率(0~1) */
        private double hotKeySampleRate = 1.0;

        /** 每分钟热点Key告警阈值 */
        private long hotKeyThresholdPerMinute = 80;

        /** 超过阈值后每多少次追加告警 */
        private long hotKeyAlertStep = 40;

        /** 热点Key汇总TopN */
        private int hotKeyTopN = 10;

        /** 热点Key汇总间隔(ms) */
        private long hotKeyReportIntervalMs = 60000;

        /** 慢SQL采样开关 */
        private boolean slowSqlEnabled = true;

        /** 慢SQL阈值(ms) */
        private long slowSqlThresholdMs = 300;

        /** 慢SQL采样率(0~1) */
        private double slowSqlSampleRate = 0.3;

        /** 慢SQL窗口分析TopN */
        private int slowSqlReportTopN = 5;

        /** 慢SQL分析汇总间隔(ms) */
        private long slowSqlReportIntervalMs = 60000;

        /** 单条慢SQL样本最大长度 */
        private int slowSqlSampleSqlMaxLen = 500;
    }
}
