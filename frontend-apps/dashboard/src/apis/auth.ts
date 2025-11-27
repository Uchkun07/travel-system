import { post, get, put, del } from "./request";

// 登录请求参数
export interface LoginRequest {
  username: string;
  password: string;
  rememberMe?: boolean;
}

// 登录响应数据
export interface LoginResponse {
  token: string;
  tokenType: string;
  userId: number;
  username: string;
  fullName: string;
  email: string;
  phone: string;
}

/**
 * 管理员登录
 */
export function adminLogin(data: LoginRequest) {
  return post<LoginResponse>("/api/admin/login", data);
}

/**
 * 管理员登出
 */
export function adminLogout() {
  return post("/api/admin/logout");
}

/**
 * 获取当前管理员权限列表
 */
export function getPermissions() {
  return get("/api/admin/permissions");
}

/**
 * 获取当前登录管理员信息（不需要特殊权限）
 */
export function getCurrentAdminProfile() {
  return get<Admin>("/api/admin/profile");
}

/**
 * 更新当前登录管理员的个人信息（不需要特殊权限）
 */
export function updateCurrentAdminProfile(params: UpdateAdminRequest) {
  return put<Admin>("/api/admin/profile", params);
}

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
  adminId?: number; // 可选，用于个人信息更新时不需要传adminId
  fullName?: string;
  phone?: string;
  email?: string;
  status?: number;
}

// 修改密码请求
// 修改密码请求
export interface UpdatePasswordRequest {
  adminId: number;
  oldPassword: string;
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
  return post<void>("/api/admin/admin-role/bind", params);
}

// 管理员解绑角色
export function unbindRolesFromAdmin(params: AdminRoleUnbindRequest) {
  return post<void>("/api/admin/admin-role/unbind", params);
}

// 管理员解绑所有角色
export function unbindAllRolesFromAdmin(adminId: number) {
  return post<void>(`/api/admin/admin-role/unbind-all/${adminId}`);
}

// 查询管理员的所有角色
export function getAdminRoles(adminId: number) {
  return get<AdminRole[]>(`/api/admin/admin-role/list/${adminId}`);
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

// ==================== 权限相关API ====================

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
  return post<AdminPermission>("/api/admin/permission/create", params);
}

// 更新权限
export function updatePermission(params: UpdatePermissionRequest) {
  return put<AdminPermission>("/api/admin/permission/update", params);
}

// 删除权限
export function deletePermission(permissionId: number) {
  return del<void>(`/api/admin/permission/delete/${permissionId}`);
}

// 批量删除权限
export function batchDeletePermissions(permissionIds: number[]) {
  return del<void>("/api/admin/permission/batch-delete", {
    data: permissionIds,
  });
}

// 分页查询权限
export function queryPermissions(params?: QueryPermissionsRequest) {
  return get<PageResponse<AdminPermission>>(
    "/api/admin/permission/list",
    params
  );
}

// 查询权限详情
export function getPermissionById(permissionId: number) {
  return get<AdminPermission>(`/api/admin/permission/detail/${permissionId}`);
}

// 获取所有权限
export function getAllPermissions() {
  return get<AdminPermission[]>("/api/admin/permission/all");
}

// 查询角色的所有权限
export function getRolePermissions(roleId: number) {
  return get<AdminPermission[]>(`/api/admin/role-permission/list/${roleId}`);
}

// 绑定权限到角色
export function bindPermissionsToRole(params: RolePermissionBindRequest) {
  return post<void>("/api/admin/role-permission/bind", params);
}

// 角色解绑权限
export function unbindPermissionsFromRole(params: RolePermissionBindRequest) {
  return post<void>("/api/admin/role-permission/unbind", params);
}

// 角色解绑所有权限
export function unbindAllPermissionsFromRole(roleId: number) {
  return post<void>(`/api/admin/role-permission/unbind-all/${roleId}`);
}

// ==================== 角色相关API ====================

// 角色接口定义在上面已经定义 AdminRole

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
  return post<AdminRole>("/api/admin/role/create", params);
}

// 更新角色
export function updateRole(params: UpdateRoleRequest) {
  return put<AdminRole>("/api/admin/role/update", params);
}

// 删除角色
export function deleteRole(roleId: number) {
  return del<void>(`/api/admin/role/delete/${roleId}`);
}

// 批量删除角色
export function batchDeleteRoles(roleIds: number[]) {
  return del<void>("/api/admin/role/batch-delete", { data: roleIds });
}

// 分页查询角色
export function queryRoles(params?: QueryRolesRequest) {
  return get<PageResponse<AdminRole>>("/api/admin/role/list", params);
}

// 查询角色详情
export function getRoleById(roleId: number) {
  return get<AdminRole>(`/api/admin/role/detail/${roleId}`);
}

// 获取所有角色
export function getAllRoles() {
  return get<AdminRole[]>("/api/admin/role/all");
}

// ==================== 管理员操作日志API ====================

// 操作日志接口定义
export interface OperationLog {
  operationLogId: number;
  adminId: number;
  operationType: string;
  operationObject: string;
  objectId: number;
  operationContent: string;
  operationIp: string;
  operationTime: string;
  createTime: string;
}

// 分页查询操作日志请求
export interface QueryOperationLogsRequest {
  pageNum?: number;
  pageSize?: number;
  adminId?: number;
  operatorName?: string;
  operationType?: string;
  operationObject?: string;
  startTime?: string;
  endTime?: string;
}

// 分页查询操作日志
export function queryOperationLogs(params?: QueryOperationLogsRequest) {
  return get<PageResponse<OperationLog>>(
    "/api/admin/operation-log/list",
    params
  );
}

// 删除操作日志
export function deleteOperationLog(operationLogId: number) {
  return del<void>(`/api/admin/operation-log/delete/${operationLogId}`);
}

// 批量删除操作日志
export function batchDeleteOperationLogs(operationLogIds: number[]) {
  return del<void>("/api/admin/operation-log/batch-delete", {
    data: operationLogIds,
  });
}
