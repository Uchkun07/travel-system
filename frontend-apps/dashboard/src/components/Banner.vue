<template>
  <!-- 顶部横幅 -->
  <el-header class="layout-header">
    <div class="header-left">
      <img src="@/assets/imgs/logo.png" alt="Logo" class="logo" />
      <span class="system-title">WayStar</span>
    </div>
    <div class="header-right">
      <el-dropdown>
        <div class="user-info">
          <el-avatar :size="40" :src="authStore.user?.avatar" />
          <span class="username">{{ authStore.user?.username }}</span>
          <el-icon class="el-icon--right">
            <arrow-down />
          </el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="router.push('/profile')"
              >个人中心</el-dropdown-item
            >
            <el-dropdown-item @click="showPasswordDialog = true"
              >修改密码</el-dropdown-item
            >
            <el-dropdown-item divided @click="handleLogout"
              >退出登录</el-dropdown-item
            >
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>

    <ChangePasswordDialog v-model="showPasswordDialog" />
  </el-header>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { ArrowDown } from "@element-plus/icons-vue";
import { useAuthStore } from "../stores/auth";
import { useRouter } from "vue-router";
import { ElMessageBox } from "element-plus";
import ChangePasswordDialog from "./ChangePasswordDialog.vue";

const authStore = useAuthStore();
const router = useRouter();
const showPasswordDialog = ref(false);

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm("确定要退出登录吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    // 等待退出登录完成
    await authStore.logout();

    // 跳转到登录页
    router.push("/login");
  } catch (error) {
    // 用户取消操作或退出失败
    console.log("取消退出登录");
  }
};
</script>

<style scoped>
.layout-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #ffffff;
  padding: 0 40px;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo {
  height: 50px;
  width: 50px;
}
.system-title {
  font-size: 20px;
  font-family: Microsoft YaHei, Arial, Helvetica, sans-serif;
  font-weight: 600;
  color: #807cb9;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: #f5f7fa;
}

.username {
  font-size: 14px;
  color: #606266;
}
</style>
