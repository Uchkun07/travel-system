<template>
  <div class="user-sidebar">
    <!-- 用户信息卡片 -->
    <div class="user-info-card">
      <div class="user-avatar">
        <img :src="userStore.avatar" :alt="userStore.username" />
      </div>
      <h3 class="user-name">{{ userStore.username }}</h3>
      <span class="user-level">资深游侠</span>
      <span class="user-level">全球</span>
    </div>

    <!-- 菜单列表 -->
    <nav class="sidebar-menu">
      <ul>
        <li>
          <a
            href="#"
            :class="{ active: activeMenu === 'profile' }"
            @click.prevent="handleMenuClick('profile')"
          >
            <ElIcon><User /></ElIcon>
            <span>个人资料</span>
          </a>
        </li>
        <li>
          <a
            href="#"
            :class="{ active: activeMenu === 'favorites' }"
            @click.prevent="handleMenuClick('favorites')"
          >
            <ElIcon><Star /></ElIcon>
            <span>我的收藏</span>
          </a>
        </li>
        <li>
          <a
            href="#"
            :class="{ active: activeMenu === 'security' }"
            @click.prevent="handleMenuClick('security')"
          >
            <ElIcon><Lock /></ElIcon>
            <span>安全设置</span>
          </a>
        </li>
        <li>
          <a href="#" class="logout-link" @click.prevent="handleLogout">
            <ElIcon><SwitchButton /></ElIcon>
            <span>退出登录</span>
          </a>
        </li>
      </ul>
    </nav>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from "vue";
import { ElIcon, ElMessageBox, ElMessage } from "element-plus";
import { User, Star, Lock, SwitchButton } from "@element-plus/icons-vue";
import { useUserStore } from "@/stores";
import { useRouter } from "vue-router";

const userStore = useUserStore();
const router = useRouter();
const activeMenu = ref("profile");

const props = defineProps<{
  initialMenu?: string;
}>();

const emit = defineEmits<{
  "menu-change": [menu: string];
}>();

// 监听 props 变化
watch(
  () => props.initialMenu,
  (newMenu) => {
    if (newMenu) {
      activeMenu.value = newMenu;
    }
  },
  { immediate: true }
);

const handleMenuClick = (menu: string) => {
  activeMenu.value = menu;
  emit("menu-change", menu);
};

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm("确定要退出登录吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    await userStore.logout();
    ElMessage.success("已退出登录");
    router.push("/");
  } catch {
    // 用户取消操作
  }
};
</script>

<style scoped>
.user-sidebar {
  flex: 0 0 220px;
  background: white;
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
  align-self: flex-start;
}

/* 用户信息卡片 */
.user-info-card {
  text-align: center;
  padding-bottom: 20px;
  margin-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.user-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  margin: 0 auto 15px;
  overflow: hidden;
  border: 3px solid #3498db;
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-name {
  font-size: 22px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 5px;
}

.user-level {
  display: inline-block;
  background: #fcf2e9;
  color: #ff9500;
  padding: 3px 12px;
  border-radius: 50px;
  font-size: 14px;
  font-weight: 600;
}

/* 菜单样式 */
.sidebar-menu {
  margin: 0;
}

.sidebar-menu ul {
  list-style: none;
  margin: 0;
  padding: 0;
}

.sidebar-menu li {
  margin-bottom: 12px;
}

.sidebar-menu a {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 15px;
  border-radius: 8px;
  text-decoration: none;
  color: #2c3e50;
  font-size: 16px;
  font-weight: 500;
  transition: all 0.3s ease;
  cursor: pointer;
}

.sidebar-menu a:hover {
  background: #ecf0f1;
  color: #3498db;
}

.sidebar-menu a.active {
  background: #ecf0f1;
  color: #3498db;
}

.sidebar-menu a.active :deep(.el-icon) {
  color: #3498db;
}

.sidebar-menu :deep(.el-icon) {
  font-size: 18px;
  width: 25px;
  transition: all 0.3s ease;
}

.sidebar-menu a:hover :deep(.el-icon) {
  transform: scale(1.1);
}

.logout-link {
  color: #e74c3c !important;
  margin-top: 10px;
  padding-top: 10px !important;
}

.logout-link :deep(.el-icon) {
  color: #e74c3c !important;
}

.logout-link:hover {
  background: #ecf0f1 !important;
  color: #c0392b !important;
}

.logout-link:hover :deep(.el-icon) {
  color: #c0392b !important;
}

@media (max-width: 992px) {
  .user-sidebar {
    flex: 0 0 auto;
    width: 100%;
  }
}
</style>
