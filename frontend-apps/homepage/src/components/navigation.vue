<template>
  <nav class="navbar">
    <div class="logo">
      <img src="@/assets/imgs/logo.png" alt="WayStar Logo" />
      <h1>WayStar</h1>
    </div>
    <div class="menu">
      <ul>
        <li><router-link to="/">景点推荐</router-link></li>
        <li><router-link to="/explore">探索</router-link></li>
        <li><router-link to="/hotcity">热门城市</router-link></li>
        <li><router-link to="/routeline">路线规划</router-link></li>
      </ul>
    </div>

    <!-- 未登录状态 -->
    <div class="user" v-if="!userStore.isLoggedIn">
      <ElButton @click="handleLogin">登录</ElButton>
      <ElButton type="primary" @click="handleRegister">注册</ElButton>
    </div>

    <!-- 已登录状态 - 用户头像和下拉菜单 -->
    <div class="user-info" v-else>
      <ElDropdown @command="handleCommand">
        <div class="avatar-wrapper">
          <img
            :src="userStore.avatar"
            :alt="userStore.username"
            class="avatar"
          />
          <span class="username">{{ userStore.username }}</span>
        </div>
        <template #dropdown>
          <ElDropdownMenu>
            <ElDropdownItem command="profile">
              <ElIcon><User /></ElIcon>
              个人中心
            </ElDropdownItem>
            <ElDropdownItem command="settings">
              <ElIcon><Setting /></ElIcon>
              账号设置
            </ElDropdownItem>
            <ElDropdownItem divided command="logout">
              <ElIcon><SwitchButton /></ElIcon>
              退出登录
            </ElDropdownItem>
          </ElDropdownMenu>
        </template>
      </ElDropdown>
    </div>
  </nav>
</template>

<script setup lang="ts">
import {
  ElButton,
  ElDropdown,
  ElDropdownMenu,
  ElDropdownItem,
  ElIcon,
} from "element-plus";
import { User, Setting, SwitchButton } from "@element-plus/icons-vue";
import { useUserStore } from "@/stores";
import { ElMessage } from "element-plus";
import { useRouter } from "vue-router";

const userStore = useUserStore();
const router = useRouter();

const emit = defineEmits<{
  "show-login": [];
  "show-register": [];
}>();

const handleLogin = () => {
  emit("show-login");
};

const handleRegister = () => {
  emit("show-register");
};

// 处理下拉菜单命令
const handleCommand = async (command: string) => {
  switch (command) {
    case "profile":
      // 跳转到个人中心
      router.push("/profile");
      break;
    case "settings":
      // 跳转到账号设置（安全设置）
      router.push("/profile?menu=security");
      break;
    case "logout":
      await userStore.logout();
      // 退出后跳转到首页
      router.push("/");
      break;
  }
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
  box-shadow: 0 2px 15px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  position: relative;
  z-index: 100;
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
    width: 60px;
    height: 60px;
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
    height: 3px;
    border-radius: 10px;
    background: #3498db;
    transition: all 0.2s ease;
  }
  a:hover:after,
  a.router-link-exact-active:after {
    width: 100%;
  }
  a:hover,
  a.router-link-exact-active {
    color: #3498db;
  }
}
.user {
  display: flex;
  gap: 1.5rem;
}

/* 已登录用户信息样式 */
.user-info {
  display: flex;
  align-items: center;
}

.avatar-wrapper {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
  padding: 0.25rem 0.75rem;
  border-radius: 50px;
  transition: all 0.3s ease;
}

.avatar-wrapper:hover {
  background-color: rgba(45, 136, 255, 0.1);
}

.avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #2d88ff;
  transition: all 0.3s ease;
}

.avatar-wrapper:hover .avatar {
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(45, 136, 255, 0.3);
}

.username {
  font-family: "Montserrat", sans-serif;
  font-weight: 600;
  color: #333;
  font-size: 14px;
}

/* 移除 Dropdown 触发器的 outline 和 focus 样式 */
:deep(.el-tooltip__trigger) {
  outline: none !important;
}

:deep(.el-tooltip__trigger:focus) {
  outline: none !important;
}

:deep(.el-tooltip__trigger:focus-visible) {
  outline: none !important;
}

/* 下拉菜单样式 */
:deep(.el-dropdown-menu__item) {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 10px 20px;
  font-size: 14px;
}

:deep(.el-dropdown-menu__item:hover) {
  background-color: rgba(45, 136, 255, 0.1);
  color: #2d88ff;
}

:deep(.el-dropdown-menu__item--divided) {
  border-top: 1px solid #ebeef5;
}

:deep(.el-icon) {
  font-size: 16px;
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
