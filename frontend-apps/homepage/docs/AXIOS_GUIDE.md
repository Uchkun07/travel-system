# 前端 Axios 封装使用指南

## 目录结构

```
src/
├── apis/
│   ├── index.ts          # 统一导出
│   ├── request.ts        # Axios封装和拦截器
│   └── auth.ts           # 认证相关API
├── stores/
│   ├── index.ts          # Store统一导出
│   └── user.ts           # 用户状态管理
└── routers/
    ├── index.ts          # 路由配置
    └── guard.ts          # 路由守卫
```

## 功能特性

### ✅ Axios 封装

- 统一的请求/响应拦截器
- 自动携带 JWT Token
- 统一错误处理
- 401 自动跳转登录
- 支持文件上传/下载

### ✅ API 类型定义

- TypeScript 完整类型支持
- 请求/响应接口定义
- 智能代码提示

### ✅ 状态管理 (Pinia)

- 用户登录状态
- Token 管理
- 用户信息缓存

### ✅ 路由守卫

- 登录状态验证
- 自动跳转登录页
- Token 有效性检查

## 环境配置

### 1. 开发环境 (`.env.development`)

```env
VITE_API_BASE_URL=https://localhost:8080/api
```

### 2. 生产环境 (`.env.production`)

```env
VITE_API_BASE_URL=https://your-production-domain.com/api
```

## 使用示例

### 1. 基础 API 调用

```typescript
import { get, post } from "@/apis";

// GET 请求
const data = await get("/user/list", { page: 1, size: 10 });

// POST 请求
const result = await post("/user/create", { name: "test" });
```

### 2. 认证相关

#### 发送验证码

```typescript
import { sendVerificationCode } from "@/apis";

const sendCode = async () => {
  try {
    const res = await sendVerificationCode({ email: "test@example.com" });
    if (res.success) {
      ElMessage.success(res.message);
    }
  } catch (error) {
    // 错误已在拦截器中处理
  }
};
```

#### 用户注册

```typescript
import { register } from "@/apis";

const handleRegister = async () => {
  const formData = {
    username: "testuser",
    email: "test@example.com",
    password: "password123",
    confirmPassword: "password123",
    captcha: "123456",
  };

  const res = await register(formData);
  if (res.success) {
    ElMessage.success("注册成功");
    router.push("/login");
  }
};
```

#### 用户登录

```typescript
import { login } from "@/apis";
import { useUserStore } from "@/stores";

const handleLogin = async () => {
  const userStore = useUserStore();

  const success = await userStore.login({
    username: "testuser",
    password: "password123",
    rememberMe: true,
  });

  if (success) {
    router.push("/");
  }
};
```

#### 用户登出

```typescript
import { useUserStore } from "@/stores";

const handleLogout = async () => {
  const userStore = useUserStore();
  await userStore.logout();
  // 会自动跳转到登录页
};
```

### 3. 使用 Pinia Store

```vue
<script setup lang="ts">
import { useUserStore } from "@/stores";

const userStore = useUserStore();

// 获取用户信息
const username = computed(() => userStore.username);
const avatar = computed(() => userStore.avatar);
const isLoggedIn = computed(() => userStore.isLoggedIn);

// 登录
const login = async () => {
  await userStore.login({ username: "test", password: "123456" });
};

// 登出
const logout = async () => {
  await userStore.logout();
};
</script>

<template>
  <div>
    <div v-if="isLoggedIn">
      <p>欢迎, {{ username }}</p>
      <img :src="avatar" alt="头像" />
      <button @click="logout">退出</button>
    </div>
    <div v-else>
      <button @click="login">登录</button>
    </div>
  </div>
</template>
```

### 4. 文件上传

```typescript
import { upload } from "@/apis";

const handleUpload = async (file: File) => {
  try {
    const res = await upload("/file/upload", file, (progressEvent) => {
      const percent = Math.round(
        (progressEvent.loaded * 100) / progressEvent.total
      );
      console.log("上传进度:", percent + "%");
    });
    ElMessage.success("上传成功");
  } catch (error) {
    ElMessage.error("上传失败");
  }
};
```

### 5. 文件下载

```typescript
import { download } from "@/apis";

const handleDownload = async () => {
  try {
    await download("/file/download", "filename.pdf", { id: 1 });
    ElMessage.success("下载成功");
  } catch (error) {
    ElMessage.error("下载失败");
  }
};
```

## 路由守卫使用

### 在 main.ts 中引入

```typescript
import { createApp } from "vue";
import { createPinia } from "pinia";
import App from "./App.vue";
import { router } from "./routers";
import "./routers/guard"; // 引入路由守卫

const app = createApp(App);
app.use(createPinia());
app.use(router);
app.mount("#app");
```

