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
.layout-content {
  padding: 20px;
  background: #f8f8f8;
  overflow: auto;
  box-shadow: inset 3px 3px 10px 0px rgb(45 51 54 / 3%);
  border-radius: 30px 0 0 0;
}
.layout-main {
  flex: 1;
  overflow: hidden;
}
</style>
