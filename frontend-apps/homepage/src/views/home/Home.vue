<template>
  <div class="container">
    <navigation @show-login="showLogin" />
    <loginWindow v-model="loginVisible" />
    <!-- <Window /> -->
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import Navigation from "@/components/navigation.vue";
import loginWindow from "@/components/LoginWindow.vue";
// import Window from "@/components/Window.vue";

const loginVisible = ref(false);

const showLogin = () => {
  loginVisible.value = true;
};
</script>

<style scoped>
.container {
  width: 100%;
  height: auto;
}
</style>
