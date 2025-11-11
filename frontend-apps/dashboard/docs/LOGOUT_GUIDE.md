# 退出登录功能说明

## 📋 功能描述

实现管理员退出登录后自动跳转到登录页面，并清除所有登录状态信息。

## 🔄 退出登录流程

### 1. 用户触发退出

**位置**: 顶部导航栏 → 用户头像下拉菜单 → 退出登录

**组件**: `Banner.vue`

```typescript
const handleLogout = async () => {
  try {
    // 1. 显示确认对话框
    await ElMessageBox.confirm("确定要退出登录吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    // 2. 调用 store 的 logout 方法
    await authStore.logout();

    // 3. 跳转到登录页
    router.push("/login");
  } catch (error) {
    // 用户取消操作
    console.log("取消退出登录");
  }
};
```

### 2. Store 处理退出逻辑

**位置**: `stores/auth.ts`

```typescript
const logout = async () => {
  try {
    // 调用后端登出API（可选）
    await adminLogout();
  } catch (error) {
    console.error("登出API调用失败:", error);
  } finally {
    // 无论API调用成功与否，都清除本地数据
    user.value = null;
    token.value = "";

    // 清除 Cookie 中的 token
    Cookies.remove("token");

    // 清除 localStorage 中的用户信息
    localStorage.removeItem("user");
    sessionStorage.removeItem("user");
    sessionStorage.removeItem("token");

    ElMessage.success("已退出登录");
  }
};
```

### 3. 清除的数据

#### Cookie

- ✅ `token` - JWT 认证令牌

#### localStorage

- ✅ `user` - 用户信息（JSON 字符串）

#### sessionStorage（兼容旧版本）

- ✅ `user` - 用户信息
- ✅ `token` - Token

#### Pinia Store

- ✅ `user.value = null`
- ✅ `token.value = ""`

### 4. 路由跳转

退出登录后跳转到登录页面：

```typescript
router.push("/login");
```

## 🛡️ 自动退出场景

### 401 未授权错误

当后端返回 401 状态码时，系统会自动：

**位置**: `apis/request.ts` - 响应拦截器

```typescript
case 401:
  ElMessage.error(data.message || "登录已过期,请重新登录");

  // 清除所有登录信息
  Cookies.remove("token");
  localStorage.removeItem("user");
  sessionStorage.removeItem("user");
  sessionStorage.removeItem("token");

  // 跳转到登录页
  window.location.href = "/login";
  break;
```

**触发场景**：

- Token 过期
- Token 无效
- Token 被篡改
- 未登录访问需要认证的接口

## 📊 完整流程图

```
用户点击退出登录
    ↓
显示确认对话框
    ↓
用户确认
    ↓
调用 authStore.logout()
    ↓
├─ 调用后端 /admin/logout API (可选)
├─ 清除 Cookie: token
├─ 清除 localStorage: user
├─ 清除 sessionStorage: user, token
├─ 清空 Pinia Store 状态
└─ 显示成功消息
    ↓
router.push("/login")
    ↓
跳转到登录页面
    ↓
路由守卫检查登录状态
    ↓
允许访问登录页
```

## 🧪 测试步骤

### 1. 正常退出登录

1. **登录系统**
   - 访问 `http://localhost:5173/login`
   - 输入账号密码登录成功
2. **进入管理后台**

   - 自动跳转到 `/dashboard`
   - 顶部显示用户头像和用户名

3. **点击退出登录**

   - 点击用户头像
   - 选择"退出登录"
   - 确认对话框点击"确定"

4. **验证结果**
   - ✅ 显示"已退出登录"成功消息
   - ✅ 自动跳转到 `/login` 页面
   - ✅ Cookie 中的 token 被删除
   - ✅ localStorage 中的 user 被删除

### 2. 取消退出登录

1. **点击退出登录**
   - 点击用户头像
   - 选择"退出登录"
2. **取消操作**

   - 确认对话框点击"取消"

3. **验证结果**
   - ✅ 留在当前页面
   - ✅ 登录状态保持不变
   - ✅ 不显示任何消息

### 3. Token 过期自动退出

1. **模拟 Token 过期**

   - 手动修改 Cookie 中的 token 为无效值
   - 或等待 Token 自然过期

2. **访问需要认证的接口**

   - 点击任意菜单
   - 或刷新页面

3. **验证结果**
   - ✅ 显示"登录已过期,请重新登录"错误消息
   - ✅ 自动跳转到 `/login` 页面
   - ✅ 所有登录信息被清除

## 🔍 浏览器开发者工具验证

### 退出前

**Application → Cookies**

```
token: eyJhbGciOiJIUzI1NiJ9...
```

**Application → Local Storage**

```javascript
{
  "user": "{\"userId\":1,\"username\":\"admin\",...}"
}
```

### 退出后

**Application → Cookies**

```
(空 - token 已删除)
```

**Application → Local Storage**

```javascript
{
  (空 - user 已删除)
}
```

## 🎯 关键代码位置

| 文件                    | 功能                     |
| ----------------------- | ------------------------ |
| `components/Banner.vue` | 退出登录按钮和确认对话框 |
| `stores/auth.ts`        | logout 方法实现          |
| `apis/request.ts`       | 401 自动退出处理         |
| `router/index.ts`       | 路由守卫（登录检查）     |

## 💡 最佳实践

1. **双重确认**：退出前显示确认对话框，防止误操作

2. **完整清理**：清除所有存储位置的登录信息

   - Cookie
   - localStorage
   - sessionStorage
   - Pinia Store

3. **异步处理**：使用 `await` 等待 logout 完成后再跳转

4. **错误处理**：即使后端 API 调用失败，也要清除本地数据

5. **安全跳转**：使用 `router.push` 而不是 `window.location.href`（除非是拦截器中）

## 🔄 与登录的完整循环

```
登录 → 保存 Token 到 Cookie
    ↓
保存用户信息到 localStorage
    ↓
使用系统（所有请求携带 Token）
    ↓
退出登录 → 清除所有数据
    ↓
跳转到登录页
    ↓
重新登录...
```

---

**文档更新时间**: 2025-11-11
**功能版本**: v1.0.0
