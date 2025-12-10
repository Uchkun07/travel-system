// stores/user.ts (在现有基础上添加收藏相关功能)
import { defineStore } from "pinia";
import { ref, computed } from "vue";
import Cookies from "js-cookie";
import {
  login as apiLogin,
  logout as apiLogout,
  getUserProfile,
  type LoginRequest,
} from "@/apis";
import { ElMessage } from "element-plus";
import { router } from "@/routers";
import { useCollectionStore } from "./collection";

export interface UserInfo {
  userId: number;
  username: string;
  email: string;
  fullName?: string;
  avatar?: string;
  phone?: string;
  gender?: number; // 0=保密, 1=男, 2=女
  birthday?: string; // 生日
  status?: number;
}

// Cookie 配置常量
const TOKEN_COOKIE_NAME = "token";
const TOKEN_EXPIRES_DAYS = 7; // Cookie 过期时间(天)

export const useUserStore = defineStore("user", () => {
  // 状态 - Token 从 Cookie 读取
  const token = ref<string>(Cookies.get(TOKEN_COOKIE_NAME) || "");

  // 状态 - 用户信息从 localStorage 读取
  const userInfo = ref<UserInfo | null>(
    localStorage.getItem("userInfo")
      ? JSON.parse(localStorage.getItem("userInfo")!)
      : null
  );

  // 计算属性 - isLoggedIn 基于 token 和 userInfo 的存在性
  const isLoggedIn = computed(() => !!token.value && !!userInfo.value);
  const username = computed(() => userInfo.value?.username || "");

  // 头像URL - 如果是相对路径则拼接API地址
  const avatar = computed(() => {
    const avatarPath = userInfo.value?.avatar || "/img/defaultavatar.png";

    // 如果是完整URL（http/https开头），直接返回
    if (avatarPath.startsWith("http://") || avatarPath.startsWith("https://")) {
      return avatarPath;
    }

    // 如果是相对路径，拼接服务器地址（不包含/api）
    const baseUrl = "http://localhost:8080";

    // 确保路径以 / 开头
    const path = avatarPath.startsWith("/") ? avatarPath : `/${avatarPath}`;

    return `${baseUrl}${path}`;
  });

  const userId = computed(() => userInfo.value?.userId || 0);

  /**
   * 设置token - 保存到 Cookie
   */
  function setToken(newToken: string, rememberMe: boolean = false) {
    token.value = newToken;

    // 保存到 Cookie,设置过期时间
    const expires = rememberMe ? TOKEN_EXPIRES_DAYS : 1; // 记住我:7天,否则1天
    Cookies.set(TOKEN_COOKIE_NAME, newToken, {
      expires,
      path: "/",
      sameSite: "strict", // 防止 CSRF 攻击
      secure: window.location.protocol === "https:", // HTTPS 环境下启用
    });
  }

  /**
   * 设置用户信息 - 保存到 localStorage
   */
  function setUserInfo(info: UserInfo) {
    userInfo.value = info;
    localStorage.setItem("userInfo", JSON.stringify(info));
  }

  /**
   * 清除用户信息 - 清除 Cookie 和 localStorage
   */
  function clearUserInfo() {
    token.value = "";
    userInfo.value = null;

    // 清除 Cookie (多种方式确保删除)
    Cookies.remove(TOKEN_COOKIE_NAME, { path: "/" });
    Cookies.remove(TOKEN_COOKIE_NAME, {
      path: "/",
      domain: window.location.hostname,
    });
    Cookies.remove(TOKEN_COOKIE_NAME); // 默认方式

    // 清除 localStorage
    localStorage.removeItem("userInfo");

    // 额外清理:防止有旧的 token 残留
    localStorage.removeItem("token");

    // 清空收藏状态
    const collectionStore = useCollectionStore();
    collectionStore.clearCollections();
  }

  /**
   * 登录
   */
  async function login(loginData: LoginRequest) {
    try {
      const res: any = await apiLogin(loginData);

      // 后端返回 ApiResponse<UserLoginResponse> 格式: {code, message, data: {token, userId, ...}}
      if (res.code === 200 && res.data?.token) {
        // 保存 token 到 Cookie (传递 rememberMe 参数)
        setToken(res.data.token, loginData.rememberMe || false);

        // 保存用户信息到 localStorage
        setUserInfo({
          userId: res.data.userId!,
          username: res.data.username!,
          email: res.data.email!,
          fullName: res.data.fullName,
          avatar: res.data.avatar,
          phone: res.data.phone,
          gender: res.data.gender,
          birthday: res.data.birthday,
        });

        // 登录后初始化收藏列表
        const collectionStore = useCollectionStore();
        await collectionStore.initializeCollections();

        ElMessage.success(res.message || "登录成功");
        return true;
      } else {
        ElMessage.error(res.message || "登录失败");
        return false;
      }
    } catch (error) {
      console.error("登录失败:", error);
      return false;
    }
  }

  /**
   * 登出
   */
  async function logout() {
    try {
      // 调用登出接口(将token加入黑名单)
      await apiLogout();
    } catch (error) {
      console.error("登出接口调用失败:", error);
    } finally {
      // 无论接口是否成功,都清除本地信息
      clearUserInfo();

      // 再次确认清除 Cookie (双重保险)
      Cookies.remove(TOKEN_COOKIE_NAME, { path: "/" });
      Cookies.remove(TOKEN_COOKIE_NAME); // 不带 path 再试一次

      ElMessage.success("已退出登录");

      // 跳转到首页
      router.push("/");
    }
  }

  /**
   * 获取用户信息
   */
  async function fetchUserInfo() {
    try {
      const res = await getUserProfile();

      if (res && res.userId) {
        setUserInfo({
          userId: res.userId,
          username: res.username!,
          email: res.email!,
          fullName: res.fullName,
          avatar: res.avatar,
          phone: res.phone,
          gender: res.gender,
          birthday: res.birthday,
        });

        // 获取用户信息后初始化收藏列表
        const collectionStore = useCollectionStore();
        await collectionStore.initializeCollections();

        return true;
      } else {
        // 获取失败,清除本地信息
        clearUserInfo();
        return false;
      }
    } catch (error) {
      console.error("获取用户信息失败:", error);
      clearUserInfo();
      return false;
    }
  }

  /**
   * 刷新用户信息(用于token验证)
   */
  async function refreshUserInfo() {
    if (!token.value) {
      return false;
    }
    return await fetchUserInfo();
  }

  return {
    // 状态
    token,
    userInfo,

    // 计算属性
    isLoggedIn,
    username,
    avatar,
    userId,

    // 方法
    setToken,
    setUserInfo,
    clearUserInfo,
    login,
    logout,
    fetchUserInfo,
    refreshUserInfo,
  };
});
