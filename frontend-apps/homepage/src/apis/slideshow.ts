import { get, post } from "./request";

// 轮播图数据接口
export interface Slideshow {
  slideshowId: number;
  title: string;
  subtitle?: string;
  imageUrl: string;
  attractionId?: number;
  displayOrder: number;
  status: number;
  startTime?: string;
  endTime?: string;
  clickCount: number;
  createTime: string;
  updateTime: string;
}

// API 响应接口
export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

/**
 * 获取启用的轮播图(前台展示)
 */
export function getActiveSlideshow() {
  return get<ApiResponse<Slideshow[]>>("/api/home/slideshow/list");
}

/**
 * 记录轮播图点击
 */
export function recordClick(slideshowId: number) {
  return post<ApiResponse<null>>(`/api/home/slideshow/click/${slideshowId}`);
}

/**
 * 获取所有轮播图(管理后台)
 */
export function getAllSlideshow() {
  return get<ApiResponse<Slideshow[]>>("/slideshow/list");
}

/**
 * 根据ID获取轮播图详情
 */
export function getSlideshowById(id: number) {
  return get<ApiResponse<Slideshow>>(`/slideshow/${id}`);
}
