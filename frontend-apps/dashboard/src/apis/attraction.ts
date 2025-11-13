import { post, get, put, del } from "./request";

// ===================== 通用接口 =====================
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

// ===================== 景点类型相关 =====================
// 景点类型接口定义
export interface AttractionType {
  typeId: number;
  typeName: string;
  sortOrder: number;
  status: number;
  createTime: string;
  updateTime: string;
}

// 创建景点类型请求
export interface CreateAttractionTypeRequest {
  typeName: string;
  sortOrder: number;
  status: number;
}

// 更新景点类型请求
export interface UpdateAttractionTypeRequest {
  typeId: number;
  typeName?: string;
  sortOrder?: number;
  status?: number;
}

// 分页查询景点类型请求
export interface QueryAttractionTypesRequest {
  pageNum?: number;
  pageSize?: number;
  typeId?: number;
  typeName?: string;
  status?: number;
}

// 创建景点类型
export function createAttractionType(params: CreateAttractionTypeRequest) {
  return post<AttractionType>("/api/admin/attraction/type/create", params);
}

// 更新景点类型
export function updateAttractionType(params: UpdateAttractionTypeRequest) {
  return put<AttractionType>("/api/admin/attraction/type/update", params);
}

// 删除景点类型
export function deleteAttractionType(typeId: number) {
  return del<void>(`/api/admin/attraction/type/delete/${typeId}`);
}

// 批量删除景点类型
export function batchDeleteAttractionTypes(typeIds: number[]) {
  return del<void>("/api/admin/attraction/type/batch-delete", {
    data: typeIds,
  });
}

// 分页查询景点类型
export function queryAttractionTypes(params?: QueryAttractionTypesRequest) {
  return get<PageResponse<AttractionType>>(
    "/api/admin/attraction/type/list",
    params
  );
}

// 查询景点类型详情
export function getAttractionTypeById(typeId: number) {
  return get<AttractionType>(`/api/admin/attraction/type/detail/${typeId}`);
}

// ===================== 城市相关 =====================
// 城市接口定义
export interface City {
  cityId: number;
  cityName: string;
  provinceCode: string;
  cityCode: string;
  level: number;
  sortOrder: number;
  status: number;
  createTime: string;
  updateTime: string;
}

// 创建城市请求
export interface CreateCityRequest {
  cityName: string;
  provinceCode: string;
  cityCode: string;
  level: number;
  sortOrder: number;
  status: number;
}

// 更新城市请求
export interface UpdateCityRequest {
  cityId: number;
  cityName?: string;
  provinceCode?: string;
  cityCode?: string;
  level?: number;
  sortOrder?: number;
  status?: number;
}

// 分页查询城市请求
export interface QueryCitiesRequest {
  pageNum?: number;
  pageSize?: number;
  cityId?: number;
  cityName?: string;
  provinceCode?: string;
  level?: number;
  status?: number;
}

// 创建城市
export function createCity(params: CreateCityRequest) {
  return post<City>("/api/admin/attraction/city/create", params);
}

// 更新城市
export function updateCity(params: UpdateCityRequest) {
  return put<City>("/api/admin/attraction/city/update", params);
}

// 删除城市
export function deleteCity(cityId: number) {
  return del<void>(`/api/admin/attraction/city/delete/${cityId}`);
}

// 批量删除城市
export function batchDeleteCities(cityIds: number[]) {
  return del<void>("/api/admin/attraction/city/batch-delete", {
    data: cityIds,
  });
}

// 分页查询城市
export function queryCities(params?: QueryCitiesRequest) {
  return get<PageResponse<City>>("/api/admin/attraction/city/list", params);
}

// 查询城市详情
export function getCityById(cityId: number) {
  return get<City>(`/api/admin/attraction/city/detail/${cityId}`);
}

// 获取所有城市
export function getAllCities() {
  return get<City[]>("/api/admin/attraction/city/all");
}

// ===================== 景点标签相关 =====================
// 景点标签接口定义
export interface AttractionTag {
  tagId: number;
  tagName: string;
  status: number;
  createTime: string;
  updateTime: string;
}

// 创建景点标签请求
export interface CreateTagRequest {
  tagName: string;
  status: number;
}

// 更新景点标签请求
export interface UpdateTagRequest {
  tagId: number;
  tagName?: string;
  status?: number;
}

// 分页查询景点标签请求
export interface QueryTagsRequest {
  pageNum?: number;
  pageSize?: number;
  tagId?: number;
  tagName?: string;
  status?: number;
}

// 创建景点标签
export function createTag(params: CreateTagRequest) {
  return post<AttractionTag>("/api/admin/attraction/tag/create", params);
}

// 更新景点标签
export function updateTag(params: UpdateTagRequest) {
  return put<AttractionTag>("/api/admin/attraction/tag/update", params);
}

// 删除景点标签
export function deleteTag(tagId: number) {
  return del<void>(`/api/admin/attraction/tag/delete/${tagId}`);
}

