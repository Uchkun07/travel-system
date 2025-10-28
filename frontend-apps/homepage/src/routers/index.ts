import { createRouter, createWebHistory } from "vue-router";
import type { RouteRecordRaw } from "vue-router";
import DefaultLayout from "@/layouts/DefaultLayout.vue";
import { useUserStore } from "@/stores";
import { ElMessage } from "element-plus";

const routes: RouteRecordRaw[] = [
  // 需要导航栏的页面 - 使用 DefaultLayout 布局
  {
    path: "/",
    component: DefaultLayout,
    children: [
      {
        path: "",
        name: "Home",
        meta: { title: "悦旅 - 探索世界的每一个角落" },
        component: () =>
          import("@/views/recommend-attraction/recommend-attraction.vue"),
      },
      {
        path: "explore",
        name: "Explore",
        meta: { title: "探索 - 悦旅" },
        component: () => import("@/views/explore/Explore.vue"),
      },
      {
        path: "hotcity",
        name: "HotCity",
        meta: { title: "热门城市 - 悦旅" },
        component: () => import("@/views/hotcity/HotCity.vue"),
      },
      {
        path: "routeline",
        name: "RouteLine",
        meta: { title: "路线规划 - 悦旅" },
        component: () => import("@/views/routeline/RouteLine.vue"),
      },
      {
        path: "profile",
        name: "Profile",
        meta: { title: "个人中心 - 悦旅", requiresAuth: true },
        component: () => import("@/views/user-center/index.vue"),
      },
    ],
  },

  // 不需要导航栏的页面 - 独立路由
  // 示例：登录页面（如果未来需要独立登录页）
  // {
  //   path: "/login",
  //   name: "Login",
  //   meta: { title: "登录 - 悦旅" },
  //   component: () => import("@/views/auth/Login.vue"),
  // },
];

export const router = createRouter({
  history: createWebHistory(),
  routes,
});

// 路由守卫：保护需要登录的页面
router.beforeEach((to, from, next) => {
  const userStore = useUserStore();

  // 设置页面标题
  if (to.meta.title) {
    document.title = to.meta.title as string;
  }

  // 检查路由是否需要登录
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    ElMessage.warning("请先登录");
    // 跳转到首页，用户可以在那里点击登录
    next({ path: "/", query: { redirect: to.fullPath } });
  } else {
    next();
  }
});
