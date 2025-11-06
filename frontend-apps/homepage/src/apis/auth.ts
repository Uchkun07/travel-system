import { post, get, put } from "./request";

// 请求接口类型定义
export interface SendCodeRequest {
  email: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
  confirmPassword: string;
  email: string;
  captcha: string;
}

export interface LoginRequest {
  username: string;
  password: string;
  rememberMe?: boolean;
}

// 响应接口类型定义
export interface LoginResponse {
  success: boolean;
  message: string;
  userId?: number;
  username?: string;
  email?: string;
  avatar?: string;
  fullName?: string;
  phone?: string;
  gender?: number; // 0=保密, 1=男, 2=女
  birthday?: string; // 生日
  token?: string;
}

export interface UserInfoResponse {
  success: boolean;
  message?: string;
  userId?: number;
  username?: string;
  email?: string;
  fullName?: string;
  avatar?: string;
  phone?: string;
  gender?: number;
  birthday?: string;
  status?: number;
}

export interface CheckResponse {
  success: boolean;
  available: boolean;
  message: string;
}

// 更新用户资料请求接口
export interface UpdateProfileRequest {
  username?: string;
  fullName?: string;
  email?: string;
  phone?: string;
  gender?: number; // 0=保密, 1=男, 2=女
  birthday?: string; // YYYY-MM-DD 格式
  avatar?: string;
}

// 更新用户资料响应接口
export interface UpdateProfileResponse {
  success: boolean;
  message: string;
  token?: string;
  userInfo?: {
    userId: number;
    username: string;
    fullName?: string;
    email?: string;
    phone?: string;
    gender?: number;
    birthday?: string;
    avatar?: string;
  };
}

/**
 * 发送邮箱验证码
 */
export function sendVerificationCode(data: SendCodeRequest) {
  return post<{ success: boolean; message: string }>("/email/sendCode", data);
}

/**
 * 用户注册
 */
export function register(data: RegisterRequest) {
  return post<LoginResponse>("/auth/register", data);
}

/**
 * 用户登录
 */
export function login(data: LoginRequest) {
  return post<LoginResponse>("/auth/login", data);
}

/**
 * 用户登出
 */
export function logout() {
  return post<{ success: boolean; message: string }>("/auth/logout");
}

/**
 * 获取当前用户信息
 */
export function getUserInfo() {
  return get<UserInfoResponse>("/auth/userInfo");
}

/**
 * 检查用户名是否可用
 */
export function checkUsername(username: string) {
  return get<CheckResponse>("/auth/checkUsername", { username });
}

/**
 * 检查邮箱是否可用
 */
export function checkEmail(email: string) {
  return get<CheckResponse>("/auth/checkEmail", { email });
}

/**
 * 更新用户资料（需要JWT认证）
 */
export function updateUserProfile(data: UpdateProfileRequest) {
  return put<UpdateProfileResponse>("/v1/users/profile", data);
}

/**
 * 修改密码请求接口
 */
export interface ChangePasswordRequest {
  oldPassword: string;
  newPassword: string;
  confirmPassword: string;
}

/**
 * 修改密码响应接口
 */
export interface ChangePasswordResponse {
  success: boolean;
  message: string;
}

/**
 * 修改密码（需要JWT认证）
 */
export function changePassword(data: ChangePasswordRequest) {
  return put<ChangePasswordResponse>("/v1/users/password", data);
}

/**
 * 修改用户名请求接口
 */
export interface ChangeUsernameRequest {
  username: string;
}

/**
 * 修改用户名响应接口
 */
export interface ChangeUsernameResponse {
  success: boolean;
  message: string;
  token?: string;
  username?: string;
}

/**
 * 修改用户名（需要JWT认证）
 */
export function changeUsername(data: ChangeUsernameRequest) {
  return put<ChangeUsernameResponse>("/v1/users/username", data);
}

/**
 * 修改邮箱请求接口
 */
export interface ChangeEmailRequest {
  email: string;
  captcha: string;
}

/**
 * 修改邮箱响应接口
 */
export interface ChangeEmailResponse {
  success: boolean;
  message: string;
  email?: string;
}

/**
 * 修改邮箱（需要JWT认证）
 */
export function changeEmail(data: ChangeEmailRequest) {
  return put<ChangeEmailResponse>("/v1/users/email", data);
}

// API 响应格式
interface ApiResponse<T = any> {
  code: number;
  message: string;
  data?: T;
}

/**
 * 上传头像
 * @param file 头像文件
 * @returns 返回头像URL和新token
 */
export const uploadAvatar = async (
  file: File
): Promise<ApiResponse<{ avatarUrl: string; token: string }>> => {
  const { upload } = await import("./request");
  return upload<ApiResponse<{ avatarUrl: string; token: string }>>(
    "/v1/upload/avatar",
    file
  );
};
