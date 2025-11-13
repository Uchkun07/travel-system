# 管理员登录功能使用指南

## 📋 功能概述

完整实现了前后端联调的管理员登录功能，包括：

- ✅ 前端登录界面
- ✅ 后端登录 API
- ✅ JWT token 认证
- ✅ PBKDF2 密码加密
- ✅ 自动跳转和状态管理
- ✅ 记住我功能

## 🚀 快速开始

### 1. 后端准备

#### 1.1 生成测试管理员密码

运行以下 Java 类生成测试管理员的密码哈希：

```java
// 运行 GenerateAdminPassword.main() 方法
// 位置: backend/springProject/src/main/java/io/github/uchkun07/travelsystem/util/GenerateAdminPassword.java
```

输出示例：

```
=== 管理员密码哈希信息 ===
原始密码: admin123
盐值 (password_salt): xxxx
密码哈希 (password): yyyy
迭代次数 (pbkdf2_iterations): 10000

=== SQL插入语句 ===
INSERT INTO `admin` (...) VALUES (...);
```

#### 1.2 插入测试数据

将生成的 SQL 语句复制到数据库执行，创建测试管理员账号。

#### 1.3 启动后端服务

```bash
cd backend/springProject
./mvnw spring-boot:run
# 或使用 start.ps1
```

确保后端运行在 `http://localhost:8080`

### 2. 前端准备

#### 2.1 安装依赖

```bash
cd frontend-apps/dashboard
pnpm install
```

#### 2.2 配置环境变量

检查 `.env.development` 文件：

```env
VITE_API_BASE_URL=http://localhost:8080
```

#### 2.3 启动前端服务

```bash
pnpm dev
```

前端运行在 `http://localhost:5123`

### 3. 测试登录

1. 打开浏览器访问 `http://localhost:5123/login`
2. 输入测试账号：
   - 用户名: `admin`
   - 密码: `admin123`
3. 可选勾选"记住我"
4. 点击登录按钮

登录成功后会自动跳转到仪表板页面。

## 📁 文件结构

### 后端文件

```
backend/springProject/
├── src/main/java/io/github/uchkun07/travelsystem/
│   ├── controller/
│   │   └── AdminController.java          # 管理员控制器
│   ├── service/
│   │   ├── IAdminService.java            # 管理员服务接口
│   │   └── impl/
│   │       └── AdminServiceImpl.java     # 管理员服务实现
│   ├── entity/
│   │   └── Admin.java                    # 管理员实体
│   ├── mapper/
│   │   └── AdminMapper.java              # 管理员Mapper
│   ├── dto/
│   │   ├── LoginRequest.java             # 登录请求DTO
│   │   ├── LoginResponse.java            # 登录响应DTO
│   │   └── ApiResponse.java              # 统一响应DTO
│   └── util/
│       ├── PasswordUtil.java             # 密码加密工具
│       ├── JwtUtil.java                  # JWT工具
│       ├── IpUtil.java                   # IP获取工具
│       └── GenerateAdminPassword.java    # 密码生成工具
└── docs/
    ├── ADMIN_LOGIN_API.md                # API文档
    └── ADMIN_LOGIN_IMPLEMENTATION.md     # 实现说明
```

### 前端文件

```
frontend-apps/dashboard/
├── src/
│   ├── apis/
│   │   ├── request.ts                    # Axios封装
│   │   └── auth.ts                       # 认证API
│   ├── stores/
│   │   └── auth.ts                       # 认证状态管理
│   ├── views/
│   │   └── LoginPage.vue                 # 登录页面
│   ├── components/
│   │   └── Login.vue                     # 登录组件
│   └── router/
│       └── index.ts                      # 路由配置
├── .env.development                      # 开发环境配置
└── .env.production                       # 生产环境配置
```

## 🔐 安全特性

### 密码加密

- **算法**: PBKDF2WithHmacSHA256
- **迭代次数**: 10000 次
- **盐值**: 每个账号独立的 32 字节随机盐值
- **哈希长度**: 256 位

### JWT Token

- **签名算法**: HS256
- **载荷**: userId, username
- **有效期**:
  - 普通登录: 短期（配置在 application.yml）
  - 记住我: 长期（配置在 application.yml）

### 前端安全

- Token 存储在 localStorage（记住我）或 sessionStorage
- 请求拦截器自动添加 Authorization 头
- 响应拦截器统一处理 401 错误
- 登出时清除所有本地存储

## 🔄 登录流程

```
1. 用户输入用户名和密码
   ↓
2. 前端表单验证
   ↓
3. 调用 POST /admin/login API
   ↓
4. 后端验证用户名和密码
   ↓
5. 生成JWT token
   ↓
6. 返回token和用户信息
   ↓
7. 前端保存到本地存储
   ↓
8. 自动跳转到仪表板
   ↓
9. 后续请求携带token进行认证
```

