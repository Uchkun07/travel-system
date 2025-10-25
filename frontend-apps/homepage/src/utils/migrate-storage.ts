/**
 * 存储迁移工具
 * 将 localStorage 中的 token 迁移到 Cookie
 */
import Cookies from "js-cookie";

export function migrateTokenToCookie() {
  try {
    // 检查 localStorage 中是否有旧的 token
    const oldToken = localStorage.getItem("token");

    if (oldToken && !Cookies.get("token")) {
      console.log("检测到旧的 token,正在迁移到 Cookie...");

      // 迁移到 Cookie (默认7天过期)
      Cookies.set("token", oldToken, {
        expires: 7,
        path: "/",
        sameSite: "strict",
        secure: window.location.protocol === "https:",
      });

      // 删除 localStorage 中的旧 token
      localStorage.removeItem("token");

      console.log("Token 已成功迁移到 Cookie");
    }
  } catch (error) {
    console.error("Token 迁移失败:", error);
  }
}
