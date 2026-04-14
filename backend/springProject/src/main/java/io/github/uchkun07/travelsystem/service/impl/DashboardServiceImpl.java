package io.github.uchkun07.travelsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.uchkun07.travelsystem.dto.DashboardDTO;
import io.github.uchkun07.travelsystem.entity.*;
import io.github.uchkun07.travelsystem.mapper.*;
import io.github.uchkun07.travelsystem.service.IDashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Dashboard 数据大屏 Service 实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements IDashboardService {

    private final UserMapper userMapper;
    private final AttractionMapper attractionMapper;
    private final TravelRouteMapper travelRouteMapper;
    private final CityMapper cityMapper;
    private final SlideshowMapper slideshowMapper;
    private final DashboardMapper dashboardMapper;

    @Override
    public DashboardDTO getDashboardData() {
        DashboardDTO dto = new DashboardDTO();

        // ── 1. 概览卡 ────────────────────────────────────────────────────────
        DashboardDTO.OverviewStats overview = buildOverviewStats();
        dto.setOverview(overview);

        // ── 2. 近 30 天趋势 ──────────────────────────────────────────────────
        dto.setThirtyDayTrend(buildTrend());

        // ── 3. 热门景点 TOP 10 ───────────────────────────────────────────────
        dto.setTopAttractions(dashboardMapper.selectTopAttractions());

        // ── 4. 分布图表 ──────────────────────────────────────────────────────
        dto.setGroupDist(emptyToDefault(dashboardMapper.selectGroupDist(),
                defaultGroupDist()));
        dto.setModeDist(emptyToDefault(dashboardMapper.selectModeDist(),
                defaultModeDist()));
        dto.setPrefDist(emptyToDefault(dashboardMapper.selectPrefDist(),
                defaultPrefDist()));

        // ── 5. 最近 10 条路线记录 ─────────────────────────────────────────────
        dto.setRecentRoutes(buildRecentRoutes());

        return dto;
    }

    // ────────────────────────── 私有辅助方法 ──────────────────────────────────

    /** 构建概览卡数据 */
    private DashboardDTO.OverviewStats buildOverviewStats() {
        DashboardDTO.OverviewStats stats = new DashboardDTO.OverviewStats();

        // 累计用户数（全部用户）
        stats.setTotalUsers(userMapper.selectCount(null));

        // 今日新增用户
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        stats.setTodayNewUsers(userMapper.selectCount(
                new LambdaQueryWrapper<User>().ge(User::getCreateTime, startOfDay)));

        // 总景点数（status=1 正常上架，@TableLogic 自动过滤 is_deleted）
        stats.setTotalAttractions(attractionMapper.selectCount(
                new LambdaQueryWrapper<Attraction>().eq(Attraction::getStatus, 1)));

        // 路线规划总次数（@TableLogic 自动过滤 is_deleted）
        stats.setTotalRoutePlans(travelRouteMapper.selectCount(null));

        // 总城市数
        stats.setTotalCities(cityMapper.selectCount(null));

        // 轮播图点击量总和
        List<Slideshow> slideshows = slideshowMapper.selectList(null);
        long totalClicks = slideshows.stream()
                .mapToLong(s -> s.getClickCount() == null ? 0L : s.getClickCount())
                .sum();
        stats.setSlideshowClicks(totalClicks);

        return stats;
    }

    /** 构建近 30 天趋势数据（补全无数据日期为 0） */
    private DashboardDTO.TrendData buildTrend() {
        // 生成近 30 天的日期标签列表
        List<String> dates = new ArrayList<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM/dd");
        for (int i = 29; i >= 0; i--) {
            dates.add(today.minusDays(i).format(fmt));
        }

        // 查询 DB 结果，转为 Map<日期标签, count>
        Map<String, Long> userMap = toDateMap(dashboardMapper.selectDailyNewUsers());
        Map<String, Long> routeMap = toDateMap(dashboardMapper.selectDailyNewRoutes());

        // 按顺序填充，缺失日期补 0
        List<Long> userGrowth = new ArrayList<>();
        List<Long> planGrowth = new ArrayList<>();
        for (String date : dates) {
            userGrowth.add(userMap.getOrDefault(date, 0L));
            planGrowth.add(routeMap.getOrDefault(date, 0L));
        }

        DashboardDTO.TrendData trend = new DashboardDTO.TrendData();
        trend.setDates(dates);
        trend.setUserGrowth(userGrowth);
        trend.setPlanGrowth(planGrowth);
        return trend;
    }

    /** 将 Mapper 查询结果 [{date, cnt}] 转为 Map<date, cnt> */
    private Map<String, Long> toDateMap(List<Map<String, Object>> rows) {
        Map<String, Long> map = new HashMap<>();
        for (Map<String, Object> row : rows) {
            String date = String.valueOf(row.get("date"));
            long cnt = ((Number) row.get("cnt")).longValue();
            map.put(date, cnt);
        }
        return map;
    }

    /** 将最近路线记录行转为 DTO 列表 */
    private List<DashboardDTO.RecentRoute> buildRecentRoutes() {
        List<Map<String, Object>> rows = dashboardMapper.selectRecentRoutes();
        List<DashboardDTO.RecentRoute> result = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            DashboardDTO.RecentRoute r = new DashboardDTO.RecentRoute();
            r.setUsername(str(row.get("username"), "—"));
            r.setDeparture(str(row.get("departure"), "—"));
            r.setTravelMode(str(row.get("travelMode"), "—"));
            r.setTravelGroup(str(row.get("travelGroup"), "—"));
            r.setBudget(str(row.get("budget"), "—"));
            r.setCreatedAt(str(row.get("createdAt"), "—"));
            result.add(r);
        }
        return result;
    }

    /** Object → String，null 或 "null" 时返回 fallback */
    private String str(Object val, String fallback) {
        if (val == null) return fallback;
        String s = val.toString();
        return ("null".equals(s) || s.isEmpty()) ? fallback : s;
    }

    /** 当实际查询结果为空时，使用预置默认数据（保证图表不空白） */
    private List<DashboardDTO.DistItem> emptyToDefault(
            List<DashboardDTO.DistItem> actual,
            List<DashboardDTO.DistItem> defaults) {
        return (actual == null || actual.isEmpty()) ? defaults : actual;
    }

    // ────────────────── 默认占位分布数据（无真实数据时展示） ──────────────────

    private List<DashboardDTO.DistItem> defaultGroupDist() {
        return List.of(
                item("独自", 25), item("亲子", 18),
                item("家庭", 37), item("朋友", 20));
    }

    private List<DashboardDTO.DistItem> defaultModeDist() {
        return List.of(item("自驾", 45), item("火车", 30), item("飞机", 25));
    }

    private List<DashboardDTO.DistItem> defaultPrefDist() {
        return List.of(
                item("经济", 30), item("舒适", 40), item("适中", 15),
                item("小众", 8), item("深度", 7));
    }

    private DashboardDTO.DistItem item(String name, long value) {
        DashboardDTO.DistItem d = new DashboardDTO.DistItem();
        d.setName(name);
        d.setValue(value);
        return d;
    }
}
