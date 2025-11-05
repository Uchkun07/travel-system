<template>
  <div class="security-content">
    <!-- 页面头部 -->
    <div class="content-header">
      <h2 class="content-title">安全设置</h2>
    </div>

    <!-- 安全状态卡片 -->
    <div class="security-status">
      <div class="status-header">
        <ElIcon class="status-icon"><Lock /></ElIcon>
        <div>
          <h3>账户安全等级</h3>
          <p>{{ securityLevel.text }}</p>
        </div>
      </div>
      <ElProgress
        :percentage="securityLevel.percentage"
        :color="securityLevel.color"
        :stroke-width="12"
      />
    </div>

    <!-- 安全设置项 -->
    <div class="security-items">
      <!-- 更改用户名 -->
      <div class="security-item">
        <div class="item-info">
          <ElIcon class="item-icon"><User /></ElIcon>
          <div class="item-content">
            <h4>更改用户名</h4>
            <p>为了保护您的账户安全，请定期更改用户名</p>
          </div>
        </div>
        <ElButton type="primary" plain @click="showUsernameDialog = true">
          更改用户名
        </ElButton>
      </div>

      <!-- 邮箱绑定 -->
      <div class="security-item">
        <div class="item-info">
          <ElIcon class="item-icon"><Message /></ElIcon>
          <div class="item-content">
            <h4>邮箱绑定</h4>
            <p>{{ emailInfo }}</p>
          </div>
        </div>
        <ElButton type="primary" plain @click="showEmailDialog = true">
          {{ emailBound ? "更换邮箱" : "绑定邮箱" }}
        </ElButton>
      </div>

      <!-- 修改密码 -->
      <div class="security-item">
        <div class="item-info">
          <ElIcon class="item-icon"><Key /></ElIcon>
          <div class="item-content">
            <h4>登录密码</h4>
            <p>定期修改密码可以提高账户安全性</p>
          </div>
        </div>
        <ElButton type="primary" plain @click="showPasswordDialog = true">
          修改密码
        </ElButton>
      </div>
    </div>

    <!-- 修改用户名对话框 -->
    <ElDialog
      v-model="showUsernameDialog"
      title="更改用户名"
      width="500px"
      :close-on-click-modal="false"
    >
      <ElForm
        :model="usernameForm"
        :rules="usernameRules"
        ref="usernameFormRef"
        label-width="100px"
      >
        <ElFormItem label="新用户名" prop="username">
          <ElInput
            v-model="usernameForm.username"
            placeholder="请输入新用户名"
          />
        </ElFormItem>
      </ElForm>
      <template #footer>
        <ElButton @click="showUsernameDialog = false">取消</ElButton>
        <ElButton type="primary" @click="handleUsernameSubmit">确定</ElButton>
      </template>
    </ElDialog>

    <!-- 修改密码对话框 -->
    <ElDialog
      v-model="showPasswordDialog"
      title="修改密码"
      width="500px"
      :close-on-click-modal="false"
    >
      <ElForm
        :model="passwordForm"
        :rules="passwordRules"
        ref="passwordFormRef"
        label-width="100px"
      >
        <ElFormItem label="当前密码" prop="oldPassword">
          <ElInput
            v-model="passwordForm.oldPassword"
            type="password"
            show-password
          />
        </ElFormItem>
        <ElFormItem label="新密码" prop="newPassword">
          <ElInput
            v-model="passwordForm.newPassword"
            type="password"
            show-password
          />
        </ElFormItem>
        <ElFormItem label="确认密码" prop="confirmPassword">
          <ElInput
            v-model="passwordForm.confirmPassword"
            type="password"
            show-password
          />
        </ElFormItem>
      </ElForm>
      <template #footer>
        <ElButton @click="showPasswordDialog = false">取消</ElButton>
        <ElButton type="primary" @click="handlePasswordSubmit">确定</ElButton>
      </template>
    </ElDialog>

    <!-- 邮箱绑定对话框 -->
    <ElDialog
      v-model="showEmailDialog"
      :title="emailBound ? '更换邮箱' : '绑定邮箱'"
      width="500px"
      :close-on-click-modal="false"
    >
      <ElForm :model="emailForm" ref="emailFormRef" label-width="100px">
        <ElFormItem label="邮箱地址">
          <ElInput v-model="emailForm.email" placeholder="请输入邮箱地址" />
        </ElFormItem>
        <ElFormItem label="验证码">
          <div style="display: flex; align-items: center; gap: 10px">
            <ElInput v-model="emailForm.code" placeholder="请输入验证码" />
            <ElButton :disabled="countdownRef > 0" @click="sendEmailCode">
              <template v-if="countdownRef === 0">发送验证码</template>
              <template v-else>{{ countdownRef }}s</template>
            </ElButton>
          </div>
        </ElFormItem>
      </ElForm>
      <template #footer>
        <ElButton @click="showEmailDialog = false">取消</ElButton>
        <ElButton type="primary" @click="handleEmailSubmit">确定</ElButton>
      </template>
    </ElDialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from "vue";