## 📡 API 接口

### 登录接口

**请求**:

```http
POST /admin/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123",
  "rememberMe": false
}
```

**成功响应**:

```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "userId": 1,
    "username": "admin",
    "fullName": "系统管理员",
    "email": "admin@example.com",
    "phone": "13800138000"
  }
}
```

**错误响应**:

```json
{
  "code": 400,
  "message": "用户名或密码错误",
  "data": null
}
```

### 登出接口

**请求**:

```http
POST /admin/logout
Authorization: Bearer {token}
```

## 🎯 使用示例

### 在其他组件中使用认证状态

```vue
<script setup lang="ts">
import { useAuthStore } from "@/stores/auth";

const authStore = useAuthStore();

// 获取当前用户信息
const user = authStore.user;

// 检查是否已登录
if (authStore.isLoggedIn) {
  console.log("已登录:", user);
}

// 登出
const handleLogout = async () => {
  await authStore.logout();
  router.push("/login");
};
</script>
```

### 在路由守卫中使用

```typescript
import { useAuthStore } from "@/stores/auth";

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();

  if (to.meta.requiresAuth && !authStore.isLoggedIn) {
    next("/login");
  } else {
    next();
  }
});
```

### 在 API 请求中自动携带 token

Token 会通过请求拦截器自动添加到请求头中：

```typescript
import { get, post } from "@/apis/request";

// 无需手动添加token，拦截器会自动处理
const result = await get("/admin/profile");
```

## ⚠️ 注意事项

### 开发环境

1. **CORS 配置**: 确保后端配置了 CORS，允许前端域名访问
2. **端口配置**:
   - 后端: `http://localhost:8080`
   - 前端: `http://localhost:5123`
3. **数据库**: 确保 MySQL 服务运行并创建了相应的表

### 生产环境

1. **HTTPS**: 生产环境必须使用 HTTPS 传输
2. **环境变量**: 修改 `.env.production` 中的 API 地址
3. **密码强度**: 修改默认管理员密码
4. **Token 过期**: 合理配置 token 过期时间
5. **密钥安全**: 保护好 JWT 密钥

## 🐛 常见问题

### 1. 登录时提示网络错误

**原因**: 后端服务未启动或 CORS 配置问题

**解决**:

- 检查后端服务是否运行在 8080 端口
- 检查后端 CORS 配置是否允许前端域名

### 2. 登录成功但没有跳转

**原因**: 路由配置问题

**解决**:

- 检查 `/dashboard` 路由是否存在
- 检查路由守卫配置

### 3. Token 过期后没有自动跳转登录页

**原因**: 响应拦截器未正确处理 401 错误

**解决**:

- 检查 `request.ts` 中的响应拦截器
- 确保 401 错误时清除了本地存储

### 4. 记住我功能不起作用

**原因**: localStorage 存储失败或读取失败

**解决**:

- 检查浏览器是否禁用了 localStorage
- 检查 `auth.ts` 中的存储逻辑

## 🔧 调试技巧

### 后端调试

1. **查看日志**:

```bash
# 登录成功日志
管理员登录成功: username=admin, adminId=1

# 登录失败日志
管理员登录失败: 用户名或密码错误
```

2. **检查数据库**:

```sql
-- 查看管理员账号
SELECT * FROM admin WHERE username = 'admin';

-- 查看登录信息
SELECT admin_id, username, last_login_time, last_login_ip, login_count
FROM admin;
```

### 前端调试

1. **浏览器控制台**:

```javascript
// 查看存储的token
console.log(localStorage.getItem("token"));

// 查看用户信息
console.log(JSON.parse(localStorage.getItem("user")));
```

2. **网络请求**:

- 打开浏览器开发者工具 → Network
- 查看登录请求的 payload 和 response
- 检查 Authorization 头是否正确添加

## 📚 相关文档

- [后端 API 文档](../../backend/springProject/docs/ADMIN_LOGIN_API.md)
- [后端实现说明](../../backend/springProject/docs/ADMIN_LOGIN_IMPLEMENTATION.md)
- [Element Plus 文档](https://element-plus.org/)
- [Vue Router 文档](https://router.vuejs.org/)
- [Pinia 文档](https://pinia.vuejs.org/)

## 🎉 下一步

登录功能完成后，你可以：

1. 实现权限管理
2. 添加验证码功能
3. 实现登录失败次数限制
4. 添加多因素认证
5. 实现 Token 自动刷新
6. 添加登录历史记录
7. 实现账号安全设置
