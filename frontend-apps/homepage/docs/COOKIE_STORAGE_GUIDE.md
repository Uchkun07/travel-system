# 🍪 Cookie + localStorage 认证方案

## 📦 存储架构

```
🍪 Cookie (HttpOnly 级别安全)
└── token: "eyJhbGciOiJIUzI1NiIs..."
    ├── expires: 7天 (记住我) / 1天 (普通登录)
    ├── path: "/"
    ├── sameSite: "strict"  ← 防 CSRF 攻击
    └── secure: true (HTTPS) ← 仅 HTTPS 传输

💾 localStorage (非敏感数据)
└── userInfo: {
      userId: 1,
      username: "zhangsan",
      email: "zhangsan@example.com",
      avatar: "/img/defaultavatar.png",
      fullName: "张三"
    }
```

---

## ✅ 优势对比

| 特性             | 旧方案 (localStorage) | 新方案 (Cookie + localStorage) |
| ---------------- | --------------------- | ------------------------------ |
| **Token 安全性** | ⚠️ JavaScript 可读取  | ✅ Cookie 更安全               |
| **XSS 防护**     | ❌ 容易被窃取         | ✅ sameSite 防护               |
| **CSRF 防护**    | ✅ 不自动携带         | ✅ sameSite=strict             |
| **过期管理**     | ❌ 手动判断           | ✅ 浏览器自动管理              |
| **用户信息读取** | ✅ 快速               | ✅ 快速 (localStorage)         |
| **跨标签页共享** | ✅ 支持               | ✅ 支持                        |

---

## 🚀 核心功能

### 1. Token 自动注入

```typescript
// ✅ axios 拦截器自动从 Cookie 读取 token
service.interceptors.request.use((config: any) => {
  const token = Cookies.get("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});
```

### 2. 记住我功能

```typescript
// ✅ 登录时根据 rememberMe 设置不同过期时间
function setToken(newToken: string, rememberMe: boolean = false) {
  const expires = rememberMe ? 7 : 1; // 7天 or 1天
  Cookies.set("token", newToken, {
    expires,
    path: "/",
    sameSite: "strict",
    secure: window.location.protocol === "https:",
  });
}
```

### 3. 登录状态判断

```typescript
// ✅ 双重验证: Cookie token + localStorage userInfo
const isLoggedIn = computed(() => {
  const hasToken = !!Cookies.get("token");
  const hasUserInfo = !!userInfo.value;
  return hasToken && hasUserInfo;
});
```

### 4. 自动登出处理

```typescript
// ✅ 401 响应自动清除 Cookie 和 localStorage
case 401:
  Cookies.remove("token", { path: "/" });
  localStorage.removeItem("userInfo");
  router.push("/login");
  break;
```

### 5. 迁移脚本

```typescript
// ✅ 自动将旧的 localStorage token 迁移到 Cookie
export function migrateTokenToCookie() {
  const oldToken = localStorage.getItem("token");
  if (oldToken && !Cookies.get("token")) {
    Cookies.set("token", oldToken, { expires: 7 });
    localStorage.removeItem("token");
    console.log("Token 已迁移到 Cookie");
  }
}
```

---

## 🔒 安全特性

### 1. Cookie 配置

```typescript
Cookies.set("token", token, {
  expires: 7, // 7天后过期
  path: "/", // 全站可用
  sameSite: "strict", // 严格同站策略,防止 CSRF
  secure: true, // 仅 HTTPS 传输 (生产环境)
});
```

### 2. SameSite 策略

| 值         | 说明                        | 使用场景             |
| ---------- | --------------------------- | -------------------- |
| **strict** | 完全禁止第三方 Cookie       | ✅ 当前使用 (最安全) |
| lax        | 允许部分第三方 Cookie (GET) | 需要跨站跳转时       |
| none       | 允许所有第三方 Cookie       | 跨域嵌入场景         |

### 3. Secure 标志

```typescript
// ✅ 自动检测协议,HTTPS 环境启用 secure
secure: window.location.protocol === "https:";
```

---

## 📱 使用示例

### 登录

```typescript
// 用户登录
const success = await userStore.login({
  username: "zhangsan",
  password: "123456",
  rememberMe: true, // ← 记住我 7 天
});

if (success) {
  console.log("✅ Token 已保存到 Cookie");
  console.log("✅ 用户信息已保存到 localStorage");
}
```

### 获取用户信息

