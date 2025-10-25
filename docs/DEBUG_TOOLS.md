# 调试工具使用指南

## 认证调试工具 (debug-auth.ts)

### 概述

`debug-auth.ts` 提供了便捷的认证状态查看和清理工具,用于开发过程中调试登录、注册等认证相关功能。

### 🔒 安全特性

**仅在开发环境可用**: 调试工具仅在开发模式下暴露到 `window` 对象,生产环境不会包含这些方法,确保安全性。

```typescript
// 仅在开发环境下暴露
if (typeof window !== "undefined" && import.meta.env.DEV) {
  (window as any).debugAuth = debugAuthStatus;
  (window as any).cleanAuth = forceCleanAuth;
}
```

### 环境检查

| 环境                     | `import.meta.env.DEV` | window.debugAuth | window.cleanAuth |
| ------------------------ | --------------------- | ---------------- | ---------------- |
| 开发环境 (npm run dev)   | `true`                | ✅ 可用          | ✅ 可用          |
| 生产环境 (npm run build) | `false`               | ❌ 不存在        | ❌ 不存在        |

### 使用方法

#### 开发环境

打开浏览器控制台 (F12),输入以下命令:

**1. 查看认证状态**

```javascript
window.debugAuth();
```

输出示例:

```
🔍 认证状态调试
📦 Cookie:
  - token: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
  - 所有 Cookies: token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

💾 localStorage:
  - token: ❌ 不存在
  - userInfo: {"userId":1,"username":"testuser","email":"test@example.com"}
  - 用户ID: 1
  - 用户名: testuser
  - 邮箱: test@example.com
```

**2. 强制清除所有认证信息**

```javascript
window.cleanAuth();
```

功能:

- 清除所有可能的 Cookie (token, TOKEN, jwt, JWT)
- 清除所有 localStorage 数据
- 清除所有 sessionStorage 数据
- 尝试多种路径和域名组合确保完全清除

输出示例:

```
🧹 强制清除所有认证信息
✅ 已清除所有认证信息

🔍 认证状态调试
📦 Cookie:
  - token: ❌ 不存在
  - 所有 Cookies: ❌ 空

💾 localStorage:
  - token: ❌ 不存在
  - userInfo: ❌ 不存在
```

### 常见使用场景

#### 场景 1: 调试登录问题

1. 尝试登录
2. 运行 `window.debugAuth()` 查看 token 是否正确存储
3. 检查 userInfo 是否正确保存

#### 场景 2: 清理测试数据

在测试不同账号时,需要完全清除之前的认证信息:

```javascript
window.cleanAuth(); // 清除所有认证信息
// 然后重新登录测试
```

#### 场景 3: 排查 Cookie 问题

当遇到"Cookie 没有正确清除"的问题:

```javascript
window.debugAuth(); // 先查看当前状态
window.cleanAuth(); // 强制清除
window.debugAuth(); // 验证是否清除成功
```

#### 场景 4: 测试退出登录

```javascript
// 1. 登录成功后
window.debugAuth(); // 应该看到 token 和 userInfo

// 2. 点击退出登录

// 3. 检查清理是否完整
window.debugAuth(); // 应该看到所有数据都不存在
```

### 生产环境

在生产环境中,调试工具不会暴露:

```javascript
window.debugAuth();
// ❌ Uncaught TypeError: window.debugAuth is not a function

window.cleanAuth();
// ❌ Uncaught TypeError: window.cleanAuth is not a function
```

这确保了:

- ✅ 不会泄露调试接口给用户
- ✅ 减小生产环境打包体积
- ✅ 防止用户误操作清除自己的认证信息

### 高级用法

#### 直接导入使用 (开发和生产环境都可用)

如果需要在代码中使用这些功能:

```typescript
import { debugAuthStatus, forceCleanAuth } from "@/utils/debug-auth";

// 在代码中调用
function handleDebug() {
  debugAuthStatus();
}

function handleForceLogout() {
  forceCleanAuth();
  router.push("/login");
}
```

**注意**: 直接导入的方式在生产环境也会包含这些函数,仅用于特殊需求。

#### 条件导入 (推荐)

如果要在代码中使用但仅限开发环境:

```typescript
// 仅在开发环境导入
if (import.meta.env.DEV) {
  import("@/utils/debug-auth").then(({ debugAuthStatus }) => {
    debugAuthStatus();
  });
}
```

### 实现原理

#### 环境变量检查

Vite 提供的 `import.meta.env.DEV`:

- 开发环境 (`npm run dev`): `true`
- 生产环境 (`npm run build`): `false`

#### 构建优化

在生产构建时,未使用的代码会被 Tree Shaking 移除:

```typescript
// 开发环境: 完整保留
if (import.meta.env.DEV) {
  // 这段代码会被包含
  window.debugAuth = debugAuthStatus;
}

// 生产环境: 整个 if 块被移除
// (因为 import.meta.env.DEV === false)
```

### 最佳实践

✅ **推荐做法**:

- 在开发环境使用 `window.debugAuth()` 和 `window.cleanAuth()`
- 在生产环境依赖正常的登录/退出流程
- 不要在业务代码中依赖 `window.debugAuth`

❌ **避免做法**:

- 在生产环境代码中使用 `window.debugAuth`
- 将调试工具暴露给最终用户
- 在关键业务逻辑中依赖调试工具

### 扩展调试工具

如果需要添加更多调试功能,遵循相同的模式:

```typescript
// debug-auth.ts
export function myNewDebugTool() {
  // 调试逻辑
}

// 仅开发环境暴露
if (import.meta.env.DEV) {
  (window as any).myDebug = myNewDebugTool;
}
```

### TypeScript 类型支持

为 window 对象添加类型声明:

```typescript
// vite-env.d.ts 或 global.d.ts
interface Window {
  debugAuth?: () => void;
  cleanAuth?: () => void;
}
```

然后可以安全使用:

```typescript
// TypeScript 不会报错
window.debugAuth?.();
```

### 故障排查

#### 问题 1: 开发环境找不到 window.debugAuth

**原因**: 可能是热更新问题

**解决**:

1. 刷新页面 (Ctrl+R)
2. 检查控制台是否有 "💡 调试工具已加载" 消息
3. 确认运行的是 `npm run dev` 而不是 `npm run build`

#### 问题 2: debugAuth() 显示的数据不准确

**解决**:

1. 确认是最新数据: 刷新页面后再调用
2. 检查是否有其他标签页在使用同一个站点
3. 清除浏览器缓存后重试

#### 问题 3: cleanAuth() 后仍有残留数据

**原因**: 可能是不同域名或路径的 Cookie

**解决**:

1. 运行 `window.cleanAuth()` 多次
2. 手动在浏览器开发工具中查看并删除 Cookie
3. 使用无痕模式重新测试

## 总结

| 功能               | 开发环境 | 生产环境        |
| ------------------ | -------- | --------------- |
| window.debugAuth() | ✅ 可用  | ❌ 不可用       |
| window.cleanAuth() | ✅ 可用  | ❌ 不可用       |
| 导入函数直接调用   | ✅ 可用  | ⚠️ 可用但不推荐 |
| 条件导入           | ✅ 推荐  | ✅ 安全         |

调试工具让开发更高效,环境隔离让生产更安全! 🚀
