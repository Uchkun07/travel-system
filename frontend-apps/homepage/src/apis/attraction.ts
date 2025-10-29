import { get, post, del } from "./request";

// API 响应接口
export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

// 景点信息接口
export interface Attraction {
  attractionId: number;
  name: string;
  description: string;
  type: string;
  location: string;
  latitude: number;
  longitude: number;
  imageUrl: string;
  averageRating: number;
  viewCount: number;
  popularity: number;
  estimatedTime: number;
  ticketPrice: number;
  openingHours: string;
  createTime?: string;
  updateTime?: string;
}

// 收藏操作响应接口
export interface CollectResponseData {
  collected?: boolean;
  uncollected?: boolean;
}

// 收藏状态响应接口
export interface CollectionStatusResponseData {
  collected: boolean;
}

/**
 * 获取所有景点
 * @returns 景点列表
 */
export function getAllAttractions() {
  return get<Attraction[]>("/attractions/list");
}

/**
 * 收藏景点
 * @param attractionId 景点ID
 */
export function collectAttraction(attractionId: number) {
  return post<ApiResponse<CollectResponseData>>(
    `/collections/attractions/${attractionId}`
  );
}

/**
 * 取消收藏景点
 * @param attractionId 景点ID
 */
export function uncollectAttraction(attractionId: number) {
  return del<ApiResponse<CollectResponseData>>(
    `/collections/attractions/${attractionId}`
  );
}

/**
 * 获取当前用户收藏的所有景点ID列表
 * @returns 景点ID数组
 */
export function getCollectedAttractionIds() {
  return get<ApiResponse<number[]>>("/collections/attractions/ids");
}

/**
 * 检查景点是否已收藏
 * @param attractionId 景点ID
 * @returns 收藏状态
 */
export function checkCollectionStatus(attractionId: number) {
  return get<ApiResponse<CollectionStatusResponseData>>(
    `/collections/attractions/${attractionId}/status`
  );
}