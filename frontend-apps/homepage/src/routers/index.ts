import { createRouter, createWebHistory } from "vue-router";
import type { RouteRecordRaw } from "vue-router";

const routes: RouteRecordRaw[] = [
  {
    path: "/",
    name: "Home",
    meta: { title: "悦旅 - 探索世界的每一个角落" },
    component: () => import("@/views/home/Home.vue"),
  },
];

export const router = createRouter({
  history: createWebHistory(),
  routes,
});
