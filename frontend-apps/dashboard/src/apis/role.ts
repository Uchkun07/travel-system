import { post, get, put, del } from "./request";
// 导入 AdminPermission 类型（用于角色权限相关接口）
import type { AdminPermission } from "./permission";

// 角色接口定义
export interface AdminRole {
  roleId: number;
  roleName: string;
  roleDesc?: string;
  status: number;
  createTime: string;
  updateTime: string;
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

// 创建角色请求
export interface CreateRoleRequest {
  roleName: string;
  roleDesc?: string;
  status: number;
}

// 更新角色请求
export interface UpdateRoleRequest {
  roleId: number;
  roleName?: string;
  roleDesc?: string;
  status?: number;
}

// 分页查询角色请求
export interface QueryRolesRequest {
  pageNum?: number;
  pageSize?: number;
  roleName?: string;
  status?: number;
}

// 角色权限绑定请求
export interface RolePermissionBindRequest {
  roleId: number;
  permissionIds: number[];
}

// 创建角色
export function createRole(params: CreateRoleRequest) {
  return post<AdminRole>("/admin/role/create", params);
}

// 更新角色
export function updateRole(params: UpdateRoleRequest) {
  return put<AdminRole>("/admin/role/update", params);
}

// 删除角色
export function deleteRole(roleId: number) {
  return del<void>(`/admin/role/delete/${roleId}`);
}

// 批量删除角色
export function batchDeleteRoles(roleIds: number[]) {
  return del<void>("/admin/role/batch-delete", { data: roleIds });
}

// 分页查询角色
export function queryRoles(params?: QueryRolesRequest) {
  return get<PageResponse<AdminRole>>("/admin/role/list", params);
}

// 查询角色详情
export function getRoleById(roleId: number) {
  return get<AdminRole>(`/admin/role/detail/${roleId}`);
}

// 获取所有角色
export function getAllRoles() {
  return get<AdminRole[]>("/admin/role/all");
}

// 绑定权限到角色
export function bindPermissionsToRole(params: RolePermissionBindRequest) {
  return post<void>("/admin/role-permission/bind", params);
}

// 角色解绑权限
export function unbindPermissionsFromRole(params: RolePermissionBindRequest) {
  return post<void>("/admin/role-permission/unbind", params);
}

// 角色解绑所有权限
export function unbindAllPermissionsFromRole(roleId: number) {
  return post<void>(`/admin/role-permission/unbind-all/${roleId}`);
}

// 查询角色的所有权限
export function getRolePermissions(roleId: number) {
  return get<AdminPermission[]>(`/admin/role-permission/list/${roleId}`);
}


