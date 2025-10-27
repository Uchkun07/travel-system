<template>
  <div class="default-layout">
    <!-- 导航栏 -->
    <Navigation
      @show-login="showLoginDialog"
      @show-register="showRegisterDialog"
    />

    <!-- 主内容区域 - 这里会渲染子路由页面 -->
    <main class="main-content">
      <router-view />
    </main>

    <!-- 页脚 -->
    <Footer />

    <!-- 登录弹窗 -->
    <LoginWindow
      v-model:visible="loginVisible"
      @show-register="showRegisterDialog"
    />

    <!-- 注册弹窗 -->
    <RegisterWindow
      v-model:visible="registerVisible"
      @show-login="showLoginDialog"
    />
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import Navigation from "@/components/navigation.vue";
import Footer from "@/components/Footer.vue";
import LoginWindow from "@/components/LoginWindow.vue";
import RegisterWindow from "@/components/registerWindow.vue";

// 控制登录和注册弹窗的显示
const loginVisible = ref(false);
const registerVisible = ref(false);

// 显示登录弹窗
const showLoginDialog = () => {
  loginVisible.value = true;
  registerVisible.value = false;
};

// 显示注册弹窗
const showRegisterDialog = () => {
  registerVisible.value = true;
  loginVisible.value = false;
};
</script>

<style scoped>
.default-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-content {
  flex: 1;
  /* 如果需要给内容区域添加内边距，可以在这里设置 */
  /* padding: 20px; */
}
</style>
