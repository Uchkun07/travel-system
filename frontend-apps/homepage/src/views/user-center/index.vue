<template>
  <div class="user-center">
    <div class="container">
      <div class="user-center-wrapper">
        <!-- 左侧边栏 -->
        <UserSidebar
          :initial-menu="activeMenu"
          @menu-change="handleMenuChange"
        />

        <!-- 右侧内容区 -->
        <UserContent :active-menu="activeMenu" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";
import UserSidebar from "@/components/user-center/UserSidebar.vue";
import UserContent from "@/components/user-center/UserContent.vue";

const route = useRoute();
const activeMenu = ref("profile");

const handleMenuChange = (menu: string) => {
  activeMenu.value = menu;
};

// 组件挂载时检查 URL 参数
onMounted(() => {
  const menuParam = route.query.menu as string;
  if (menuParam) {
    activeMenu.value = menuParam;
  }
});
</script>

<style scoped>
.user-center {
  min-height: calc(100vh - 75px);
  background-color: #f9fbfd;
  padding: 25px 0;
  margin-top: 0;
}

.container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 30px;
}

.user-center-wrapper {
  display: flex;
  gap: 40px;
  align-items: flex-start;
}

@media (max-width: 992px) {
  .user-center-wrapper {
    flex-direction: column;
  }
}
</style>
