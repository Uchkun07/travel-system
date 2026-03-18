import request from "./request";
import type { ApiResponse } from "./request";

// ── 类型定义 ──────────────────────────────────────────────────────────────────

export interface OverviewStats {
  totalUsers: number;
  todayNewUsers: number;
  totalAttractions: number;
  totalRoutePlans: number;
  totalCities: number;
  slideshowClicks: number;
}

export interface TrendData {
  dates: string[];
  userGrowth: number[];
  planGrowth: number[];
}

export interface AttractionStat {
  name: string;
  views: number;
}

export interface DistItem {
  name: string;
  value: number;
}

export interface RecentRoute {
  username: string;
  departure: string;
  travelMode: string;
  travelGroup: string;
  budget: string;
  createdAt: string;
}

export interface DashboardData {
  overview: OverviewStats;
  thirtyDayTrend: TrendData;
  topAttractions: AttractionStat[];
  groupDist: DistItem[];
  modeDist: DistItem[];
  prefDist: DistItem[];
  recentRoutes: RecentRoute[];
}

// ── API 调用 ──────────────────────────────────────────────────────────────────

/**
 * 获取 Dashboard 大屏全量数据
 */
export const getDashboardData = (): Promise<ApiResponse<DashboardData>> => {
  return request.get("/api/admin/dashboard");
};
