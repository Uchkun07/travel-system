<template>
  <div class="home-page">
    <!-- 首页内容 -->
    <h1>欢迎来到悦旅</h1>
    <p>探索世界的每一个角落</p>

    <!-- 这里放置首页的其他内容 -->
  </div>
</template>

<script setup lang="ts">
// 现在不需要导入导航栏和弹窗了，它们在 Layout 中统一管理
</script>

<style scoped>
.home-page {
  padding: 40px 18rem;
  min-height: calc(100vh - 75px); /* 减去导航栏高度 */
}

.home-page h1 {
  font-size: 3rem;
  color: #2d88ff;
  margin-bottom: 1rem;
}

.home-page p {
  font-size: 1.5rem;
  color: #666;
}
</style>