// 批量删除景点标签
export function batchDeleteTags(tagIds: number[]) {
  return del<void>("/api/admin/attraction/tag/batch-delete", {
    data: tagIds,
  });
}

// 分页查询景点标签
export function queryTags(params?: QueryTagsRequest) {
  return get<PageResponse<AttractionTag>>(
    "/api/admin/attraction/tag/list",
    params
  );
}

// 查询景点标签详情
export function getTagById(tagId: number) {
  return get<AttractionTag>(`/api/admin/attraction/tag/detail/${tagId}`);
}

// 获取所有景点标签
export function getAllTags() {
  return get<AttractionTag[]>("/api/admin/attraction/tag/all");
}

// ===================== 景点相关 =====================
// 标签信息
export interface TagInfo {
  tagId: number;
  tagName: string;
  status: number;
}

// 景点列表响应
export interface AttractionListResponse {
  attractionId: number;
  name: string;
  typeId: number;
  typeName: string;
  cityId: number;
  cityName: string;
  viewCount: number;
  favoriteCount: number;
  popularityScore: number;
  status: number;
  createTime: string;
  updateTime: string;
}

// 景点详情响应
export interface AttractionDetailResponse {
  attractionId: number;
  name: string;
  typeId: number;
  typeName: string;
  cityId: number;
  cityName: string;
  address: string;
  description: string;
  openingHours: string;
  ticketPrice: number;
  contact: string;
  imageUrls: string[];
  viewCount: number;
  favoriteCount: number;
  popularityScore: number;
  longitude: number;
  latitude: number;
  status: number;
  createTime: string;
  updateTime: string;
  tags: TagInfo[];
}

// 创建景点请求
export interface CreateAttractionRequest {
  name: string;
  typeId: number;
  cityId: number;
  address?: string;
  description?: string;
  openingHours?: string;
  ticketPrice?: number;
  contact?: string;
  imageUrls?: string[];
  longitude?: number;
  latitude?: number;
  status?: number;
}

// 更新景点请求
export interface UpdateAttractionRequest {
  attractionId: number;
  name?: string;
  typeId?: number;
  cityId?: number;
  address?: string;
  description?: string;
  openingHours?: string;
  ticketPrice?: number;
  contact?: string;
  imageUrls?: string[];
  longitude?: number;
  latitude?: number;
  status?: number;
}

// 分页查询景点请求
export interface QueryAttractionsRequest {
  pageNum?: number;
  pageSize?: number;
  attractionId?: number;
  name?: string;
  typeId?: number;
  cityId?: number;
  minPopularityScore?: number;
  maxPopularityScore?: number;
  status?: number;
}

// 创建景点
export function createAttraction(params: CreateAttractionRequest) {
  return post<number>("/api/admin/attraction/create", params);
}

// 更新景点
export function updateAttraction(params: UpdateAttractionRequest) {
  return put<void>("/api/admin/attraction/update", params);
}

// 删除景点
export function deleteAttraction(attractionId: number) {
  return del<void>(`/api/admin/attraction/delete/${attractionId}`);
}

// 分页查询景点列表
export function queryAttractions(params?: QueryAttractionsRequest) {
  return post<PageResponse<AttractionListResponse>>(
    "/api/admin/attraction/list",
    params
  );
}

// 查询景点详情
export function getAttractionDetail(attractionId: number) {
  return get<AttractionDetailResponse>(
    `/api/admin/attraction/detail/${attractionId}`
  );
}

// ===================== 景点标签关系相关 =====================
// 景点绑定标签请求
export interface BindTagRequest {
  attractionId: number;
  tagId: number;
}

// 景点批量绑定标签请求
export interface BatchBindTagsRequest {
  attractionId: number;
  tagIds: number[];
}

// 景点解绑标签请求
export interface UnbindTagRequest {
  attractionId: number;
  tagId: number;
}

// 景点批量解绑标签请求
export interface BatchUnbindTagsRequest {
  attractionId: number;
  tagIds: number[];
}

// 景点绑定标签
export function bindTag(params: BindTagRequest) {
  return post<void>("/api/admin/attraction/tag-relation/bind", params);
}

// 景点批量绑定标签
export function batchBindTags(params: BatchBindTagsRequest) {
  return post<void>("/api/admin/attraction/tag-relation/batch-bind", params);
}

// 景点解绑标签
export function unbindTag(params: UnbindTagRequest) {
  return post<void>("/api/admin/attraction/tag-relation/unbind", params);
}

// 景点批量解绑标签
export function batchUnbindTags(params: BatchUnbindTagsRequest) {
  return post<void>("/api/admin/attraction/tag-relation/batch-unbind", params);
}

// 查询景点的所有标签
export function getAttractionTags(attractionId: number) {
  return get<TagInfo[]>(
    `/api/admin/attraction/tag-relation/list/${attractionId}`
  );
}