import {
  ElIcon,
  ElButton,
  ElProgress,
  ElDialog,
  ElForm,
  ElFormItem,
  ElInput,
  ElMessage,
  type FormInstance,
  type FormRules,
} from "element-plus";
import { Lock, Key, Message, User } from "@element-plus/icons-vue";
import { useUserStore } from "@/stores";
import {
  changePassword,
  changeUsername,
  changeEmail,
  sendVerificationCode,
  type ChangePasswordRequest,
  type ChangeUsernameRequest,
  type ChangeEmailRequest,
} from "@/apis/auth";

const userStore = useUserStore();

// 安全等级 (用户名30% + 密码30% + 邮箱40%)
const securityLevel = computed(() => {
  let level = 0;
  const userInfo = userStore.userInfo;

  // 用户名：30分 (用户登录即有用户名)
  if (userInfo?.username) level += 30;

  // 密码：30分 (用户登录即有密码)
  level += 30;

  // 邮箱绑定：40分
  if (emailBound.value) level += 40;

  return {
    percentage: level,
    text:
      level >= 80 ? "安全等级高" : level >= 50 ? "安全等级中" : "安全等级低",
    color: level >= 80 ? "#67c23a" : level >= 50 ? "#e6a23c" : "#f56c6c",
  };
});

// 安全设置状态
const emailBound = ref(false);

const emailInfo = computed(() => {
  if (userStore.userInfo?.email) {
    const email = userStore.userInfo.email;
    const parts = email.split("@");
    if (parts.length === 2) {
      const [name, domain] = parts;
      const maskedName =
        name && name.length > 2 ? name.slice(0, 2) + "****" : name;
      return `已绑定邮箱：${maskedName}@${domain}`;
    }
  }
  return "未绑定邮箱";
});

// 组件挂载时初始化
onMounted(() => {
  if (userStore.userInfo?.email) {
    emailBound.value = true;
  }
});

// 对话框显示状态
const showPasswordDialog = ref(false);
const showEmailDialog = ref(false);
const showUsernameDialog = ref(false);

// 修改密码表单
const passwordFormRef = ref<FormInstance>();
const passwordForm = reactive({
  oldPassword: "",
  newPassword: "",
  confirmPassword: "",
});

const passwordRules: FormRules = {
  oldPassword: [{ required: true, message: "请输入当前密码", trigger: "blur" }],
  newPassword: [
    { required: true, message: "请输入新密码", trigger: "blur" },
    { min: 6, message: "密码长度至少6位", trigger: "blur" },
  ],
  confirmPassword: [
    { required: true, message: "请确认新密码", trigger: "blur" },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error("两次输入的密码不一致"));
        } else {
          callback();
        }
      },
      trigger: "blur",
    },
  ],
};

// 邮箱绑定表单
const emailFormRef = ref<FormInstance>();
const emailForm = reactive({
  email: "",
  code: "",
});

const countdownRef = ref(0);

