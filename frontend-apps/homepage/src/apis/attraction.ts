import { get, post, del } from "./request";
import type { ApiResponse, PageResponse } from "./common";

// 景点卡片信息接口(C端列表展示)
export interface AttractionCard {
  attractionId: number;
  name: string;
  description: string; // 对应后端的subtitle
  type: string;
  location: string;
  imageUrl: string;
  averageRating?: number;
  viewCount: number;
  popularity: number;
  ticketPrice?: number;
}

// 景点详情信息接口(C端详情页)
export interface AttractionDetail {
  attractionId: number;
  name: string;
  subtitle?: string;
  description?: string;
  typeId?: number;
  typeName?: string;
  cityId?: number;
  cityName?: string;
  address?: string;
  latitude?: number;
  longitude?: number;
  mainImageUrl?: string;
  multiImageUrls?: string;
  estimatedPlayTime?: number;
  ticketPrice?: number;
  ticketDescription?: string;
  openingHours?: string;
  bestSeason?: string;
  historicalContext?: string;
  culturalSignificance?: string;
  safetyTips?: string;
  accessibilityInfo?: string;
  nearbyFood?: string;
  nearbyAccommodation?: string;
  transportationGuide?: string;
  officialWebsite?: string;
  contactPhone?: string;
  averageRating?: number;
  browseCount?: number;
  popularity?: number;
  tags?: Array<{ tagId: number; tagName: string }>;
}

// 景点查询请求接口
export interface AttractionQueryRequest {
  pageNum: number;
  pageSize: number;
  attractionId?: number;
  name?: string;
  cityId?: number;
  typeId?: number;
  auditStatus?: number;
  status?: number;
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
 * 分页获取景点卡片列表(C端)
 * @param params 查询参数
 * @returns 分页景点列表
 */
export function getAttractionCards(params: AttractionQueryRequest) {
  return post<ApiResponse<PageResponse<AttractionCard>>>(
    "/api/attraction/list",
    params
  );
}

/**
 * 获取景点详情(C端)
 * @param attractionId 景点ID
 * @returns 景点详情
 */
export function getAttractionDetail(attractionId: number) {
  return get<ApiResponse<AttractionDetail>>(
    `/api/attraction/detail/${attractionId}`
  );
}

/**
 * 收藏景点
 * @param attractionId 景点ID
 */
export function collectAttraction(attractionId: number) {
  return post<ApiResponse<CollectResponseData>>(
    `/api/attraction/collection/${attractionId}`
  );
}

/**
 * 取消收藏景点
 * @param attractionId 景点ID
 */
export function uncollectAttraction(attractionId: number) {
  return del<ApiResponse<CollectResponseData>>(
    `/api/attraction/collection/${attractionId}`
  );
}

/**
 * 获取当前用户收藏的所有景点ID列表
 * @returns 景点ID数组
 */
export function getCollectedAttractionIds() {
  return get<ApiResponse<number[]>>("/api/attraction/collection/ids");
}

/**
 * 检查景点是否已收藏
 * @param attractionId 景点ID
 * @returns 收藏状态
 */
export function checkCollectionStatus(attractionId: number) {
  return get<ApiResponse<CollectionStatusResponseData>>(
    `/api/attraction/collection/${attractionId}/status`
  );
}

/**
 * 批量获取景点卡片信息(根据ID列表)
 * @param attractionIds 景点ID数组
 * @returns 景点卡片列表
 */
export function getAttractionsByIds(attractionIds: number[]) {
  return post<ApiResponse<AttractionCard[]>>(
    "/api/attraction/batch",
    attractionIds
  );
}
