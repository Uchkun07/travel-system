<template>
  <div class="container">
    <navigation @show-login="showLogin" @show-register="showRegister" />
    <loginWindow
      v-model="loginVisible"
      @show-register="showRegister"
      @close="onLoginClose"
    />
    <registerWindow
      v-model="registerVisible"
      @show-login="showLogin"
      @close="onRegisterClose"
      @register="onRegister"
    />
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import Navigation from "@/components/navigation.vue";
import loginWindow from "@/components/LoginWindow.vue";
import registerWindow from "@/components/registerWindow.vue";

const loginVisible = ref(false);
const registerVisible = ref(false);

const showLogin = () => {
  loginVisible.value = true;
};

const showRegister = () => {
  registerVisible.value = true;
};

const onLoginClose = () => {
  loginVisible.value = false;
};

const onRegisterClose = () => {
  registerVisible.value = false;
};

const onRegister = (data: {
  username: string;
  password: string;
  email: string;
}) => {
  // 处理注册后的逻辑（示例：直接打开登录窗口或显示消息）
  console.log("registered", data);
};
</script>

<style scoped>
.container {
  width: 100%;
  height: auto;
}
</style>
