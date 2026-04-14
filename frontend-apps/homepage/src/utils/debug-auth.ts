/**
 * 认证调试工具
 * 用于查看和清理认证相关的存储
 *
 * ⚠️ 注意: 此调试工具仅在开发环境 (import.meta.env.DEV) 下暴露到 window 对象
 * 生产环境不会暴露 window.debugAuth 和 window.cleanAuth 方法
 *
 * 使用方法 (开发环境):
 * - window.debugAuth() : 查看当前认证状态
 * - window.cleanAuth() : 强制清除所有认证信息
 */
import Cookies from "js-cookie";

const TOKEN_COOKIE_NAME = "user_token";

/**
 * 打印当前认证状态
 */
export function debugAuthStatus() {
  console.group("🔍 认证状态调试");

  // Cookie 状态
  console.log("📦 Cookie:");
  console.log("  - user_token:", Cookies.get(TOKEN_COOKIE_NAME) || "❌ 不存在");
  console.log("  - 所有 Cookies:", document.cookie || "❌ 空");

  // localStorage 状态
  console.log("\n💾 localStorage:");
  console.log("  - token:", localStorage.getItem("token") || "❌ 不存在");
  console.log("  - userInfo:", localStorage.getItem("userInfo") || "❌ 不存在");

  // 解析 userInfo
  const userInfoStr = localStorage.getItem("userInfo");
  if (userInfoStr) {
    try {
      const userInfo = JSON.parse(userInfoStr);
      console.log("  - 用户ID:", userInfo.userId);
      console.log("  - 用户名:", userInfo.username);
      console.log("  - 邮箱:", userInfo.email);
    } catch (e) {
      console.error("  - ⚠️ userInfo 格式错误");
    }
  }

  console.groupEnd();
}

/**
 * 强制清除所有认证信息
 */
export function forceCleanAuth() {
  console.warn("🧹 强制清除所有认证信息");

  // 清除所有可能的 Cookie
  const cookieNames = [TOKEN_COOKIE_NAME, "token", "TOKEN", "jwt", "JWT"];
  const paths = ["/", "/api", ""];
  const domains = [
    window.location.hostname,
    `.${window.location.hostname}`,
    "localhost",
    "",
  ];

  cookieNames.forEach((name) => {
    paths.forEach((path) => {
      domains.forEach((domain) => {
        Cookies.remove(name, { path, domain });
        Cookies.remove(name, { path });
        Cookies.remove(name);
      });
    });
  });

  // 清除所有 localStorage
  localStorage.clear();

  // 清除所有 sessionStorage
  sessionStorage.clear();

  console.log("✅ 已清除所有认证信息");

  // 再次检查
  debugAuthStatus();
}

/**
 * 在控制台暴露调试方法 (仅开发环境)
 */
if (typeof window !== "undefined" && import.meta.env.DEV) {
  (window as any).debugAuth = debugAuthStatus;
  (window as any).cleanAuth = forceCleanAuth;
  console.log("💡 调试工具已加载 (开发环境):");
  console.log("  - window.debugAuth() : 查看认证状态");
  console.log("  - window.cleanAuth() : 强制清除所有认证信息");
}
