import { post } from "./request";
import type { ApiResponse } from "./common";

// ── 请求参数类型 ────────────────────────────────────────────────────────────

export interface RoutePlanRequest {
  departure: string;
  budget: number;
  departureDate: string;
  travelMode: string;
  travelGroup: string;
  travelPreference: string;
  attractionIds: number[];
}

// ── 返回类型 ────────────────────────────────────────────────────────────────

export interface RouteSummary {
  totalCost: number;
  totalDurationMinutes: number;
  totalDistanceKm: number;
  preference: string;
  travelMode: string;
}

export interface RouteStep {
  order: number;
  attractionId: number;
  name: string;
  address: string;
  latitude: number;
  longitude: number;
  imageUrl: string;
  ticketPrice: number;
  averageRating: number;
  arrivalDate: string;
  travelDurationMinutes: number;
  travelDistanceKm: number;
  travelCost: number;
  estimatedPlayMinutes: number;
  /** 模拟天气，如 "晴，22℃" */
  weather: string;
  /** 人流量：低/中/高 */
  crowdLevel: string;
}

export interface RoutePlanResult {
  summary: RouteSummary;
  steps: RouteStep[];
  overBudget: boolean;
  tips: string;
}

// ── API 调用 ────────────────────────────────────────────────────────────────

/**
 * 提交路线规划请求
 * POST /api/route/plan
 */
export const planRoute = (
  params: RoutePlanRequest,
): Promise<ApiResponse<RoutePlanResult>> => {
  return post("/api/route/plan", params);
};
