<template>
  <div class="avatar-upload">
    <ElUpload
      class="avatar-uploader"
      :show-file-list="false"
      :before-upload="beforeAvatarUpload"
      :http-request="handleUpload"
      accept="image/*"
    >
      <img v-if="avatarUrl" :src="avatarUrl" class="avatar" />
      <ElIcon v-else class="avatar-uploader-icon"><Plus /></ElIcon>
    </ElUpload>
    <p class="upload-tip">点击上传头像（最大5MB）</p>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from "vue";
import { ElUpload, ElIcon, ElMessage } from "element-plus";
import { Plus } from "@element-plus/icons-vue";
import { uploadAvatar } from "@/apis/auth";
import { useUserStore } from "@/stores";
import Cookies from "js-cookie";

const props = defineProps<{
  modelValue?: string;
}>();

const emit = defineEmits<{
  (e: "update:modelValue", value: string): void;
  (e: "success", avatarUrl: string): void;
}>();

const userStore = useUserStore();

const avatarUrl = computed(() => {
  if (props.modelValue) {
    // 如果是完整URL，直接使用；否则拼接基础URL
    if (props.modelValue.startsWith("http")) {
      return props.modelValue;
    }
    return `${import.meta.env.VITE_API_BASE_URL || "http://8.146.237.23:8080"}${
      props.modelValue
    }`;
  }
  return "";
});

const beforeAvatarUpload = (file: File) => {
  const isImage = file.type.startsWith("image/");
  const isLt5M = file.size / 1024 / 1024 < 5;

  if (!isImage) {
    ElMessage.error("只能上传图片文件！");
    return false;
  }
  if (!isLt5M) {
    ElMessage.error("图片大小不能超过 5MB！");
    return false;
  }
  return true;
};

const handleUpload = async (options: any) => {
  const { file } = options;

  try {
    console.log("开始上传头像...");
    console.log("上传前 - Cookie token:", Cookies.get("token"));
    console.log("上传前 - localStorage token:", localStorage.getItem("token"));

    const response = await uploadAvatar(file);
    console.log("上传头像完整响应:", response);
    console.log("响应数据类型:", typeof response);
    console.log("响应data字段:", response.data);

    // 处理响应数据 - 后端返回的是 ApiResponse<AvatarUploadResponse>
    // response.data 才是真正的 AvatarUploadResponse { avatarUrl, token }
    const responseData = response.data;
    console.log("解析后的响应数据:", responseData);

    if (responseData && responseData.avatarUrl) {
      // 保存新的 token
      if (responseData.token) {
        console.log("收到新 token，准备更新...");
        console.log("新 token 值:", responseData.token);

        // 使用 userStore 的 setToken 方法来更新 token
        // 这样可以确保 token 同时更新到 Cookie 和 store
        userStore.setToken(responseData.token, true);

        console.log("Token更新后 - Cookie:", Cookies.get("token"));
        console.log(
          "Token更新后 - localStorage:",
          localStorage.getItem("token")
        );
      } else {
        console.warn("响应中没有token字段！");
      }

      // 更新头像URL
      emit("update:modelValue", responseData.avatarUrl);
      emit("success", responseData.avatarUrl);

      // 更新 store 中的用户信息
      if (userStore.userInfo) {
        userStore.setUserInfo({
          ...userStore.userInfo,
          avatar: responseData.avatarUrl,
        });
      }

      ElMessage.success("头像上传成功！");
    } else {
      console.error("响应数据格式错误:", responseData);
      ElMessage.error("上传失败，请重试");
    }
  } catch (error: any) {
    console.error("上传头像失败:", error);
    console.error("错误详情:", error.response);
    ElMessage.error(error.response?.data?.message || "头像上传失败，请重试");
  }
};
</script>

<style scoped>
.avatar-upload {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.avatar-uploader {
  border: 2px dashed #d9d9d9;
  border-radius: 50%;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s;
  width: 120px;
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-uploader:hover {
  border-color: #3498db;
}

.avatar {
  width: 120px;
  height: 120px;
  object-fit: cover;
  border-radius: 50%;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
}

.upload-tip {
  font-size: 14px;
  color: #95a5a6;
  margin: 0;
}
</style>