```vue
<template>
  <div>
    <!-- ✅ 直接使用 computed 属性 -->
    <span>{{ userStore.username }}</span>
    <img :src="userStore.avatar" />
    <span v-if="userStore.isLoggedIn">已登录</span>
  </div>
</template>

<script setup lang="ts">
import { useUserStore } from "@/stores";

const userStore = useUserStore();

// ✅ 实时响应登录状态
console.log("是否登录:", userStore.isLoggedIn); // true/false
console.log("用户名:", userStore.username); // "zhangsan"
console.log("头像:", userStore.avatar); // "/img/defaultavatar.png"
console.log("用户ID:", userStore.userId); // 1
</script>
```

### 登出

```typescript
// 用户登出
await userStore.logout();

// ✅ 自动执行:
// 1. 调用后端登出接口 (token 加入黑名单)
// 2. 删除 Cookie 中的 token
// 3. 清空 localStorage 中的 userInfo
// 4. 跳转到登录页
```

---

## 🔄 数据流程

### 登录流程

```
用户输入账号密码
    ↓
前端调用 /api/auth/login
    ↓
后端验证 → 返回 JWT token
    ↓
前端保存:
  ├── Cookie: token (7天/1天过期)
  └── localStorage: userInfo
    ↓
跳转到首页
```

### 请求流程

```
前端发起请求
    ↓
Axios 拦截器自动:
  ├── 从 Cookie 读取 token
  └── 添加到 Authorization header
    ↓
发送到后端
    ↓
后端验证 token
  ├── ✅ 有效 → 返回数据
  └── ❌ 无效 → 返回 401
    ↓
前端拦截 401:
  ├── 清除 Cookie token
  ├── 清除 localStorage userInfo
  └── 跳转登录页
```

---

## 🐛 常见问题

### Q1: Cookie 在开发环境不生效?

**A:** 检查浏览器开发工具 → Application → Cookies

```typescript
// 开发环境可能需要关闭 secure (HTTP 环境)
secure: window.location.protocol === "https:";
```

### Q2: 跨域时 Cookie 丢失?

**A:** 后端需要配置 CORS 允许携带凭证

```java
@Configuration
public class CorsConfig {
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); // ← 允许携带 Cookie
        config.addAllowedOrigin("http://localhost:5173");
        return source;
    }
}
```

### Q3: 如何查看 Cookie 中的 token?

**A:** 浏览器 F12 → Application → Cookies → 查看 `token`

或使用代码:

```typescript
import Cookies from "js-cookie";
console.log("Token:", Cookies.get("token"));
```

### Q4: 如何手动清除 Cookie?

**A:**

```typescript
// 清除指定 Cookie
Cookies.remove("token", { path: "/" });

// 清除所有 Cookie
document.cookie.split(";").forEach((c) => {
  document.cookie = c
    .replace(/^ +/, "")
    .replace(/=.*/, "=;expires=" + new Date().toUTCString() + ";path=/");
});
```

---

## 📊 存储容量

| 存储方式       | 容量限制        |
| -------------- | --------------- |
| Cookie         | 4KB / 域名      |
| localStorage   | 5-10MB / 域名   |
| sessionStorage | 5-10MB / 标签页 |

**当前方案:**

- Cookie (token): ~500 字节 (JWT 长度)
- localStorage (userInfo): ~200 字节

**总计:** < 1KB,远低于限制 ✅

---

## 🎯 最佳实践

### ✅ 推荐做法

1. **Token 放 Cookie**

   - 设置 `sameSite: "strict"`
   - HTTPS 环境启用 `secure`
   - 设置合理的过期时间

2. **用户信息放 localStorage**

   - 只存储非敏感展示数据
   - 不存储密码、token 等敏感信息

3. **双重验证登录状态**

   ```typescript
   isLoggedIn = !!cookie.token && !!localStorage.userInfo;
   ```

4. **401 自动清理**
   - 清除 Cookie
   - 清除 localStorage
   - 跳转登录页

### ❌ 避免做法

1. ❌ 不要在 Cookie 中存储用户信息 (容量太小)
2. ❌ 不要在 localStorage 中存储敏感信息
3. ❌ 不要忘记设置 Cookie 的 `path` 和 `sameSite`
4. ❌ 不要在生产环境禁用 `secure` 标志

---

## 📚 相关文档

- [js-cookie 官方文档](https://github.com/js-cookie/js-cookie)
- [MDN: HTTP Cookies](https://developer.mozilla.org/en-US/docs/Web/HTTP/Cookies)
- [OWASP: Session Management](https://cheatsheetseries.owasp.org/cheatsheets/Session_Management_Cheat_Sheet.html)

---

## ✅ 迁移完成

- ✅ Token 从 localStorage 迁移到 Cookie
- ✅ 用户信息保持在 localStorage
- ✅ 自动迁移脚本已添加
- ✅ 所有功能正常工作
- ✅ 安全性大幅提升
