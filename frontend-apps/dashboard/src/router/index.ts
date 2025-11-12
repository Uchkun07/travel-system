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
          path: "/users/list",
          name: "UserList",
          component: () => import("../views/users/UserList.vue"),
        },
        {
          path: "/users/roles",
          name: "RoleManagement",
          component: () => import("../views/users/RoleManagement.vue"),
        },
        // 景点管理
        {
          path: "/attractions/list",
          name: "AttractionList",
          component: () => import("../views/attractions/AttractionList.vue"),
        },
        {
          path: "/attractions/categories",
          name: "AttractionCategories",
          component: () =>
            import("../views/attractions/AttractionCategories.vue"),
        },
        // 订单管理
        {
          path: "/orders/list",
          name: "OrderList",
          component: () => import("../views/orders/OrderList.vue"),
        },
        {
          path: "/orders/statistics",
          name: "OrderStatistics",
          component: () => import("../views/orders/OrderStatistics.vue"),
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
