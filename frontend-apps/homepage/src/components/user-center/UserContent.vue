<template>
  <div class="user-content">
    <!-- 个人资料页面 -->
    <ProfileContent v-if="currentMenu === 'profile'" />

    <!-- 我的收藏页面 -->
    <FavoritesContent v-else-if="currentMenu === 'favorites'" />

    <!-- 安全设置页面 -->
    <SecurityContent v-else-if="currentMenu === 'security'" />
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from "vue";
import ProfileContent from "@/views/user-center/ProfileContent.vue";
import FavoritesContent from "@/views/user-center/FavoritesContent.vue";
import SecurityContent from "@/views/user-center/SecurityContent.vue";

const currentMenu = ref("profile");

// 接收侧边栏的菜单切换事件
const props = defineProps<{
  activeMenu?: string;
}>();

// 监听 props 变化
watch(
  () => props.activeMenu,
  (newMenu) => {
    if (newMenu) {
      currentMenu.value = newMenu;
    }
  }
);
</script>

<style scoped>
.user-content {
  flex: 1;
  background: white;
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
  min-height: 600px;
}

@media (max-width: 992px) {
  .user-content {
    width: 100%;
  }
}
</style>
