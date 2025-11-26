<template>
  <!-- 左侧菜单 -->
  <el-aside :width="isCollapse ? '64px' : '240px'" class="layout-aside">
    <div class="collapse-btn" @click="toggleCollapse">
      <i v-if="!isCollapse" class="fa-solid fa-angle-left"></i>
      <i v-else class="fa-solid fa-angle-right"></i>
    </div>
    <el-menu
      :default-active="activeMenu"
      :collapse="isCollapse"
      :collapse-transition="false"
      class="sidebar-menu"
      router
    >
      <el-menu-item index="/dashboard">
        <i class="fa-solid fa-light fa-gauge"></i>
        <template #title>
          <span class="title">首页</span>
        </template>
      </el-menu-item>

      <el-sub-menu index="1">
        <template #title>
          <i class="fa-brands fa-black-tie" style="font-size: 20px"></i>
          <span class="title">管理员管理</span>
        </template>
        <el-menu-item index="/admin/list"
          ><i class="fa-solid fa-list-ul"></i>管理员列表</el-menu-item
        >
        <el-menu-item index="/admin/roles"
          ><i class="fa-solid fa-address-card"></i>角色管理</el-menu-item
        >
        <el-menu-item index="/admin/permissions"
          ><i class="fa-solid fa-key"></i>权限管理</el-menu-item
        >
      </el-sub-menu>

      <el-sub-menu index="2">
        <template #title>
          <i class="fa-solid fa-user-group"></i>
          <span class="title">用户管理</span>
        </template>
        <el-menu-item index="/users/UserList"
          ><i class="fa-solid fa-list-ul"></i>用户列表</el-menu-item
        >
        <el-menu-item index="/users/UserTags"
          ><i class="fa-solid fa-tags"></i>用户标签管理</el-menu-item
        >
      </el-sub-menu>

      <el-sub-menu index="3">
        <template #title>
          <i class="fa-solid fa-mountain-sun"></i>
          <span class="title">景点管理</span>
        </template>
        <el-menu-item index="/attraction/AttractionList"
          ><i class="fa-solid fa-list-ul"></i>景点列表</el-menu-item
        >
        <el-menu-item index="/attraction/AttractionTypes"
          ><i class="fa-solid fa-font-awesome"></i>景点类型管理</el-menu-item
        >
        <el-menu-item index="/attraction/CityList"
          ><i class="fa-solid fa-city"></i>城市管理</el-menu-item
        >
        <el-menu-item index="/attraction/AttractionTags"
          ><i class="fa-solid fa-tags"></i>景点标签管理</el-menu-item
        >
      </el-sub-menu>

      <el-menu-item
        class=".firist-level"
        index="/slideshow/SlideshowManagement"
      >
        <i class="fa-solid fa-arrows-turn-to-dots"></i>
        <span class="title">轮播图管理</span>
      </el-menu-item>

      <el-sub-menu index="4">
        <template #title>
          <i class="fa-solid fa-gear"></i>
          <span class="title">系统设置</span>
        </template>
        <el-menu-item index="/system/logManage"
          ><i class="fa-solid fa-clock-rotate-left"></i>操作日志</el-menu-item
        >
      </el-sub-menu>
    </el-menu>
  </el-aside>
</template>
<script setup lang="ts">
import { ref, computed } from "vue";
import { useRoute } from "vue-router";
const route = useRoute();
const isCollapse = ref(false);

const activeMenu = computed(() => route.path);

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value;
};
</script>
<style scoped>
.layout-aside {
  background: #ffffff;
  transition: width 0.3s;
  position: relative;
  overflow: hidden;
  overflow-x: none;
  min-width: 0;
}

.collapse-btn {
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background-color 0.3s;
}

.collapse-btn:hover {
  background-color: #f5f7fa;
}

.sidebar-menu {
  border-right: none;
  height: calc(100% - 48px);
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 10px;
  gap: 10px;
}
.title {
  height: 40px;
  line-height: 40px;
  font-family: Arial, Helvetica, sans-serif;
  font-weight: 600;
  font-size: 14px;
}
.el-menu-item {
  gap: 10px;
  width: 90%;
  height: 40px;
  border-radius: 6px;
  font-family: Arial, Helvetica, sans-serif;
  font-weight: 400;
  font-size: 12px;
}
:deep(.el-menu-item.is-active) {
  background: linear-gradient(to right, #7468f0, #9c93f4);
  color: white;
  /* box-shadow: 0 0 10px rgba(208, 255, 0, 0.5); */
}
.el-sub-menu .el-menu-item {
  height: 40px;
}
.fa-solid {
  font-size: 20px;
}
.el-sub-menu {
  width: 90%;
}
:deep(.el-sub-menu__title) {
  height: 40px;
  gap: 10px;
}

:deep(.el-menu--collapse .el-sub-menu.is-active .el-sub-menu__title) {
  background: linear-gradient(to right, #7468f0, #9c93f4);
  color: white;
  /* box-shadow: 0 0 10px rgba(229, 255, 0, 0.5); */
  border-radius: 6px;
}
:deep(.el-menu--inline) {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.sidebar-menu:not(.el-menu--collapse) {
  width: 240px;
}
</style>