// 修改密码
const handlePasswordSubmit = async () => {
  if (!passwordFormRef.value) return;

  try {
    const valid = await passwordFormRef.value.validate();
    if (!valid) return;

    const requestData: ChangePasswordRequest = {
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword,
      confirmPassword: passwordForm.confirmPassword,
    };

    const response = await changePassword(requestData);

    if (response.success) {
      ElMessage.success(response.message || "密码修改成功");
      showPasswordDialog.value = false;
      passwordForm.oldPassword = "";
      passwordForm.newPassword = "";
      passwordForm.confirmPassword = "";
    } else {
      ElMessage.error(response.message || "密码修改失败");
    }
  } catch (error: any) {
    console.error("修改密码失败:", error);
    if (error.response?.status === 401) {
      ElMessage.error("登录已过期，请重新登录");
      userStore.logout();
    } else if (error.response?.data?.message) {
      ElMessage.error(error.response.data.message);
    } else {
      ElMessage.error("密码修改失败，请稍后重试");
    }
  }
};

// 发送邮箱验证码
const sendEmailCode = async () => {
  if (countdownRef.value > 0) return;

  if (!emailForm.email) {
    ElMessage.warning("请输入邮箱地址");
    return;
  }

  // 验证邮箱格式
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(emailForm.email)) {
    ElMessage.warning("请输入有效的邮箱地址");
    return;
  }

  try {
    const response = await sendVerificationCode({ email: emailForm.email });

    if (response.success) {
      ElMessage.success(response.message || "验证码已发送");
      startCountdown();
    } else {
      ElMessage.error(response.message || "验证码发送失败");
    }
  } catch (error: any) {
    console.error("发送验证码失败:", error);
    if (error.response?.data?.message) {
      ElMessage.error(error.response.data.message);
    } else {
      ElMessage.error("验证码发送失败,请稍后重试");
    }
  }
};

const startCountdown = () => {
  countdownRef.value = 60;
  const timer = setInterval(() => {
    countdownRef.value--;
    if (countdownRef.value <= 0) {
      clearInterval(timer);
    }
  }, 1000);
}; // 绑定邮箱
const handleEmailSubmit = async () => {
  if (!emailForm.email || !emailForm.code) {
    ElMessage.warning("请填写完整信息");
    return;
  }

  // 验证邮箱格式
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(emailForm.email)) {
    ElMessage.warning("请输入有效的邮箱地址");
    return;
  }

  try {
    const requestData: ChangeEmailRequest = {
      email: emailForm.email,
      captcha: emailForm.code,
    };

    const response = await changeEmail(requestData);

    if (response.success) {
      ElMessage.success(
        response.message || emailBound.value ? "邮箱更换成功" : "邮箱绑定成功"
      );
      emailBound.value = true;
      showEmailDialog.value = false;
      emailForm.email = "";
      emailForm.code = "";

      // 更新本地用户信息
      if (response.email && userStore.userInfo) {
        userStore.setUserInfo({
          userId: userStore.userInfo.userId,
          username: userStore.userInfo.username,
          email: response.email,
          fullName: userStore.userInfo.fullName,
          avatar: userStore.userInfo.avatar,
        });
      }
    } else {
      ElMessage.error(response.message || "邮箱绑定失败");
    }
  } catch (error: any) {
    console.error("绑定邮箱失败:", error);
    if (error.response?.status === 401) {
      ElMessage.error("登录已过期，请重新登录");
      userStore.logout();
    } else if (error.response?.data?.message) {
      ElMessage.error(error.response.data.message);
    } else {
      ElMessage.error("邮箱绑定失败，请稍后重试");
    }
  }
};

// 更改用户名表单
const usernameFormRef = ref<FormInstance>();
const usernameForm = reactive({
  username: "",
});

const usernameRules: FormRules = {
  username: [
    { required: true, message: "请输入新用户名", trigger: "blur" },
    { min: 3, max: 20, message: "用户名长度在3-20个字符之间", trigger: "blur" },
    {
      pattern: /^[a-zA-Z0-9_]+$/,
      message: "用户名只能包含字母、数字和下划线",
      trigger: "blur",
    },
  ],
};

