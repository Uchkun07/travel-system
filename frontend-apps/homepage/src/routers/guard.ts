import { router } from "./index";
import { useUserStore } from "@/stores";
import { ElMessage } from "element-plus";

// 不需要登录就能访问的页面白名单
const whiteList = ["/"];

// 路由前置守卫
router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore();

  // 设置页面标题
  document.title = (to.meta.title as string) || "WayStar - 旅游系统";

  if (userStore.isLoggedIn) {
    // 已登录 - 验证 token 是否有效
    if (!userStore.userInfo) {
      try {
        // 尝试获取用户信息
        const success = await userStore.fetchUserInfo();
        if (success) {
          next();
        } else {
          // token 无效,清除信息并跳转到首页
          userStore.clearUserInfo();
          next("/");
          ElMessage.warning("登录已过期,请重新登录");
        }
      } catch (error) {
        // 获取用户信息失败
        userStore.clearUserInfo();
        next("/");
      }
    } else {
      next();
    }
  } else {
    // 未登录
    if (whiteList.includes(to.path)) {
      // 在白名单中,直接访问
      next();
    } else {
      // 不在白名单中,跳转到首页
      next("/");
      ElMessage.warning("请先登录");
    }
  }
});

// 路由后置守卫
router.afterEach(() => {
  // 页面加载完成后的处理
});
