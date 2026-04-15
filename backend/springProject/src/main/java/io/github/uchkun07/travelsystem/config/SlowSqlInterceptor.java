package io.github.uchkun07.travelsystem.config;

import io.github.uchkun07.travelsystem.monitor.SlowSqlAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;

import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * MyBatis 慢SQL拦截器
 * 对超过阈值的 SQL 按采样率记录到分析服务。
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {
                MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class
        }),
        @Signature(type = Executor.class, method = "query", args = {
                MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class
        })
})
public class SlowSqlInterceptor implements Interceptor {

    private final PerformanceProperties performanceProperties;
    private final SlowSqlAnalysisService slowSqlAnalysisService;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        long startNs = System.nanoTime();
        Throwable throwable = null;
        try {
            return invocation.proceed();
        } catch (Throwable t) {
            throwable = t;
            throw t;
        } finally {
            PerformanceProperties.Monitor monitor = performanceProperties.getMonitor();
            if (monitor.isSlowSqlEnabled()) {
                long elapsedMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
                if (elapsedMs >= Math.max(1L, monitor.getSlowSqlThresholdMs())
                        && hitSample(monitor.getSlowSqlSampleRate())) {
                    String statementId = "UNKNOWN_STATEMENT";
                    String sql = "UNKNOWN_SQL";
                    try {
                        Object[] args = invocation.getArgs();
                        MappedStatement mappedStatement = (MappedStatement) args[0];
                        statementId = mappedStatement.getId();
                        Object parameter = args.length > 1 ? args[1] : null;
                        BoundSql boundSql = resolveBoundSql(mappedStatement, args, parameter);
                        if (boundSql != null) {
                            sql = boundSql.getSql();
                        }
                    } catch (Exception ex) {
                        log.debug("慢SQL上下文解析失败", ex);
                    }

                    slowSqlAnalysisService.recordSlowSql(statementId, sql, elapsedMs, throwable);
                }
            }
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // no-op
    }

    private BoundSql resolveBoundSql(MappedStatement mappedStatement, Object[] args, Object parameter) {
        if (args.length >= 6 && args[5] instanceof BoundSql boundSql) {
            return boundSql;
        }
        return mappedStatement.getBoundSql(parameter);
    }

    private boolean hitSample(double sampleRate) {
        if (sampleRate >= 1.0) {
            return true;
        }
        if (sampleRate <= 0) {
            return false;
        }
        return ThreadLocalRandom.current().nextDouble() <= sampleRate;
    }
}
