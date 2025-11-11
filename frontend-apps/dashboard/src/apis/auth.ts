import { post } from "./request";

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
  return post<LoginResponse>("/admin/login", data);
}

/**
 * 管理员登出
 */
export function adminLogout() {
  return post("/admin/logout");
}

/**
 * 获取当前管理员权限列表
 */
export function getPermissions() {
  return post("/admin/permissions");
}
