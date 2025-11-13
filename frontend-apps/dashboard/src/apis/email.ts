import { post } from "./request";

// 发送验证码请求
export interface SendCodeRequest {
  email: string;
}

/**
 * 发送邮箱验证码
 */
export function sendVerificationCode(params: SendCodeRequest) {
  return post<void>("/api/email/sendCode", params);
}
