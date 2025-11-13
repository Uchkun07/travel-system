import { createRouter, createWebHistory } from "vue-router";
import { useAuthStore } from "../stores/auth";
import MainLayout from "../layout/MainLayout.vue";
import LoginPage from "../views/LoginPage.vue";
import Dashboard from "../views/Dashboard.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/login",
      name: "Login",
      component: LoginPage,
    },
    {
      path: "/",
      component: MainLayout,
      redirect: "/dashboard",
      children: [
        {
          path: "/dashboard",
          name: "Dashboard",
          component: Dashboard,
        },
        // 用户管理
        {
          path: "/users/UserList",
          name: "UserList",
          component: () => import("../views/users/UserList.vue"),
        },
        {
          path: "/users/UserTags",
          name: "UserTags",
          component: () => import("../views/users/UserTags.vue"),
        },
        // 景点管理
        {
          path: "/attraction/AttractionList",
          name: "AttractionList",
          component: () => import("../views/attractions/AttractionList.vue"),
        },
        {
          path: "/attraction/AttractionTypes",
          name: "AttractionTypes",
          component: () => import("../views/attractions/AttractionTypes.vue"),
        },
        {
          path: "/attraction/CityList",
          name: "CityList",
          component: () => import("../views/attractions/CityList.vue"),
        },
        {
          path: "/attraction/AttractionTags",
          name: "AttractionTags",
          component: () => import("../views/attractions/AttractionTags.vue"),
        },
        // 轮播图管理
        {
          path: "/slideshow/SlideshowManagement",
          name: "SlideshowManagement",
          component: () => import("../views/slideshow/SlideshowManagement.vue"),
        },
        // 系统设置
        {
          path: "/settings",
          name: "Settings",
          component: () => import("../views/Settings.vue"),
        },
        // 管理员管理
        {
          path: "/admin/list",
          name: "AdminList",
          component: () => import("../views/admin/AdminList.vue"),
        },
        {
          path: "/admin/roles",
          name: "RoleManagement",
          component: () => import("../views/admin/RoleManagement.vue"),
        },
        {
          path: "/admin/permissions",
          name: "PermissionManagement",
          component: () => import("../views/admin/PermissionManagement.vue"),
        },
      ],
    },
  ],
});

// 路由守卫
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();

  // 检查是否已登录
  const isLoggedIn = authStore.checkAuth();

  // 如果访问的是登录页面
  if (to.path === "/login") {
    if (isLoggedIn) {
      // 已登录，跳转到首页
      next("/dashboard");
    } else {
      // 未登录，允许访问登录页面
      next();
    }
    return;
  }

  // 如果访问的是受保护的页面
  if (!isLoggedIn) {
    // 未登录，跳转到登录页面
    next("/login");
  } else {
    // 已登录，允许访问
    next();
  }
});

export default router;
