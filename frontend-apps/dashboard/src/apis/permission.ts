import { post, get, put, del } from "./request";

// 权限接口定义
export interface AdminPermission {
  permissionId: number;
  permissionCode: string;
  permissionName: string;
  resourceType: string;
  resourcePath: string;
  isSensitive: number;
  sortOrder: number;
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

// 创建权限请求
export interface CreatePermissionRequest {
  permissionCode: string;
  permissionName: string;
  resourceType: string;
  resourcePath: string;
  isSensitive: number;
  sortOrder: number;
}

// 更新权限请求
export interface UpdatePermissionRequest {
  permissionId: number;
  permissionCode?: string;
  permissionName?: string;
  resourceType?: string;
  resourcePath?: string;
  isSensitive?: number;
  sortOrder?: number;
}

// 分页查询权限请求
export interface QueryPermissionsRequest {
  pageNum?: number;
  pageSize?: number;
  permissionCode?: string;
  permissionName?: string;
  resourceType?: string;
  isSensitive?: number;
}

// 角色权限绑定请求
export interface RolePermissionBindRequest {
  roleId: number;
  permissionIds: number[];
}

// 创建权限
export function createPermission(params: CreatePermissionRequest) {
  return post<AdminPermission>("/admin/permission/create", params);
}

// 更新权限
export function updatePermission(params: UpdatePermissionRequest) {
  return put<AdminPermission>("/admin/permission/update", params);
}

// 删除权限
export function deletePermission(permissionId: number) {
  return del<void>(`/admin/permission/delete/${permissionId}`);
}

// 批量删除权限
export function batchDeletePermissions(permissionIds: number[]) {
  return del<void>("/admin/permission/batch-delete", { data: permissionIds });
}

// 分页查询权限
export function queryPermissions(params?: QueryPermissionsRequest) {
  return get<PageResponse<AdminPermission>>("/admin/permission/list", params);
}

// 查询权限详情
export function getPermissionById(permissionId: number) {
  return get<AdminPermission>(`/admin/permission/detail/${permissionId}`);
}

// 获取所有权限
export function getAllPermissions() {
  return get<AdminPermission[]>("/admin/permission/all");
}

// 查询角色的所有权限
export function getRolePermissions(roleId: number) {
  return get<AdminPermission[]>(`/admin/role-permission/list/${roleId}`);
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
