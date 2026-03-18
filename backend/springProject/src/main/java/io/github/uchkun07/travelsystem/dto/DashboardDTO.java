package io.github.uchkun07.travelsystem.dto;

import lombok.Data;
import java.util.List;

/**
 * Dashboard 数据大屏聚合 DTO
 */
@Data
public class DashboardDTO {

    /** 顶部概览卡数据 */
    private OverviewStats overview;

    /** 近 30 天趋势（用户增长 + 路线规划次数） */
    private TrendData thirtyDayTrend;

    /** 热门景点 TOP10（按浏览量） */
    private List<AttractionStat> topAttractions;

    /** 出行人群分布 */
    private List<DistItem> groupDist;

    /** 出行方式分布 */
    private List<DistItem> modeDist;

    /** 出行偏好分布 */
    private List<DistItem> prefDist;

    /** 最近 10 条路线规划记录 */
    private List<RecentRoute> recentRoutes;

    // ──────────────────────────────── 内嵌类 ────────────────────────────────

    @Data
    public static class OverviewStats {
        /** 累计用户数 */
        private long totalUsers;
        /** 今日新增用户 */
        private long todayNewUsers;
        /** 总景点数 */
        private long totalAttractions;
        /** 路线规划总次数 */
        private long totalRoutePlans;
        /** 总城市数 */
        private long totalCities;
        /** 轮播图点击量（所有轮播图 click_count 总和） */
        private long slideshowClicks;
    }

    @Data
    public static class TrendData {
        /** 日期标签列表，如 ["3/1", "3/2", ...] */
        private List<String> dates;
        /** 每日新增用户数（与 dates 一一对应） */
        private List<Long> userGrowth;
        /** 每日新增路线规划数（与 dates 一一对应） */
        private List<Long> planGrowth;
    }

    @Data
    public static class AttractionStat {
        private String name;
        private int views;
    }

    @Data
    public static class DistItem {
        private String name;
        private long value;
    }

    @Data
    public static class RecentRoute {
        private String username;
        private String departure;
        private String travelMode;
        private String travelGroup;
        private String budget;
        private String createdAt;
    }
}
