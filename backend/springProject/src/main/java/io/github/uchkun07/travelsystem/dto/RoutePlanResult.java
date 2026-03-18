package io.github.uchkun07.travelsystem.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 路线规划结果
 */
@Data
@Builder
public class RoutePlanResult {

    /** 路线整体摘要 */
    private Summary summary;

    /** 按游览顺序排列的景点步骤列表 */
    private List<RouteStep> steps;

    /** 是否超出预算 */
    private boolean overBudget;

    /** 提示信息（超预算 / 降级提示等） */
    private String tips;

    // ───────────── 内嵌类 ─────────────────────────────────────────────────────

    @Data
    @Builder
    public static class Summary {
        /** 总费用（路费 + 门票），单位：元 */
        private double totalCost;
        /** 总路途时间（分钟） */
        private long totalDurationMinutes;
        /** 总行驶/乘车距离（公里） */
        private double totalDistanceKm;
        /** 出行偏好 */
        private String preference;
        /** 出行方式 */
        private String travelMode;
    }

    @Data
    @Builder
    public static class RouteStep {
        /** 本景点在路线中的顺序编号（从 1 开始） */
        private int order;

        // ── 景点基础信息 ──────────────────────────────────────────────────────
        private Long attractionId;
        private String name;
        private String address;
        private double latitude;
        private double longitude;
        private String imageUrl;
        private double ticketPrice;
        private double averageRating;

        // ── 到达信息（从上一节点出发抵达本景点） ─────────────────────────────
        /** 预计到达日期，格式 yyyy-MM-dd */
        private String arrivalDate;
        /** 从上一节点到本景点的路途耗时（分钟） */
        private long travelDurationMinutes;
        /** 从上一节点到本景点的距离（公里） */
        private double travelDistanceKm;
        /** 从上一节点到本景点的通行费用（油费/车票），单位：元 */
        private double travelCost;

        // ── 景点游览信息 ─────────────────────────────────────────────────────
        /** 建议游览时长（分钟） */
        private int estimatedPlayMinutes;

        // ── 模拟数据（预留接口对接真实 API） ────────────────────────────────
        /** 模拟天气描述，如"晴，18℃" */
        private String weather;
        /** 模拟人流量：低/中/高 */
        private String crowdLevel;
    }
}
