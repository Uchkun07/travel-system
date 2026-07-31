package io.github.uchkun07.travelsystem.monitor;

import io.github.uchkun07.travelsystem.config.PerformanceProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * 慢SQL采样与分析服务
 * 按窗口聚合慢SQL样本并输出分析报告。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SlowSqlAnalysisService {

    private static final int LATENCY_SAMPLE_CAP = 128;

    private final PerformanceProperties performanceProperties;

    private final ConcurrentHashMap<String, SlowSqlStat> statMap = new ConcurrentHashMap<>();

    public void recordSlowSql(String statementId, String sql, long elapsedMs, Throwable throwable) {
        PerformanceProperties.Monitor monitor = performanceProperties.getMonitor();
        if (!monitor.isSlowSqlEnabled()) {
            return;
        }

        String normalizedSql = normalizeSql(sql);
        String fingerprint = statementId + " | " + normalizedSql;
        String sampleSql = abbreviate(sql, Math.max(120, monitor.getSlowSqlSampleSqlMaxLen()));

        statMap.compute(fingerprint, (ignored, existing) -> {
            SlowSqlStat stat = existing == null
                    ? new SlowSqlStat(statementId, normalizedSql, sampleSql)
                    : existing;
            stat.record(elapsedMs, throwable);
            return stat;
        });
    }

    @Scheduled(
            fixedDelayString = "${performance.monitor.slow-sql-report-interval-ms:60000}",
            initialDelayString = "${performance.monitor.slow-sql-report-interval-ms:60000}")
    public void report() {
        PerformanceProperties.Monitor monitor = performanceProperties.getMonitor();
        if (!monitor.isSlowSqlEnabled() || statMap.isEmpty()) {
            return;
        }

        List<SlowSqlSnapshot> snapshots = statMap.values().stream()
                .map(SlowSqlStat::snapshot)
                .sorted(Comparator.comparingDouble(SlowSqlSnapshot::avgMs).reversed())
                .limit(Math.max(1, monitor.getSlowSqlReportTopN()))
                .toList();

        int fingerprintCount = statMap.size();
        statMap.clear();

        if (snapshots.isEmpty()) {
            return;
        }

        log.warn("慢SQL分析窗口报告 fingerprints={}, topN={}", fingerprintCount, snapshots.size());
        int idx = 1;
        for (SlowSqlSnapshot snapshot : snapshots) {
            log.warn(
                    "慢SQL#{} statementId={}, count={}, avgMs={}, p95Ms={}, maxMs={}, errorRate={}%, sql={}",
                    idx++,
                    snapshot.statementId(),
                    snapshot.count(),
                    snapshot.avgMs(),
                    snapshot.p95Ms(),
                    snapshot.maxMs(),
                    snapshot.errorRatePercent(),
                    snapshot.sampleSql());
        }
    }

    private String normalizeSql(String sql) {
        if (!StringUtils.hasText(sql)) {
            return "UNKNOWN_SQL";
        }
        String normalized = sql.replaceAll("\\s+", " ").trim();
        normalized = normalized.replaceAll("'[^']*'", "?");
        normalized = normalized.replaceAll("\\b\\d+\\b", "?");
        return abbreviate(normalized, 300);
    }

    private String abbreviate(String value, int maxLen) {
        if (!StringUtils.hasText(value)) {
            return "";
        }
        if (value.length() <= maxLen) {
            return value;
        }
        return value.substring(0, maxLen) + "...";
    }

    private static final class SlowSqlStat {
        private final String statementId;
        private final String normalizedSql;
        private volatile String sampleSql;

        private final LongAdder count = new LongAdder();
        private final LongAdder totalMs = new LongAdder();
        private final LongAdder errorCount = new LongAdder();
        private final AtomicLong maxMs = new AtomicLong();
        private final List<Long> latencySamples = new ArrayList<>();

        private SlowSqlStat(String statementId, String normalizedSql, String sampleSql) {
            this.statementId = statementId;
            this.normalizedSql = normalizedSql;
            this.sampleSql = sampleSql;
        }

        private void record(long elapsedMs, Throwable throwable) {
            count.increment();
            totalMs.add(elapsedMs);
            maxMs.accumulateAndGet(elapsedMs, Math::max);
            if (throwable != null) {
                errorCount.increment();
            }
            synchronized (latencySamples) {
                if (latencySamples.size() >= LATENCY_SAMPLE_CAP) {
                    latencySamples.remove(0);
                }
                latencySamples.add(elapsedMs);
            }
            if (elapsedMs > maxMs.get()) {
                sampleSql = normalizedSql;
            }
        }

        private SlowSqlSnapshot snapshot() {
            long c = Math.max(1L, count.sum());
            long total = totalMs.sum();
            long max = maxMs.get();
            long error = errorCount.sum();
            double avg = (double) total / c;
            long p95;
            synchronized (latencySamples) {
                if (latencySamples.isEmpty()) {
                    p95 = max;
                } else {
                    List<Long> sorted = new ArrayList<>(latencySamples);
                    sorted.sort(Long::compare);
                    int idx = (int) Math.ceil(sorted.size() * 0.95) - 1;
                    idx = Math.max(0, Math.min(idx, sorted.size() - 1));
                    p95 = sorted.get(idx);
                }
            }
            double errorRate = (error * 100.0) / c;
            return new SlowSqlSnapshot(statementId, c, round2(avg), p95, max, round2(errorRate), sampleSql);
        }

        private double round2(double value) {
            return Math.round(value * 100.0) / 100.0;
        }
    }

    private record SlowSqlSnapshot(String statementId,
                                   long count,
                                   double avgMs,
                                   long p95Ms,
                                   long maxMs,
                                   double errorRatePercent,
                                   String sampleSql) {
    }
}
