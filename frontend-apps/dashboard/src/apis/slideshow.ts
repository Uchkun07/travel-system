import { post, get, put, del } from "./request";

// 分页响应接口
export interface PageResponse<T> {
  records: T[];
  total: number;
  pageNum: number;
  pageSize: number;
  totalPages: number;
  hasPrevious: boolean;
  hasNext: boolean;
}

// 轮播图接口定义
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

// 创建轮播图请求
export interface CreateSlideshowRequest {
  title: string;
  subtitle?: string;
  imageUrl: string;
  attractionId?: number;
  displayOrder?: number;
  status?: number;
  startTime?: string;
  endTime?: string;
}

// 更新轮播图请求
export interface UpdateSlideshowRequest {
  slideshowId: number;
  title?: string;
  subtitle?: string;
  imageUrl?: string;
  attractionId?: number;
  displayOrder?: number;
  status?: number;
  startTime?: string;
  endTime?: string;
}

// 分页查询轮播图请求
export interface QuerySlideshowsRequest {
  pageNum?: number;
  pageSize?: number;
  title?: string;
  status?: number;
  attractionId?: number;
}

// 创建轮播图
export function createSlideshow(params: CreateSlideshowRequest) {
  return post<Slideshow>("/api/slideshow/create", params);
}

// 更新轮播图
export function updateSlideshow(params: UpdateSlideshowRequest) {
  return put<Slideshow>("/api/slideshow/update", params);
}

// 删除轮播图
export function deleteSlideshow(slideshowId: number) {
  return del<void>(`/api/slideshow/delete/${slideshowId}`);
}

// 批量删除轮播图
export function batchDeleteSlideshows(slideshowIds: number[]) {
  return del<void>("/api/slideshow/batch-delete", {
    data: slideshowIds,
  });
}

// 分页查询轮播图
export function querySlideshows(params?: QuerySlideshowsRequest) {
  return get<PageResponse<Slideshow>>("/api/slideshow/list", params);
}

// 查询轮播图详情
export function getSlideshowById(slideshowId: number) {
  return get<Slideshow>(`/api/slideshow/detail/${slideshowId}`);
}

// 获取启用的轮播图
export function getActiveSlideshows() {
  return get<Slideshow[]>("/api/slideshow/active");
}
