/**
 * JWT Token 工具函数
 */

/**
 * JWT Payload 接口
 */
interface JwtPayload {
  sub?: string;
  exp?: number;
  iat?: number;
  [key: string]: any;
}

/**
 * 解析 JWT Token 的 payload 部分
 * @param token JWT Token 字符串
 * @returns 解析后的 payload 对象，解析失败返回 null
 */
export function parseJwtPayload(token: string): JwtPayload | null {
  try {
    // JWT 格式: header.payload.signature
    const parts = token.split(".");
    if (parts.length !== 3 || !parts[1]) {
      console.error("无效的 JWT Token 格式");
      return null;
    }

    // 解码 payload (Base64Url)
    const payloadBase64 = parts[1];
    const base64 = payloadBase64.replace(/-/g, "+").replace(/_/g, "/");
    const decoded = decodeURIComponent(
      atob(base64)
        .split("")
        .map((c) => "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2))
        .join("")
    );

    return JSON.parse(decoded) as JwtPayload;
  } catch (error) {
    console.error("解析 JWT Token 失败:", error);
    return null;
  }
}

/**
 * 检查 Token 是否过期
 * @param token JWT Token 字符串
 * @returns true 表示已过期，false 表示未过期
 */
export function isTokenExpired(token: string): boolean {
  const payload = parseJwtPayload(token);
  if (!payload || !payload.exp) {
    return true;
  }

  // exp 是秒级时间戳，需要转换为毫秒
  const expirationTime = payload.exp * 1000;
  const currentTime = Date.now();

  return currentTime >= expirationTime;
}

/**
 * 获取 Token 的剩余有效时间（秒）
 * @param token JWT Token 字符串
 * @returns 剩余秒数，如果已过期或无效则返回 0
 */
export function getTokenRemainingTime(token: string): number {
  const payload = parseJwtPayload(token);
  if (!payload || !payload.exp) {
    return 0;
  }

  const expirationTime = payload.exp * 1000;
  const currentTime = Date.now();
  const remaining = Math.floor((expirationTime - currentTime) / 1000);

  return remaining > 0 ? remaining : 0;
}
