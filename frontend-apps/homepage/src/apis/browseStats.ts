import { get } from "./request";
import type { ApiResponse } from "./common";

/**
 * 浏览记录统计数据
 */
export interface BrowseStatistics {
  attractionId: number;
  attractionName: string;
  totalViews: number; // 总浏览次数
  totalDuration: number; // 总浏览时长（秒）
  averageDuration: number; // 平均浏览时长（秒）
  uniqueUsers: number; // 独立访客数
}

/**
 * 用户浏览历史
 */
export interface UserBrowseHistory {
  browseRecordId: number;
  userId: number;
  attractionId: number;
  attractionName?: string;
  browseDuration: number;
  browseTime: string;
  deviceInfo?: string;
}

/**
 * 获取景点浏览统计
 */
export const getAttractionBrowseStats = (
  attractionId: number
): Promise<ApiResponse<BrowseStatistics>> => {
  return get(`/api/browse/stats/attraction/${attractionId}`);
};

/**
 * 获取用户浏览历史
 */
export const getUserBrowseHistory = (
  userId: number,
  page: number = 1,
  size: number = 10
): Promise<ApiResponse<UserBrowseHistory[]>> => {
  return get("/api/browse/history", { userId, page, size });
};

/**
 * 获取热门景点（按浏览量排序）
 */
export const getPopularAttractions = (
  limit: number = 10
): Promise<ApiResponse<BrowseStatistics[]>> => {
  return get("/api/browse/stats/popular", { limit });
};
