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

      <!-- 手机绑定 -->
      <div class="security-item">
        <div class="item-info">
          <ElIcon class="item-icon"><Iphone /></ElIcon>
          <div class="item-content">
            <h4>手机绑定</h4>
            <p>{{ phoneInfo }}</p>
          </div>
        </div>
        <ElButton type="primary" plain @click="showPhoneDialog = true">
          {{ phoneBound ? "更换手机" : "绑定手机" }}
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

      <!-- 两步验证 -->
      <div class="security-item">
        <div class="item-info">
          <ElIcon class="item-icon"><Lock /></ElIcon>
          <div class="item-content">
            <h4>两步验证</h4>
            <p>
              {{
                twoFactorEnabled
                  ? "已开启两步验证"
                  : "未开启两步验证，建议开启以提高安全性"
              }}
            </p>
          </div>
        </div>
        <ElSwitch
          v-model="twoFactorEnabled"
          size="large"
          @change="handleTwoFactorChange"
        />
      </div>

      <!-- 登录设备管理 -->
      <div class="security-item">
        <div class="item-info">
          <ElIcon class="item-icon"><Monitor /></ElIcon>
          <div class="item-content">
            <h4>登录设备管理</h4>
            <p>管理所有登录过的设备</p>
          </div>
        </div>
        <ElButton type="primary" plain @click="showDevicesDialog = true">
          查看设备
        </ElButton>
      </div>
    </div>

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

    <!-- 手机绑定对话框 -->
    <ElDialog
      v-model="showPhoneDialog"
      :title="phoneBound ? '更换手机' : '绑定手机'"
      width="500px"
      :close-on-click-modal="false"
    >
      <ElForm :model="phoneForm" ref="phoneFormRef" label-width="100px">
        <ElFormItem label="手机号">
          <ElInput v-model="phoneForm.phone" placeholder="请输入手机号" />
        </ElFormItem>
        <ElFormItem label="验证码">
          <div style="display: flex; gap: 10px">
            <ElInput v-model="phoneForm.code" placeholder="请输入验证码" />
            <ElButton :disabled="phoneCounting" @click="sendPhoneCode">
              {{ phoneCountText }}
            </ElButton>
          </div>
        </ElFormItem>
      </ElForm>
      <template #footer>
        <ElButton @click="showPhoneDialog = false">取消</ElButton>
        <ElButton type="primary" @click="handlePhoneSubmit">确定</ElButton>
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
          <div style="display: flex; gap: 10px">
            <ElInput v-model="emailForm.code" placeholder="请输入验证码" />
            <ElButton :disabled="emailCounting" @click="sendEmailCode">
              {{ emailCountText }}
            </ElButton>
          </div>
        </ElFormItem>
      </ElForm>
      <template #footer>
        <ElButton @click="showEmailDialog = false">取消</ElButton>
        <ElButton type="primary" @click="handleEmailSubmit">确定</ElButton>
      </template>
    </ElDialog>

    <!-- 登录设备对话框 -->
    <ElDialog v-model="showDevicesDialog" title="登录设备管理" width="600px">
      <div class="devices-list">
        <div
          v-for="device in loginDevices"
          :key="device.id"
          class="device-item"
        >
          <ElIcon class="device-icon"><Monitor /></ElIcon>
          <div class="device-info">
            <h4>{{ device.name }}</h4>
            <p>{{ device.location }} · {{ device.time }}</p>
          </div>
          <ElTag v-if="device.current" type="success">当前设备</ElTag>
          <ElButton
            v-else
            type="danger"
            text
            @click="handleRemoveDevice(device.id)"
          >
            移除
          </ElButton>
        </div>
      </div>
    </ElDialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from "vue";
import {
  ElIcon,
  ElButton,
  ElProgress,
  ElSwitch,
  ElDialog,
  ElForm,
  ElFormItem,
  ElInput,
  ElTag,
  ElMessage,
  type FormInstance,
  type FormRules,
} from "element-plus";
import { Lock, Key, Iphone, Message, Monitor } from "@element-plus/icons-vue";

// 安全等级
const securityLevel = computed(() => {
  let level = 0;
  if (phoneBound.value) level += 30;
  if (emailBound.value) level += 30;
  if (twoFactorEnabled.value) level += 40;

  return {
    percentage: level,
    text:
      level >= 80 ? "安全等级高" : level >= 50 ? "安全等级中" : "安全等级低",
    color: level >= 80 ? "#67c23a" : level >= 50 ? "#e6a23c" : "#f56c6c",
  };
});

// 安全设置状态
const phoneBound = ref(true);
const emailBound = ref(true);
const twoFactorEnabled = ref(false);

const phoneInfo = computed(() =>
  phoneBound.value ? "已绑定手机：138****8000" : "未绑定手机"
);
const emailInfo = computed(() =>
  emailBound.value ? "已绑定邮箱：zhang****@example.com" : "未绑定邮箱"
);

// 对话框显示状态
const showPasswordDialog = ref(false);
const showPhoneDialog = ref(false);
const showEmailDialog = ref(false);
const showDevicesDialog = ref(false);

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

