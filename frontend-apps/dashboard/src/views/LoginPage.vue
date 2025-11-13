<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-right">
        <Login v-if="showLoginModal" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "../stores/auth";
import Login from "../components/Login.vue";

const router = useRouter();
const authStore = useAuthStore();
const showLoginModal = ref(false);

onMounted(() => {
  // 初始化认证状态
  authStore.initAuth();

  // 检查是否已登录
  if (authStore.checkAuth()) {
    router.push("/dashboard");
  } else {
    showLoginModal.value = true;
  }
});
</script>

<style scoped>
.login-page {
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.login-container {
  display: flex;
  width: 100%;
  height: 100%;
  max-width: 1200px;
}

.login-left {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: white;
}

.welcome-content {
  max-width: 500px;
  text-align: center;
}

.welcome-content h1 {
  font-size: 48px;
  font-weight: 700;
  margin-bottom: 20px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.welcome-content p {
  font-size: 18px;
  margin-bottom: 40px;
  opacity: 0.9;
  line-height: 1.6;
}

.features {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-top: 40px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: all 0.3s ease;
}

.feature-item:hover {
  transform: translateY(-5px);
  background: rgba(255, 255, 255, 0.15);
}

.feature-item i {
  font-size: 20px;
  color: #4caf50;
}

.feature-item span {
  font-size: 16px;
  font-weight: 500;
}

.login-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-container {
    flex-direction: column;
  }

  .login-left {
    flex: none;
    height: 40vh;
    padding: 20px;
  }

  .welcome-content h1 {
    font-size: 32px;
  }

  .welcome-content p {
    font-size: 16px;
  }

  .features {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .login-right {
    flex: 1;
    padding: 20px;
  }
}
</style>
