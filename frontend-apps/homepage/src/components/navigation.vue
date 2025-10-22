<template>
  <nav class="navbar">
    <div class="logo">
      <img src="@/assets/imgs/logo.png" alt="悦旅 Logo" />
      <h1>悦旅</h1>
    </div>
    <div class="menu">
      <ul>
        <li><router-link to="/">景点推荐</router-link></li>
        <li><router-link to="/">探索</router-link></li>
        <li><router-link to="/">热门城市</router-link></li>
        <li><router-link to="/">路线规划</router-link></li>
      </ul>
    </div>
    <div class="user">
      <ElButton @click="handleLogin">登录</ElButton>
      <ElButton type="primary">注册</ElButton>
    </div>
  </nav>
</template>

<script setup lang="ts">
import { ElButton } from "element-plus";

const emit = defineEmits<{
  "show-login": [];
}>();

const handleLogin = () => {
  emit("show-login");
};
</script>

<style scoped>
.navbar {
  height: 75px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 18rem;
  background-color: rgba(255, 255, 255, 0.95);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}
.navbar :deep(svg) {
  width: 30px;
  height: 30px;
  fill: #2d88ff;
}
.logo {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.8rem;
  img {
    width: 75px;
    height: 75px;
    object-fit: contain;
  }
  h1 {
    font-size: 1.5rem;
    font-weight: bold;
    color: #2d88ff;
  }
}
.menu ul {
  list-style: none;
  display: flex;
  gap: 3rem;
  li {
    font-family: "Montserrat", sans-serif;
    font-weight: 600;
    color: #333;
  }
  a {
    text-decoration: none;
    color: #333;
    position: relative;
    padding: 0.5rem 0;
    transition: all 0.2s ease;
  }
  a:after {
    content: "";
    position: absolute;
    bottom: 0;
    left: 0;
    width: 0;
    height: 2px;
    background: #ff6b35;
    transition: all 0.2s ease;
  }
  a:hover:after,
  a.router-link-active:after {
    width: 100%;
  }
  a:hover,
  a.router-link-active {
    color: #ff6b35;
  }
}
.user {
  display: flex;
  gap: 1.5rem;
}
:deep(.el-button) {
  font-family: "Montserrat", sans-serif;
  font-weight: 600;
  padding: 10px 20px;
  border-radius: 50px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 15px;
}
:deep(.el-button--primary) {
  background: #2d88ff;
  border: 2px solid #2d88ff;
  color: #fff;
  box-shadow: 0 4px 10px rgba(45, 136, 255, 0.2);
}
:deep(.el-button--primary:hover) {
  background: #2d88ff;
  border: 2px solid #2d88ff;
  transform: translateY(-3px);
  box-shadow: 0 6px 15px rgba(45, 136, 255, 0.3);
}
:deep(.el-button:not(.el-button--primary)) {
  background: transparent;
  border: 2px solid #2d88ff;
  color: #2d88ff;
}
:deep(.el-button:not(.el-button--primary):hover) {
  background: #2d88ff;
  border-color: #2d88ff;
  color: #fff;
}
</style>
