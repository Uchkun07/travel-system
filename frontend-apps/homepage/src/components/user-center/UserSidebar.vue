<template>
  <div class="user-sidebar">
    <!-- 用户信息卡片 -->
    <div class="user-info-card">
      <div class="user-avatar" @click="handleAvatarClick">
        <img :src="userStore.avatar" :alt="userStore.username" />
        <div class="avatar-overlay">
          <span>更换头像</span>
        </div>
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

    <!-- 头像上传弹窗 -->
    <ElDialog
      v-model="avatarDialogVisible"
      title="更换头像"
      width="500px"
      :close-on-click-modal="false"
    >
      <div class="avatar-upload-container">
        <!-- 当前头像预览 -->
        <div class="current-avatar">
          <img :src="previewUrl || userStore.avatar" alt="头像预览" />
        </div>

        <!-- 上传提示 -->
        <div class="upload-tips">
          <p>支持 JPG、PNG、GIF、WEBP 格式</p>
          <p>文件大小不超过 5MB</p>
        </div>

        <!-- 文件选择按钮 -->
        <ElUpload
          ref="uploadRef"
          :auto-upload="false"
          :show-file-list="false"
          :on-change="handleFileChange"
          accept="image/jpeg,image/jpg,image/png,image/gif,image/webp"
        >
          <ElButton type="primary" :icon="Upload">选择图片</ElButton>
        </ElUpload>

        <!-- 已选择的文件信息 -->
        <div v-if="selectedFile" class="file-info">
          <ElIcon><Document /></ElIcon>
          <span>{{ selectedFile.name }}</span>
          <span class="file-size"
            >({{ formatFileSize(selectedFile.size) }})</span
          >
        </div>
      </div>

      <template #footer>
        <ElButton @click="handleCancelUpload">取消</ElButton>
        <ElButton
          type="primary"
          :loading="uploading"
          :disabled="!selectedFile"
          @click="handleConfirmUpload"
        >
          {{ uploading ? "上传中..." : "确认上传" }}
        </ElButton>
      </template>
    </ElDialog>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from "vue";
import {
  ElIcon,
  ElMessageBox,
  ElMessage,
  ElDialog,
  ElButton,
  ElUpload,
} from "element-plus";
import {
  User,
  Star,
  Lock,
  SwitchButton,
  Upload,
  Document,
} from "@element-plus/icons-vue";
import type { UploadFile } from "element-plus";
import { useUserStore } from "@/stores";
import { useRouter } from "vue-router";
import { uploadAvatar } from "@/apis/auth";
import Cookies from "js-cookie";

const userStore = useUserStore();
const router = useRouter();
const activeMenu = ref("profile");

// 头像上传相关
const avatarDialogVisible = ref(false);
const selectedFile = ref<File | null>(null);
const previewUrl = ref<string>("");
const uploading = ref(false);
const uploadRef = ref();

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

// 点击头像
const handleAvatarClick = () => {
  avatarDialogVisible.value = true;
};

// 文件选择改变
const handleFileChange = (file: UploadFile) => {
  const rawFile = file.raw;
  if (!rawFile) return;

  // 验证文件类型
  const validTypes = [
    "image/jpeg",
    "image/jpg",
    "image/png",
    "image/gif",
    "image/webp",
  ];
  if (!validTypes.includes(rawFile.type)) {
    ElMessage.error("只支持 JPG、PNG、GIF、WEBP 格式的图片");
    return;
  }

  // 验证文件大小（5MB）
  const maxSize = 5 * 1024 * 1024;
  if (rawFile.size > maxSize) {
    ElMessage.error("文件大小不能超过 5MB");
    return;
  }

  selectedFile.value = rawFile;

  // 生成预览URL
  const reader = new FileReader();
  reader.onload = (e) => {
    previewUrl.value = e.target?.result as string;
  };
  reader.readAsDataURL(rawFile);
};

// 确认上传
const handleConfirmUpload = async () => {
  if (!selectedFile.value) {
    ElMessage.warning("请先选择图片");
    return;
  }

  uploading.value = true;
  try {
    const response = await uploadAvatar(selectedFile.value);

    console.log("头像上传响应:", response);

    // 后端返回的是 ApiResponse 格式：{ code, message, data }
    if (response.code === 200 && response.data) {
      // 更新 token
      Cookies.set("token", response.data.token, { expires: 7 });

      // 更新用户信息
      await userStore.fetchUserInfo();

      ElMessage.success(response.message || "头像上传成功");
      avatarDialogVisible.value = false;

      // 清空选择
      selectedFile.value = null;
      previewUrl.value = "";
    } else {
      ElMessage.error(response.message || "头像上传失败");
    }
  } catch (error: any) {
    console.error("头像上传失败:", error);
    ElMessage.error(error.response?.data?.message || "头像上传失败");
  } finally {
    uploading.value = false;
  }
};

// 取消上传
const handleCancelUpload = () => {
  avatarDialogVisible.value = false;
  selectedFile.value = null;
  previewUrl.value = "";
};

// 格式化文件大小
const formatFileSize = (bytes: number): string => {
  if (bytes < 1024) return bytes + " B";
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(2) + " KB";
  return (bytes / (1024 * 1024)).toFixed(2) + " MB";
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
  position: relative;
  cursor: pointer;
  transition: all 0.3s ease;
}

.user-avatar:hover {
  transform: scale(1.05);
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: all 0.3s ease;
}

.user-avatar:hover img {
  filter: brightness(0.6);
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
  pointer-events: none;
}

.user-avatar:hover .avatar-overlay {
  opacity: 1;
}

.avatar-overlay span {
  color: white;
  font-size: 14px;
  font-weight: 600;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
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

/* 头像上传弹窗样式 */
.avatar-upload-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  padding: 20px 0;
}

.current-avatar {
  width: 150px;
  height: 150px;
  border-radius: 50%;
  overflow: hidden;
  border: 3px solid #3498db;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.current-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.upload-tips {
  text-align: center;
  color: #95a5a6;
  font-size: 14px;
}

.upload-tips p {
  margin: 5px 0;
}

.file-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 15px;
  background: #f8f9fa;
  border-radius: 8px;
  font-size: 14px;
  color: #2c3e50;
}

.file-info :deep(.el-icon) {
  font-size: 18px;
  color: #3498db;
}

.file-size {
  color: #95a5a6;
  font-size: 12px;
}

@media (max-width: 992px) {
  .user-sidebar {
    flex: 0 0 auto;
    width: 100%;
  }
}
</style>
