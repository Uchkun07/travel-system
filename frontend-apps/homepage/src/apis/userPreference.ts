import { get, put, del } from "./request";
import type { ApiResponse } from "./common";

// 用户偏好请求接口
export interface UserPreferenceRequest {
  preferAttractionTypeId?: number;
  budgetFloor?: number;
  budgetRange?: number;
  travelCrowd?: string;
  preferSeason?: string;
}

// 用户偏好响应接口
export interface UserPreferenceResponse {
  preferenceId: number;
  userId: number;
  preferAttractionTypeId?: number;
  attractionTypeName?: string;
  budgetFloor?: number;
  budgetRange?: number;
  travelCrowd?: string;
  preferSeason?: string;
  createTime: string;
  updateTime: string;
}

/**
 * 获取用户偏好
 */
export function getUserPreference() {
  return get<ApiResponse<UserPreferenceResponse>>("/api/user/preference");
}

/**
 * 更新用户偏好
 */
export function updateUserPreference(data: UserPreferenceRequest) {
  return put<ApiResponse<UserPreferenceResponse>>("/api/user/preference", data);
}

/**
 * 删除用户偏好
 */
export function deleteUserPreference() {
  return del<ApiResponse<void>>("/api/user/preference");
}
