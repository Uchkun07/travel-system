<template>
  <div class="layout-container">
    <Banner />
    <el-container class="layout-main">
      <MenuSidebar />
      <el-main class="layout-content">
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import Banner from "@/components/Banner.vue";
import MenuSidebar from "@/components/Menu.vue";
</script>

<style scoped>
.layout-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.layout-main {
  flex: 1;
  overflow: hidden;
}
</style>
