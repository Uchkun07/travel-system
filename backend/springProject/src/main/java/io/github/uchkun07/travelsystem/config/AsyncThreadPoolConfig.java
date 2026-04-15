package io.github.uchkun07.travelsystem.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步线程池配置
 */
@Configuration
@RequiredArgsConstructor
public class AsyncThreadPoolConfig {

    private final PerformanceProperties performanceProperties;

    @Bean("generalAsyncExecutor")
    public ThreadPoolTaskExecutor generalAsyncExecutor() {
        return buildExecutor(
                "general-async-",
                performanceProperties.getExecutor().getGeneralCorePoolSize(),
                performanceProperties.getExecutor().getGeneralMaxPoolSize());
    }

    @Bean("routePlanExecutor")
    public ThreadPoolTaskExecutor routePlanExecutor() {
        return buildExecutor(
                "route-plan-",
                performanceProperties.getExecutor().getRouteCorePoolSize(),
                performanceProperties.getExecutor().getRouteMaxPoolSize());
    }

    @Bean("cacheRebuildExecutor")
    public ThreadPoolTaskExecutor cacheRebuildExecutor() {
        return buildExecutor("cache-rebuild-", 2, 8);
    }

    private ThreadPoolTaskExecutor buildExecutor(String prefix, int corePoolSize, int maxPoolSize) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix(prefix);
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(performanceProperties.getExecutor().getQueueCapacity());
        executor.setKeepAliveSeconds(performanceProperties.getExecutor().getKeepAliveSeconds());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        // 高并发下触发限流回压，避免请求被直接丢弃
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
