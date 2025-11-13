import { defineStore } from "pinia";
import { ref, computed } from "vue";
import { adminLogin, adminLogout } from "../apis/auth";
import type { LoginRequest } from "../apis/auth";
import { ElMessage } from "element-plus";
import Cookies from "js-cookie";

export interface User {
  userId: number;
  username: string;
  fullName: string;
  email: string;
  phone: string;
  avatar?: string;
}

export const useAuthStore = defineStore("auth", () => {
  const user = ref<User | null>(null);
  const token = ref<string>("");
  const loading = ref<boolean>(false);

  // 计算属性
  const isLoggedIn = computed(() => !!user.value && !!token.value);

  // 初始化时检查本地存储
  const initAuth = () => {
    // 从 cookie 中读取 token
    const storedToken = Cookies.get("token");
    // 从 localStorage 中读取用户信息
    const storedUser = localStorage.getItem("user");

    if (storedUser && storedToken) {
      try {
        user.value = JSON.parse(storedUser);
        token.value = storedToken;
      } catch (error) {
        console.error("解析用户信息失败:", error);
        logout();
      }
    }
  };

  // 登录
  const login = async (loginData: LoginRequest): Promise<boolean> => {
    loading.value = true;
    try {
      const response = await adminLogin(loginData);

      console.log("登录响应:", response); // 调试日志

      // 检查响应状态
      if (response.code !== 200 || !response.data) {
        ElMessage.error(response.message || "登录失败");
        return false;
      }

      const {
        token: newToken,
        userId,
        username,
        fullName,
        email,
        phone,
      } = response.data;

      // 保存用户信息
      const userInfo: User = {
        userId,
        username,
        fullName,
        email,
        phone,
        avatar: `https://ui-avatars.com/api/?name=${encodeURIComponent(
          fullName || username
        )}&background=667eea&color=fff&size=128`,
      };

      user.value = userInfo;
      token.value = newToken;

      // Token 保存到 Cookie (根据 rememberMe 设置过期时间)
      if (loginData.rememberMe) {
        // 记住我：30天过期
        Cookies.set("token", newToken, { expires: 30 });
      } else {
        // 不记住：会话结束后过期
        Cookies.set("token", newToken);
      }

      // 用户信息保存到 localStorage
      localStorage.setItem("user", JSON.stringify(userInfo));

      console.log("Token已保存到Cookie:", newToken); // 调试日志
      console.log("用户信息已保存到localStorage:", userInfo); // 调试日志

      ElMessage.success("登录成功");
      return true;
    } catch (error: any) {
      console.error("登录失败:", error);
      
      // 显示错误消息
      if (error.response?.data?.message) {
        ElMessage.error(error.response.data.message);
      } else {
        ElMessage.error("登录失败，请稍后重试");
      }

      return false;
    } finally {
      loading.value = false;
    }
  };

  // 登出
  const logout = async () => {
    try {
      // 调用登出API（可选）
      await adminLogout();
    } catch (error) {
      console.error("登出API调用失败:", error);
    } finally {
      // 无论API调用成功与否，都清除本地数据
      user.value = null;
      token.value = "";

      // 清除 Cookie 中的 token
      Cookies.remove("token");
      // 清除 localStorage 中的用户信息
      localStorage.removeItem("user");
      sessionStorage.removeItem("user");
      sessionStorage.removeItem("token");

      ElMessage.success("已退出登录");
    }
  };

  // 检查登录状态
  const checkAuth = (): boolean => {
    // 从 cookie 读取 token
    const storedToken = Cookies.get("token");
    // 从 localStorage 读取用户信息
    const storedUser = localStorage.getItem("user");

    if (storedUser && storedToken) {
      try {
        user.value = JSON.parse(storedUser);
        token.value = storedToken;
        return true;
      } catch (error) {
        console.error("解析用户信息失败:", error);
        logout();
        return false;
      }
    }

    return false;
  };

  return {
    user,
    token,
    loading,
    isLoggedIn,
    initAuth,
    login,
    logout,
    checkAuth,
  };
});