// 手机绑定表单
const phoneFormRef = ref<FormInstance>();
const phoneForm = reactive({
  phone: "",
  code: "",
});

const phoneCounting = ref(false);
const phoneCountdown = ref(60);
const phoneCountText = computed(() =>
  phoneCounting.value ? `${phoneCountdown.value}s` : "发送验证码"
);

// 邮箱绑定表单
const emailFormRef = ref<FormInstance>();
const emailForm = reactive({
  email: "",
  code: "",
});

const emailCounting = ref(false);
const emailCountdown = ref(60);
const emailCountText = computed(() =>
  emailCounting.value ? `${emailCountdown.value}s` : "发送验证码"
);

// 登录设备列表
const loginDevices = ref([
  {
    id: 1,
    name: "Windows 10 · Chrome 120",
    location: "北京市",
    time: "当前",
    current: true,
  },
  {
    id: 2,
    name: "iPhone 14 · Safari",
    location: "上海市",
    time: "2小时前",
    current: false,
  },
  {
    id: 3,
    name: "MacBook Pro · Chrome 119",
    location: "广州市",
    time: "1天前",
    current: false,
  },
]);

// 修改密码
const handlePasswordSubmit = async () => {
  if (!passwordFormRef.value) return;

  await passwordFormRef.value.validate((valid) => {
    if (valid) {
      // TODO: 调用API修改密码
      ElMessage.success("密码修改成功");
      showPasswordDialog.value = false;
      passwordForm.oldPassword = "";
      passwordForm.newPassword = "";
      passwordForm.confirmPassword = "";
    }
  });
};

// 发送手机验证码
const sendPhoneCode = () => {
  if (!phoneForm.phone) {
    ElMessage.warning("请输入手机号");
    return;
  }

  // TODO: 调用API发送验证码
  ElMessage.success("验证码已发送");
  phoneCounting.value = true;
  phoneCountdown.value = 60;

  const timer = setInterval(() => {
    phoneCountdown.value--;
    if (phoneCountdown.value <= 0) {
      clearInterval(timer);
      phoneCounting.value = false;
    }
  }, 1000);
};

// 绑定手机
const handlePhoneSubmit = () => {
  if (!phoneForm.phone || !phoneForm.code) {
    ElMessage.warning("请填写完整信息");
    return;
  }

  // TODO: 调用API绑定手机
  ElMessage.success(phoneBound.value ? "手机更换成功" : "手机绑定成功");
  phoneBound.value = true;
  showPhoneDialog.value = false;
  phoneForm.phone = "";
  phoneForm.code = "";
};

// 发送邮箱验证码
const sendEmailCode = () => {
  if (!emailForm.email) {
    ElMessage.warning("请输入邮箱地址");
    return;
  }

  // TODO: 调用API发送验证码
  ElMessage.success("验证码已发送");
  emailCounting.value = true;
  emailCountdown.value = 60;

  const timer = setInterval(() => {
    emailCountdown.value--;
    if (emailCountdown.value <= 0) {
      clearInterval(timer);
      emailCounting.value = false;
    }
  }, 1000);
};

// 绑定邮箱
const handleEmailSubmit = () => {
  if (!emailForm.email || !emailForm.code) {
    ElMessage.warning("请填写完整信息");
    return;
  }

  // TODO: 调用API绑定邮箱
  ElMessage.success(emailBound.value ? "邮箱更换成功" : "邮箱绑定成功");
  emailBound.value = true;
  showEmailDialog.value = false;
  emailForm.email = "";
  emailForm.code = "";
};

// 两步验证切换
const handleTwoFactorChange = (value: string | number | boolean) => {
  if (value) {
    ElMessage.success("两步验证已开启");
  } else {
    ElMessage.info("两步验证已关闭");
  }
};

// 移除设备
const handleRemoveDevice = (id: number) => {
  const index = loginDevices.value.findIndex((d) => d.id === id);
  if (index > -1) {
    loginDevices.value.splice(index, 1);
    ElMessage.success("设备已移除");
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
  font-size: 15px;
  padding: 12px 25px;
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

:deep(.el-form-item__label) {
  font-size: 16px;
  font-weight: 600;
}

:deep(.el-input__wrapper) {
  font-size: 16px;
  padding: 12px 15px;
  border-radius: 8px;
}

/* 设备列表 */
.devices-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
  max-height: 500px;
  overflow-y: auto;
}

.device-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 12px;
  border: 1px solid #eee;
  transition: all 0.3s ease;
}

.device-item:hover {
  background: #ecf0f1;
}

.device-icon {
  font-size: 30px;
  color: #3498db;
}

.device-info {
  flex: 1;
}

.device-info h4 {
  font-size: 16px;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 5px;
}

.device-info p {
  font-size: 14px;
  color: #7f8c8d;
}

:deep(.el-tag) {
  font-size: 14px;
  padding: 5px 12px;
  font-weight: 600;
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

  .security-item :deep(.el-button),
  .security-item :deep(.el-switch) {
    align-self: flex-end;
  }

  .device-item {
    flex-wrap: wrap;
  }

  .device-info {
    flex-basis: 100%;
  }

  .status-header {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
