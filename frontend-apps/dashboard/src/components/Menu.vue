<template>
  <!-- 左侧菜单 -->
  <el-aside :width="isCollapse ? '64px' : '200px'" class="layout-aside">
    <div class="collapse-btn" @click="toggleCollapse">
      <el-icon>
        <Fold v-if="!isCollapse" />
        <Expand v-else />
      </el-icon>
    </div>
    <el-menu
      :default-active="activeMenu"
      :collapse="isCollapse"
      :collapse-transition="false"
      class="sidebar-menu"
      router
    >
      <el-menu-item index="/dashboard">
        <el-icon><HomeFilled /></el-icon>
        <template #title>首页</template>
      </el-menu-item>

      <el-sub-menu index="1">
        <template #title>
          <el-icon><Avatar /></el-icon>
          <span>管理员管理</span>
        </template>
        <el-menu-item index="/admin/list">管理员列表</el-menu-item>
        <el-menu-item index="/admin/roles">角色管理</el-menu-item>
        <el-menu-item index="/admin/permissions">权限管理</el-menu-item>
      </el-sub-menu>

      <el-sub-menu index="2">
        <template #title>
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </template>
        <el-menu-item index="/users/UserList">用户列表</el-menu-item>
        <el-menu-item index="/users/UserTags">用户标签管理</el-menu-item>
      </el-sub-menu>

      <el-sub-menu index="3">
        <template #title>
          <el-icon><Collection /></el-icon>
          <span>景点管理</span>
        </template>
        <el-menu-item index="/attraction/AttractionList">景点列表</el-menu-item>
        <el-menu-item index="/attraction/AttractionTypes"
          >景点类型管理</el-menu-item
        >
        <el-menu-item index="/attraction/CityList">城市管理</el-menu-item>
        <el-menu-item index="/attraction/AttractionTags"
          >景点标签管理</el-menu-item
        >
      </el-sub-menu>

      <el-menu-item index="/slideshow/SlideshowManagement">
        <el-icon><Setting /></el-icon>
        <template #title>轮播图管理</template>
      </el-menu-item>

      <el-menu-item index="/settings">
        <el-icon><Setting /></el-icon>
        <template #title>系统设置</template>
      </el-menu-item>
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
  border-right: 1px solid #e4e7ed;
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
  border-bottom: 1px solid #e4e7ed;
  transition: background-color 0.3s;
}

.collapse-btn:hover {
  background-color: #f5f7fa;
}

.sidebar-menu {
  border-right: none;
  height: calc(100% - 48px);
  overflow-y: auto;
}

.sidebar-menu:not(.el-menu--collapse) {
  width: 200px;
}

.layout-content {
  background: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
}
</style>
