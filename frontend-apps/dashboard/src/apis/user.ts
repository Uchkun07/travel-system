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

// ===================== 用户标签字典相关 =====================
// 用户标签字典接口定义
export interface UserTagDict {
  tagDictId: number;
  tagName: string;
  tagCode: string;
  tagLevel: number;
  triggerCondition?: string;
  description?: string;
  iconUrl?: string;
  status: number;
  sortOrder: number;
  createTime: string;
  updateTime: string;
}

// 创建用户标签字典请求
export interface CreateUserTagDictRequest {
  tagName: string;
  tagCode: string;
  tagLevel: number;
  triggerCondition?: string;
  description?: string;
  iconUrl?: string;
  status?: number;
  sortOrder?: number;
}

// 更新用户标签字典请求
export interface UpdateUserTagDictRequest {
  tagDictId: number;
  tagName?: string;
  tagCode?: string;
  tagLevel?: number;
  triggerCondition?: string;
  description?: string;
  iconUrl?: string;
  status?: number;
  sortOrder?: number;
}

// 分页查询用户标签字典请求
export interface QueryUserTagDictsRequest {
  pageNum?: number;
  pageSize?: number;
  tagDictId?: number;
  tagName?: string;
  tagCode?: string;
  tagLevel?: number;
  status?: number;
}

// 创建用户标签字典
export function createUserTagDict(params: CreateUserTagDictRequest) {
  return post<UserTagDict>("/api/admin/user/tag-dict/create", params);
}

// 更新用户标签字典
export function updateUserTagDict(params: UpdateUserTagDictRequest) {
  return put<UserTagDict>("/api/admin/user/tag-dict/update", params);
}

// 删除用户标签字典
export function deleteUserTagDict(tagDictId: number) {
  return del<void>(`/api/admin/user/tag-dict/delete/${tagDictId}`);
}

// 批量删除用户标签字典
export function batchDeleteUserTagDicts(tagDictIds: number[]) {
  return del<void>("/api/admin/user/tag-dict/batch-delete", {
    data: tagDictIds,
  });
}

// 分页查询用户标签字典
export function queryUserTagDicts(params?: QueryUserTagDictsRequest) {
  return get<PageResponse<UserTagDict>>(
    "/api/admin/user/tag-dict/list",
    params
  );
}

// 查询用户标签字典详情
export function getUserTagDictById(tagDictId: number) {
  return get<UserTagDict>(`/api/admin/user/tag-dict/detail/${tagDictId}`);
}

// 获取所有启用的标签字典
export function getAllActiveUserTagDicts() {
  return get<UserTagDict[]>("/api/admin/user/tag-dict/all");
}

// ===================== 用户相关 =====================
// 用户基本信息
export interface UserProfile {
  profileId: number;
  userId: number;
  fullName?: string;
  phone?: string;
  gender?: number;
  birthday?: string;
  residentAddress?: string;
  createTime: string;
  updateTime: string;
}

// 用户偏好设置
export interface UserPreference {
  preferenceId: number;
  userId: number;
  preferAttractionTypeId?: number;
  budgetFloor?: number;
  budgetRange?: number;
  travelCrowd?: string;
  preferSeason?: string;
  createTime: string;
  updateTime: string;
}

// 用户详情响应
export interface UserDetailResponse {
  userId: number;
  username: string;
  nickname?: string;
  email?: string;
  avatarUrl?: string;
  status: number;
  createTime: string;
  lastLoginTime?: string;
  profile?: UserProfile;
  preference?: UserPreference;
  tags?: UserTagDict[];
}

// 用户计数表
export interface UserCountTable {
  countId: number;
  userId: number;
  collectCount: number;
  browsingCount: number;
  planningCount: number;
  createTime: string;
  updateTime: string;
}

// 分页查询用户请求
export interface QueryUsersRequest {
  pageNum?: number;
  pageSize?: number;
  userId?: number;
  username?: string;
  nickname?: string;
  email?: string;
  phone?: string;
  status?: number;
}

// 分页查询用户列表
export function queryUsers(params?: QueryUsersRequest) {
  return get<PageResponse<UserDetailResponse>>("/api/admin/user/list", params);
}

// 获取用户完整详情
export function getUserDetail(userId: number) {
  return get<UserDetailResponse>(`/api/admin/user/detail/${userId}`);
}

// 获取用户计数表数据
export function getUserCountTable(userId: number) {
  return get<UserCountTable>(`/api/admin/user/count/${userId}`);
}

// ===================== 用户标签关系相关 =====================
// 用户标签绑定请求
export interface UserTagBindRequest {
  userId: number;
  tagDictId: number;
}

// 用户标签批量绑定请求
export interface UserTagBatchBindRequest {
  userId: number;
  tagDictIds: number[];
}

// 用户绑定标签
export function bindUserTag(params: UserTagBindRequest) {
  return post<void>("/api/admin/user/tag/bind", params);
}

// 用户批量绑定标签
export function batchBindUserTags(params: UserTagBatchBindRequest) {
  return post<void>("/api/admin/user/tag/batch-bind", params);
}

// 用户解绑标签
export function unbindUserTag(params: UserTagBindRequest) {
  return post<void>("/api/admin/user/tag/unbind", params);
}

// 用户批量解绑标签
export function batchUnbindUserTags(params: UserTagBatchBindRequest) {
  return post<void>("/api/admin/user/tag/batch-unbind", params);
}

// 查询用户的所有标签
export function getUserTags(userId: number) {
  return get<UserTagDict[]>(`/api/admin/user/tag/list/${userId}`);
}
