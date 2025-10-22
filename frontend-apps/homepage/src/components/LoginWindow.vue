<template>
  <div class="login-overlay" v-if="visible" @click.self="handleClose">
    <div class="loginWindow">
      <div class="login-header">
        <h2>欢迎回来</h2>
        <button class="close-btn" @click="handleClose">×</button>
      </div>
      <div class="longdivider"></div>
      <ElForm :model="loginForm" :rules="loginRules" ref="loginFormRef">
        <el-form-item label="用户名" prop="username" label-position="top">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            autocomplete="off"
          />
        </el-form-item>
        <el-form-item label="密码" prop="password" label-position="top">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            autocomplete="off"
            show-password
          />
        </el-form-item>
        <div class="options">
          <label class="remember">
            <el-checkbox v-model="rememberMe">记住我</el-checkbox>
          </label>
          <ElLink class="forgot-password">忘记密码?</ElLink>
        </div>
      </ElForm>
      <div class="login-body">
        <el-button class="login-btn"> 登录 </el-button>

        <div class="divider">
          <span>或</span>
        </div>

        <div class="social-login">
          <el-button class="social-btn"> 微信登录 </el-button>
          <el-button class="social-btn"> QQ登录 </el-button>
        </div>

        <div class="register-link">
          还没有账号?
          <span>立即注册</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { FormInstance, FormRules } from "element-plus";
import { ref } from "vue";
import { ElMessage, ElInput, ElCheckbox, ElLink, ElButton } from "element-plus";

const visible = defineModel<boolean>();
const emit = defineEmits<{
  "show-register": [];
}>();

const loginFormRef = ref<FormInstance>();
const rememberMe = ref(false);
const loginForm = ref({
  username: "",
  password: "",
});

const loginRules: FormRules = {
  username: [
    { required: true, message: "请输入用户名", trigger: "blur" },
    { max: 20, message: "用户名长度不能超过20个字符", trigger: "blur" },
  ],
  password: [{ required: true, message: "请输入密码", trigger: "blur" }],
};

const handleClose = () => {
  visible.value = false;
};
</script>

<style scoped>
.login-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  backdrop-filter: blur(5px);
}

.loginWindow {
  background: white;
  border-radius: 16px;
  width: 90%;
  max-width: 420px;
  padding: 30px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  animation: slideIn 0.3s ease-out;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(-50px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.login-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.login-header h2 {
  margin: 0;
  font-size: 24px;
  color: #333;
  font-weight: 600;
}

.close-btn {
  background: none;
  border: none;
  font-size: 32px;
  color: #999;
  cursor: pointer;
  line-height: 1;
  transition: color 0.3s;
}

.close-btn:hover {
  color: #333;
}

.longdivider {
  height: 1px;
  background-color: #e0e0e0;
  margin-bottom: 20px;
  margin-top: 20px;
  margin-left: -30px;
  margin-right: -30px;
}

.login-body {
  padding: 0;
}

/* Element Plus 表单样式 */
:deep(.el-form) {
  margin-top: 8px;
}

/* 表单项整体 */
:deep(.el-form-item) {
  margin-bottom: 15px;
}

/* 去掉必填项的红色星号 */
:deep(.el-form-item__label::before) {
  display: none !important;
}

/* 表单项标签样式 */
:deep(.el-form-item__label) {
  color: #666 !important;
  font-size: 14px !important;
  font-weight: 500 !important;
  margin-bottom: 8px !important;
  padding: 0 !important;
}

/* 输入框容器样式 */
:deep(.el-input__wrapper) {
  border-radius: 8px !important;
  padding: 6px 16px !important;
  border: 2px solid #e0e0e0 !important;
  box-shadow: none !important;
  transition: all 0.3s ease !important;
}

:deep(.el-input__wrapper:hover) {
  border-color: #c0c0c0 !important;
}

/* 输入框聚焦样式 */
:deep(.el-input.is-focus .el-input__wrapper) {
  border-color: #4caf50 !important;
  box-shadow: 0 0 0 3px rgba(76, 175, 80, 0.1) !important;
}

/* 输入框内部样式 */
:deep(.el-input__inner) {
  font-size: 14px;
  color: #333;
}

:deep(.el-input__inner::placeholder) {
  color: #bbb;
}

/* 错误提示样式 */
:deep(.el-form-item__error) {
  color: #f56c6c;
  font-size: 12px;
  margin-top: 4px;
  animation: shake 0.3s;
}

/* 密码显示/隐藏图标样式 */
:deep(.el-input__suffix-inner) {
  color: #999;
  cursor: pointer;
  transition: color 0.3s;
}

:deep(.el-input__suffix-inner:hover) {
  color: #4caf50;
}

/* Checkbox 样式优化 */
:deep(.el-checkbox__label) {
  font-size: 14px;
  color: #666;
}

:deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
  background-color: #4caf50;
  border-color: #4caf50;
}

:deep(.el-checkbox__input.is-checked + .el-checkbox__label) {
  color: #333;
}

@keyframes shake {
  0%,
  100% {
    transform: translateX(0);
  }
  25% {
    transform: translateX(-5px);
  }
  75% {
    transform: translateX(5px);
  }
}

/* 登录按钮样式 */
.login-btn {
  width: 100%;
  height: 50px;
  padding: 0px;
  font-size: 16px;
  font-weight: 600;
  color: white;
  border-radius: 8px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s ease;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-btn:active {
  transform: translateY(0);
  box-shadow: 0 4px 10px rgba(102, 126, 234, 0.3);
}

/* 记住我和忘记密码区域 */
.options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  font-size: 14px;
}

.remember {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.forgot-password {
  color: #4caf50;
  font-size: 14px;
}

/* 分割线样式 */
.divider {
  text-align: center;
  margin: 24px 0;
  position: relative;
}

.divider::before {
  content: "";
  position: absolute;
  left: 0;
  top: 50%;
  width: 100%;
  height: 1px;
  background: #e0e0e0;
}

.divider span {
  position: relative;
  background: white;
  padding: 0 16px;
  color: #999;
  font-size: 14px;
}

/* 社交登录按钮 */
.social-login {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.social-btn {
  flex: 1;
  padding: 12px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  background: white;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.social-btn:hover {
  border-color: #4caf50;
  color: #4caf50;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(76, 175, 80, 0.2);
}

/* 注册链接 */
.register-link {
  text-align: center;
  font-size: 14px;
  color: #666;
}

.register-link span {
  font-weight: 600;
  font-size: 14px;
  color: #4caf50;
  cursor: pointer;
  transition: color 0.3s ease;
}

.register-link span:hover {
  color: #45a049;
  text-decoration: underline;
}
</style>
