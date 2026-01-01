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
      @show-preference="showPreferenceDialog"
    />

    <!-- 偏好设置弹窗 -->
    <PreferenceDialog v-model:visible="preferenceVisible" />

    <!-- AI助手悬浮按钮 -->
    <div class="ai-chat-button" @click="toggleChatWindow">
      <img src="@/assets/imgs/wxx.png" alt="AI助手" />
    </div>

    <!-- AI聊天窗口 -->
    <AiChatWindow v-model:visible="chatVisible" />
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import Navigation from "@/components/navigation.vue";
import Footer from "@/components/Footer.vue";
import LoginWindow from "@/components/LoginWindow.vue";
import RegisterWindow from "@/components/registerWindow.vue";
import PreferenceDialog from "@/components/PreferenceDialog.vue";
import AiChatWindow from "@/components/AiChatWindow.vue";

// 控制登录和注册弹窗的显示
const loginVisible = ref(false);
const registerVisible = ref(false);
const preferenceVisible = ref(false);
const chatVisible = ref(false);

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

// 显示偏好设置弹窗
const showPreferenceDialog = () => {
  preferenceVisible.value = true;
};

// 切换聊天窗口显示
const toggleChatWindow = () => {
  chatVisible.value = !chatVisible.value;
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

/* AI助手悬浮按钮 */
.ai-chat-button {
  position: fixed;
  right: 30px;
  bottom: 30px;
  width: 60px;
  height: 60px;
  cursor: pointer;
  z-index: 999;
  transition: transform 0.3s ease;
}

.ai-chat-button:hover {
  transform: scale(1.1);
}

.ai-chat-button img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.2));
}
</style>
