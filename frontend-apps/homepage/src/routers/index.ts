import { createRouter, createWebHistory } from "vue-router";
import type { RouteRecordRaw } from "vue-router";

const routes: RouteRecordRaw[] = [
  {
    path: "/",
    name: "Home",
    meta: { title: "首页" },
    component: () => import("@/views/Home.vue"),
  },
];

export const router = createRouter({
  history: createWebHistory(),
  routes,
});