// 提交用户名修改
const handleUsernameSubmit = async () => {
  if (!usernameFormRef.value) return;

  try {
    const valid = await usernameFormRef.value.validate();
    if (!valid) return;

    const response = await changeUsername({ username: usernameForm.username });

    if (response.success) {
      ElMessage.success(response.message || "用户名修改成功");
      showUsernameDialog.value = false;
      usernameForm.username = "";

      // 更新本地用户信息
      if (response.token) {
        userStore.setToken(response.token, true);
      }
      if (response.username && userStore.userInfo) {
        userStore.setUserInfo({
          userId: userStore.userInfo.userId,
          username: response.username,
          email: userStore.userInfo.email,
          fullName: userStore.userInfo.fullName,
          avatar: userStore.userInfo.avatar,
        });
      }
    } else {
      ElMessage.error(response.message || "用户名修改失败");
    }
  } catch (error: any) {
    console.error("修改用户名失败:", error);
    if (error.response?.status === 401) {
      ElMessage.error("登录已过期，请重新登录");
      userStore.logout();
    } else if (error.response?.data?.message) {
      ElMessage.error(error.response.data.message);
    } else {
      ElMessage.error("用户名修改失败，请稍后重试");
    }
  }
};
</script>

<style scoped>
.security-content {
  width: 100%;
}

/* 内容头部 */
.content-header {
  margin-bottom: 30px;
}

.content-title {
  font-size: 28px;
  font-weight: 700;
  color: #2c3e50;
  margin: 0;
}

/* 安全状态卡片 */
.security-status {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  padding: 30px;
  margin-bottom: 30px;
  color: white;
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
}

.status-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 20px;
}

.status-icon {
  font-size: 40px;
  background: rgba(255, 255, 255, 0.2);
  padding: 15px;
  border-radius: 12px;
}

.status-header h3 {
  font-size: 22px;
  margin-bottom: 5px;
  font-weight: 700;
}

.status-header p {
  font-size: 16px;
  opacity: 0.95;
}

:deep(.el-progress__text) {
  color: white !important;
  font-size: 16px !important;
  font-weight: 700 !important;
}

:deep(.el-progress-bar__outer) {
  background-color: rgba(255, 255, 255, 0.2) !important;
}

/* 安全设置项列表 */
.security-items {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.security-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  border-radius: 12px;
  padding: 25px 30px;
  border: 1px solid #eee;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.security-item:hover {
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

.item-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.item-icon {
  font-size: 30px;
  color: #3498db;
  background: rgba(52, 152, 219, 0.1);
  padding: 15px;
  border-radius: 12px;
}

.item-content h4 {
  font-size: 18px;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 5px;
}

.item-content p {
  font-size: 15px;
  color: #7f8c8d;
  line-height: 1.5;
}

:deep(.el-button) {
  font-size: 16px;
  padding: 18px 25px;
  border-radius: 50px;
  font-weight: 600;
  transition: all 0.3s ease;
}

:deep(.el-button--primary) {
  background: transparent;
  border: 2px solid #3498db;
  color: #3498db;
}

:deep(.el-button--primary:hover) {
  background: #3498db;
  color: white;
}

:deep(.el-switch) {
  --el-switch-on-color: #3498db;
  --el-switch-height: 28px;
  --el-switch-width: 52px;
}

/* 对话框样式 */
:deep(.el-dialog) {
  border-radius: 16px;
}

:deep(.el-dialog__header) {
  font-size: 20px;
  font-weight: 700;
  padding: 25px 30px 15px;
}

:deep(.el-dialog__body) {
  padding: 20px 30px;
}

:deep(.el-dialog__footer) {
  padding: 15px 30px 25px;
}

:deep(.el-form-item) {
  align-items: center;
}

:deep(.el-form-item__label) {
  font-size: 16px;
  font-weight: 600;
}

:deep(.el-input__wrapper) {
  font-size: 16px;
  padding: 12px 15px;
  border-radius: 8px;
}

@media (max-width: 768px) {
  .security-item {
    flex-direction: column;
    gap: 1.5rem;
    align-items: flex-start;
    padding: 2rem;
  }

  .item-info {
    width: 100%;
  }

  .security-item :deep(.el-button) {
    align-self: flex-end;
  }

  .status-header {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
