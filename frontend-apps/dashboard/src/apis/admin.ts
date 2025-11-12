import { post, get, put, del } from "./request";

// ==================== 类型定义 ====================

// 管理员接口定义
export interface Admin {
  adminId: number;
  username: string;
  password?: string;
  passwordSalt?: string;
  pbkdf2Iterations?: number;
  fullName: string;
  phone: string;
  email: string;
  status: number;
  lastLoginTime?: string;
  lastLoginIp?: string;
  loginCount?: number;
  createTime: string;
  updateTime: string;
}

// 创建管理员请求
export interface CreateAdminRequest {
  username: string;
  password: string;
  fullName: string;
  phone?: string;
  email?: string;
}

// 更新管理员请求
export interface UpdateAdminRequest {
  adminId: number;
  fullName?: string;
  phone?: string;
  email?: string;
  status?: number;
}

// 修改密码请求
export interface UpdatePasswordRequest {
  adminId: number;
  newPassword: string;
}

// 查询管理员请求
export interface QueryAdminsRequest {
  pageNum?: number;
  pageSize?: number;
  username?: string;
  fullName?: string;
  email?: string;
  phone?: string;
  status?: number;
}

// 管理员角色绑定请求
export interface AdminRoleBindRequest {
  adminId: number;
  roleIds: number[];
}

// 管理员角色解绑请求
export interface AdminRoleUnbindRequest {
  adminId: number;
  roleIds: number[];
}

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

// 角色接口定义（简化版，用于管理员角色绑定）
export interface AdminRole {
  roleId: number;
  roleName: string;
  roleDesc: string;
  status: number;
  createTime: string;
  updateTime: string;
}

// ==================== 管理员角色绑定 API ====================

// 绑定角色到管理员
export function bindRolesToAdmin(params: AdminRoleBindRequest) {
  return post<void>("/admin/admin-role/bind", params);
}

// 管理员解绑角色
export function unbindRolesFromAdmin(params: AdminRoleUnbindRequest) {
  return post<void>("/admin/admin-role/unbind", params);
}

// 管理员解绑所有角色
export function unbindAllRolesFromAdmin(adminId: number) {
  return post<void>(`/admin/admin-role/unbind-all/${adminId}`);
}

// 查询管理员的所有角色
export function getAdminRoles(adminId: number) {
  return get<AdminRole[]>(`/admin/admin-role/list/${adminId}`);
}

// ==================== 管理员管理 API ====================

// 创建管理员
export function createAdmin(params: CreateAdminRequest) {
  return post<Admin>("/api/admin/create", params);
}

// 更新管理员
export function updateAdmin(params: UpdateAdminRequest) {
  return put<Admin>("/api/admin/update", params);
}

// 修改密码
export function updatePassword(params: UpdatePasswordRequest) {
  return put<void>("/api/admin/update-password", params);
}

// 删除管理员
export function deleteAdmin(adminId: number) {
  return del<void>(`/api/admin/delete/${adminId}`);
}

// 批量删除管理员
export function batchDeleteAdmins(adminIds: number[]) {
  return del<void>("/api/admin/batch-delete", { data: adminIds });
}

// 分页查询管理员
export function queryAdmins(params?: QueryAdminsRequest) {
  return get<PageResponse<Admin>>("/api/admin/list", params);
}

// 查询管理员详情
export function getAdminDetail(adminId: number) {
  return get<Admin>(`/api/admin/detail/${adminId}`);
}
