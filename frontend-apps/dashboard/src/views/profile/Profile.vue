<template>
  <div class="profile-container">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="box-card user-card">
          <div class="user-header">
            <el-avatar :size="100" :src="defaultAvatar" class="user-avatar" />
            <h2 class="username">{{ userInfo.username }}</h2>
            <p class="role-name">{{ roleNames }}</p>
          </div>
          <div class="user-info-list">
            <div class="info-item">
              <i class="fa-solid fa-user"></i>
              <span>用户ID: {{ userInfo.adminId }}</span>
            </div>
            <div class="info-item">
              <i class="fa-solid fa-clock"></i>
              <span>注册时间: {{ formatDate(userInfo.createTime) }}</span>
            </div>
            <div class="info-item">
              <i class="fa-solid fa-location-dot"></i>
              <span>最后登录IP: {{ userInfo.lastLoginIp || "未知" }}</span>
            </div>
            <div class="info-item">
              <i class="fa-solid fa-calendar-check"></i>
              <span
                >最后登录时间: {{ formatDate(userInfo.lastLoginTime) }}</span
              >
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>基本资料</span>
              <el-button type="primary" @click="handleUpdate" :loading="loading"
                >保存修改</el-button
              >
            </div>
          </template>
          <el-form
            ref="formRef"
            :model="form"
            :rules="rules"
            label-width="100px"
            class="profile-form"
          >
            <el-form-item label="用户名">
              <el-input v-model="userInfo.username" disabled />
            </el-form-item>
            <el-form-item label="真实姓名" prop="fullName">
              <el-input v-model="form.fullName" placeholder="请输入真实姓名" />
            </el-form-item>
            <el-form-item label="手机号码" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入手机号码" />
            </el-form-item>
            <el-form-item label="电子邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入电子邮箱" />
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from "vue";
import { useAuthStore } from "@/stores/auth";
import {
  getCurrentAdminProfile,
  updateCurrentAdminProfile,
  getAdminRoles,
  type Admin,
} from "@/apis/auth";
import { ElMessage, type FormInstance, type FormRules } from "element-plus";

const authStore = useAuthStore();
const loading = ref(false);
const formRef = ref<FormInstance>();
const defaultAvatar =
  "https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png";

const userInfo = ref<Partial<Admin>>({});
const roles = ref<string[]>([]);

const form = reactive({
  fullName: "",
  phone: "",
  email: "",
});

const rules = reactive<FormRules>({
  fullName: [{ required: true, message: "请输入真实姓名", trigger: "blur" }],
  email: [{ type: "email", message: "请输入正确的邮箱地址", trigger: "blur" }],
  phone: [
    {
      pattern: /^1[3-9]\d{9}$/,
      message: "请输入正确的手机号码",
      trigger: "blur",
    },
  ],
});

const roleNames = computed(() => {
  return roles.value.length > 0 ? roles.value.join(" / ") : "管理员";
});

const formatDate = (dateStr?: string) => {
  if (!dateStr) return "暂无";
  return new Date(dateStr).toLocaleString();
};

const fetchUserInfo = async () => {
  if (!authStore.user) {
    console.log("authStore.user 为空");
    return;
  }

  console.log("当前用户信息:", authStore.user);

  try {
    // 使用新的API获取当前管理员完整信息
    const res = await getCurrentAdminProfile();
    console.log("获取个人资料响应:", res);
    if (res.code === 200 && res.data) {
      userInfo.value = res.data;
      form.fullName = res.data.fullName || "";
      form.phone = res.data.phone || "";
      form.email = res.data.email || "";

      // 使用后端返回的adminId获取角色信息
      if (res.data.adminId) {
        const roleRes = await getAdminRoles(res.data.adminId);
        console.log("获取角色信息响应:", roleRes);
        if (roleRes.code === 200 && roleRes.data) {
          roles.value = roleRes.data.map((r) => r.roleName);
        }
      }
    }
  } catch (error: any) {
    console.error("获取用户信息失败:", error);
    ElMessage.error(error.response?.data?.message || "获取用户信息失败");
  }
};

const handleUpdate = async () => {
  if (!formRef.value) return;

  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      try {
        // 使用新的API更新个人信息（不需要adminId）
        const res = await updateCurrentAdminProfile({
          fullName: form.fullName,
          phone: form.phone,
          email: form.email,
        });

        if (res.code === 200) {
          ElMessage.success("个人资料更新成功");

          // 更新 store 中的用户信息
          if (authStore.user) {
            authStore.user.fullName = form.fullName;
            authStore.user.phone = form.phone;
            authStore.user.email = form.email;
            localStorage.setItem("user", JSON.stringify(authStore.user));
          }

          // 重新获取最新信息
          await fetchUserInfo();
        }
      } catch (error: any) {
        console.error("更新失败:", error);
        ElMessage.error(error.response?.data?.message || "更新失败");
      } finally {
        loading.value = false;
      }
    }
  });
};

onMounted(() => {
  fetchUserInfo();
});
</script>

<style scoped>
.profile-container {
  padding: 20px;
}

.user-card {
  text-align: center;
}

.user-header {
  margin-bottom: 20px;
}

.user-avatar {
  margin-bottom: 10px;
  border: 4px solid #f0f2f5;
}

.username {
  margin: 10px 0 5px;
  font-size: 24px;
  color: #303133;
}

.role-name {
  color: #909399;
  font-size: 14px;
}

.user-info-list {
  text-align: left;
  padding: 0 20px;
}

.info-item {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
  color: #606266;
  font-size: 14px;
}

.info-item i {
  margin-right: 10px;
  width: 20px;
  text-align: center;
  color: #409eff;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.profile-form {
  max-width: 500px;
  margin-top: 20px;
}
</style>
