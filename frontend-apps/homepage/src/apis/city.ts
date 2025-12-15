import { post } from "./request";
import type { ApiResponse, PageResponse } from "./common";

export interface CityQueryRequest {
  pageNum: number;
  pageSize: number;
  cityName?: string;
  country?: string;
  status?: number;
}

export interface CityCard {
  cityName: string;
  country: string;
  description: string;
  cityUrl: string;
  averageTemperature: number;
  attractionCount: number;
  popularity: number;
}

/**
 * 获取城市卡片信息（分页）
 * @param request 查询请求参数
 * @returns 城市卡片分页响应
 */

export function getCityCards(request: CityQueryRequest) {
    return post<ApiResponse<PageResponse<CityCard>>>("/api/city/list", request);
}

