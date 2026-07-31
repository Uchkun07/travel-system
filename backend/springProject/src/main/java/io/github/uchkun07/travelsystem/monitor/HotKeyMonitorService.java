package io.github.uchkun07.travelsystem.monitor;

import io.github.uchkun07.travelsystem.config.PerformanceProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 热点Key监控与告警
 * 在分钟窗口内统计访问次数，并输出TopN与阈值告警日志。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HotKeyMonitorService {

    private final PerformanceProperties performanceProperties;

    private final ConcurrentHashMap<String, HotKeyCounter> minuteCounters = new ConcurrentHashMap<>();

    public void recordAccess(String key) {
        PerformanceProperties.Monitor monitor = performanceProperties.getMonitor();
        if (!monitor.isHotKeyEnabled() || key == null || key.isBlank()) {
            return;
        }

        if (!hitSample(monitor.getHotKeySampleRate())) {
            return;
        }

        long minute = currentMinute();
        String shortKey = abbreviate(key, 160);
        String mapKey = minute + "|" + shortKey;

        HotKeyCounter counter = minuteCounters.computeIfAbsent(
                mapKey,
                ignored -> new HotKeyCounter(minute, shortKey));
        long count = counter.count().incrementAndGet();

        long threshold = Math.max(1, monitor.getHotKeyThresholdPerMinute());
        long step = Math.max(1, monitor.getHotKeyAlertStep());
        if (count >= threshold && (count - threshold) % step == 0) {
            log.warn("热点Key告警 key={}, minute={}, hits={}", shortKey, minute, count);
        }
    }

    @Scheduled(
            fixedDelayString = "${performance.monitor.hot-key-report-interval-ms:60000}",
            initialDelayString = "${performance.monitor.hot-key-report-interval-ms:60000}")
    public void reportTopHotKeys() {
        PerformanceProperties.Monitor monitor = performanceProperties.getMonitor();
        if (!monitor.isHotKeyEnabled()) {
            return;
        }

        long current = currentMinute();
        long reportMinute = current - 1;
        int topN = Math.max(1, monitor.getHotKeyTopN());

        List<HotKeySnapshot> topKeys = minuteCounters.values().stream()
                .filter(counter -> counter.minute() == reportMinute)
                .map(counter -> new HotKeySnapshot(counter.key(), counter.count().get()))
                .sorted(Comparator.comparingLong(HotKeySnapshot::count).reversed())
                .limit(topN)
                .toList();

        if (!topKeys.isEmpty()) {
            log.warn("热点Key窗口报告 minute={}, topN={}", reportMinute, topKeys.size());
            for (HotKeySnapshot snapshot : topKeys) {
                log.warn("热点Key minute={} key={} hits={}", reportMinute, snapshot.key(), snapshot.count());
            }
        }

        minuteCounters.entrySet().removeIf(entry -> entry.getValue().minute() <= reportMinute);
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

    private long currentMinute() {
        return System.currentTimeMillis() / 60000;
    }

    private String abbreviate(String value, int maxLength) {
        if (value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength) + "...";
    }

    private record HotKeyCounter(long minute, String key, AtomicLong count) {
        private HotKeyCounter(long minute, String key) {
            this(minute, key, new AtomicLong());
        }
    }

    private record HotKeySnapshot(String key, long count) {
    }
}