### 路由配置示例

```typescript
import { createRouter, createWebHistory } from "vue-router";

const routes = [
  {
    path: "/",
    name: "Home",
    component: () => import("@/views/Home.vue"),
    meta: { title: "首页" },
  },
  {
    path: "/login",
    name: "Login",
    component: () => import("@/views/Login.vue"),
    meta: { title: "登录" },
  },
  {
    path: "/profile",
    name: "Profile",
    component: () => import("@/views/Profile.vue"),
    meta: { title: "个人中心", requiresAuth: true },
  },
];
```

### 白名单配置

在 `guard.ts` 中修改白名单:

```typescript
const whiteList = ["/", "/login", "/register", "/about"];
```

## 错误处理

### 统一错误处理

所有错误都在响应拦截器中统一处理:

- `401`: 自动清除 token 并跳转登录
- `403`: 显示无权限提示
- `404`: 显示资源不存在
- `500`: 显示服务器错误
- 网络错误: 显示网络连接提示

### 自定义错误处理

```typescript
import { post } from "@/apis";

try {
  const res = await post("/api/test");
  // 处理成功响应
} catch (error) {
  // 自定义错误处理
  console.error("请求失败:", error);
}
```

## API 接口列表

### 认证相关

| 方法 | 接口                  | 说明         |
| ---- | --------------------- | ------------ |
| POST | `/email/sendCode`     | 发送验证码   |
| POST | `/auth/register`      | 用户注册     |
| POST | `/auth/login`         | 用户登录     |
| POST | `/auth/logout`        | 用户登出     |
| GET  | `/auth/userInfo`      | 获取用户信息 |
| GET  | `/auth/checkUsername` | 检查用户名   |
| GET  | `/auth/checkEmail`    | 检查邮箱     |

## Token 管理

### Token 存储

- 存储位置: `localStorage.token`
- 格式: JWT Token
- 携带方式: `Authorization: Bearer {token}`

### Token 刷新

```typescript
import { useUserStore } from "@/stores";

const userStore = useUserStore();

// 刷新用户信息(验证token)
const refresh = async () => {
  const success = await userStore.refreshUserInfo();
  if (!success) {
    // token无效,跳转登录
    router.push("/login");
  }
};
```

## 完整示例: 登录组件

```vue
<script setup lang="ts">
import { ref } from "vue";
import { useUserStore } from "@/stores";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";

const router = useRouter();
const userStore = useUserStore();

const loginForm = ref({
  username: "",
  password: "",
  rememberMe: false,
});

const loading = ref(false);

const handleLogin = async () => {
  loading.value = true;
  try {
    const success = await userStore.login(loginForm.value);
    if (success) {
      const redirect = router.currentRoute.value.query.redirect as string;
      router.push(redirect || "/");
    }
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <el-form :model="loginForm">
    <el-form-item label="用户名">
      <el-input v-model="loginForm.username" placeholder="请输入用户名或邮箱" />
    </el-form-item>

    <el-form-item label="密码">
      <el-input
        v-model="loginForm.password"
        type="password"
        placeholder="请输入密码"
        @keyup.enter="handleLogin"
      />
    </el-form-item>

    <el-form-item>
      <el-checkbox v-model="loginForm.rememberMe">记住我(7天)</el-checkbox>
    </el-form-item>

    <el-form-item>
      <el-button type="primary" :loading="loading" @click="handleLogin">
        登录
      </el-button>
    </el-form-item>
  </el-form>
</template>
```

## 注意事项

1. **Token 过期**: 401 错误会自动清除 token 并跳转登录
2. **网络错误**: 检查 VITE_API_BASE_URL 配置
3. **CORS 问题**: 确保后端配置了正确的 CORS
4. **HTTPS**: 开发环境使用 https://localhost:8080
5. **类型安全**: 使用 TypeScript 类型定义避免错误

## 调试技巧

### 1. 查看请求日志

打开浏览器开发者工具 Network 标签

### 2. 查看 Token

```typescript
console.log("Token:", localStorage.getItem("token"));
console.log("UserInfo:", localStorage.getItem("userInfo"));
```

### 3. 手动测试 API

```typescript
import { post } from "@/apis";

// 在浏览器控制台测试
post("/auth/login", { username: "test", password: "123456" })
  .then(console.log)
  .catch(console.error);
```

## 下一步

- [ ] 完善登录/注册组件
- [ ] 添加表单验证
- [ ] 实现记住我功能
- [ ] 添加找回密码功能
- [ ] 实现个人中心页面
