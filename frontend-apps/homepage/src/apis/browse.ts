import { post } from "./request";
import type { ApiResponse } from "./common";

/**
 * 浏览记录请求接口
 */
export interface BrowseRecordRequest {
  userId: number;
  attractionId: number;
  browseDuration: number; // 浏览时长（秒）
  deviceInfo?: string; // 设备信息（可选）
}

/**
 * 记录浏览行为
 */
export const recordBrowse = (
  data: BrowseRecordRequest
): Promise<ApiResponse<string>> => {
  return post("/api/browse/record", data);
};
