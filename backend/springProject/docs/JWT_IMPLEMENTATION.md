# JWT 实现文档

## 概述

本项目使用 JWT (JSON Web Token) 实现用户认证和授权功能。

## 技术栈

- **JWT 库**: io.jsonwebtoken:jjwt (版本 0.12.6)
- **算法**: HS256 (HMAC with SHA-256)
- **Spring Boot**: 3.5.7
- **Java**: 21

## 核心组件

### 1. JwtUtil (JWT 工具类)

**位置**: `org.example.springproject.util.JwtUtil`

**功能**:

- 生成 JWT 令牌
- 解析 JWT 令牌
- 验证令牌有效性
- 提取用户信息

**配置参数**:

```yaml
jwt:
  secret: travel-system-secret-key-for-jwt-token-generation-and-validation-2024
  expiration: 604800000 # 7天(毫秒)
  expiration.short: 86400000 # 1天(毫秒)
```

**主要方法**:

- `generateToken(Long userId, String username, boolean rememberMe)`: 生成令牌
- `parseToken(String token)`: 解析令牌
- `getUserIdFromToken(String token)`: 获取用户 ID
- `getUsernameFromToken(String token)`: 获取用户名
- `validateToken(String token)`: 验证令牌
- `isTokenExpired(String token)`: 检查是否过期
- `getExpirationTime(String token)`: 获取过期时间

### 2. JwtInterceptor (JWT 拦截器)

**位置**: `org.example.springproject.config.JwtInterceptor`

**功能**:

- 拦截需要认证的请求
- 验证 JWT 令牌
- 检查令牌黑名单
- 提取用户信息到请求属性

**拦截逻辑**:

1. 从请求头 `Authorization: Bearer {token}` 中提取令牌
2. 验证令牌格式和有效性
3. 检查令牌是否在 Redis 黑名单中
4. 提取用户信息存储到请求属性: `userId`, `username`, `token`

**响应状态**:

- `401 Unauthorized`: 令牌无效/过期/在黑名单
- `500 Internal Server Error`: 服务器错误

### 3. WebMvcConfig (Web 配置)

**位置**: `org.example.springproject.config.WebMvcConfig`

**配置内容**:

- 注册 JWT 拦截器
- 配置拦截路径和排除路径

**公开接口** (无需认证):

- `/auth/register` - 注册
- `/auth/login` - 登录
- `/auth/checkUsername` - 检查用户名
- `/auth/checkEmail` - 检查邮箱
- `/email/sendCode` - 发送验证码
- `/swagger-ui/**` - Swagger UI
- `/v3/api-docs/**` - API 文档
- `/error` - 错误页面

**需要认证的接口**:

- 所有其他接口默认需要携带有效 JWT 令牌

## 登录流程

### 1. 用户登录

**接口**: `POST /api/auth/login`

**请求**:

```json
{
  "username": "testuser",
  "password": "password123",
  "rememberMe": true
}
```

**响应**:

```json
{
  "success": true,
  "message": "登录成功",
  "userId": 1,
  "username": "testuser",
  "email": "user@example.com",
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**后端处理** (`UserServiceImpl.login`):

1. 验证用户名和密码
2. 检查用户状态
3. 验证密码哈希
4. 调用 `jwtUtil.generateToken()` 生成令牌
5. 返回用户信息和令牌

**令牌有效期**:

- `rememberMe = true`: 7 天
- `rememberMe = false`: 1 天

### 2. 访问受保护接口

**请求头**:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**流程**:

1. 前端从 localStorage 获取 token
2. 在请求头中添加 `Authorization: Bearer {token}`
3. 后端拦截器验证令牌
4. 提取用户信息到请求属性
5. Controller 可以从 request 获取 `userId`, `username`

### 3. 用户登出

**接口**: `POST /api/auth/logout`

**请求头**:

```
Authorization: Bearer {token}
```

**后端处理** (`AuthController.logout`):

1. 从请求头提取令牌
2. 获取令牌过期时间
3. 将令牌加入 Redis 黑名单: `token_blacklist:{token}`
4. 设置过期时间与令牌一致
5. 返回成功响应

**前端处理**:

1. 调用登出接口
2. 清除 localStorage 中的 token 和 userInfo
3. 跳转到登录页

## Redis 存储

### 令牌黑名单

**Key 格式**: `token_blacklist:{完整的JWT令牌}`

**值**: `"1"`

**过期时间**: 与令牌过期时间一致

**用途**:

- 用户登出后,将令牌加入黑名单
- 拦截器检查黑名单,拒绝已登出的令牌

## 令牌结构

### Header (头部)

```json
{
  "alg": "HS256"
}
```

### Payload (负载)

```json
{
  "userId": 1,
  "username": "testuser",
  "sub": "testuser",
  "iat": 1704067200, // 签发时间
  "exp": 1704672000 // 过期时间
}
```

### Signature (签名)

使用密钥和 HS256 算法对 header 和 payload 进行签名

## 安全考虑

### 1. 密钥安全

- 密钥长度: 64 字节
- 存储: application.yml 配置文件
- 建议: 生产环境使用环境变量或密钥管理服务

### 2. 令牌有效期

- 短期令牌(1 天): 安全性高,用户体验稍差
- 长期令牌(7 天): 用户体验好,需要用户主动选择

### 3. 黑名单机制

- 登出后立即失效
- 使用 Redis 存储,自动过期
- 防止已登出令牌被重用

### 4. HTTPS 传输

- 生产环境必须使用 HTTPS
- 防止令牌被中间人拦截

### 5. XSS 防护

- 令牌存储在 localStorage
- 建议前端实施 CSP 策略
- 避免在 URL 中传递令牌

### 6. CSRF 防护

- JWT 存储在 localStorage 而非 Cookie
- 不会自动携带,防止 CSRF 攻击

## 错误处理

### 401 Unauthorized

**场景**:

- 未提供令牌
- 令牌格式错误
- 令牌已过期
- 令牌在黑名单中
- 令牌签名无效

**响应示例**:

```json
{
  "success": false,
  "message": "令牌无效或已过期"
}
```

**前端处理**:

1. 显示错误提示
2. 清除本地 token
3. 跳转到登录页

### 500 Internal Server Error

**场景**:

- 服务器内部异常
- Redis 连接失败
- 令牌解析异常

**响应示例**:

```json
{
  "success": false,
  "message": "服务器内部错误"
}
```

## 前端集成

### Axios 拦截器示例

```javascript
import axios from "axios";

const api = axios.create({
  baseURL: "https://localhost:8080/api",
});

// 请求拦截器
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// 响应拦截器
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem("token");
      window.location.href = "/login";
    }
    return Promise.reject(error);
  }
);

export default api;
```

### 使用示例

```javascript
import api from "@/apis/axios-config";

// 获取用户信息 (自动携带token)
const getUserInfo = async () => {
  const { data } = await api.get("/auth/userInfo");
  return data;
};

// 登出
const logout = async () => {
  const { data } = await api.post("/auth/logout");
  localStorage.removeItem("token");
  localStorage.removeItem("userInfo");
  return data;
};
```

## 测试

### 1. 登录获取令牌

```bash
curl -X POST https://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123",
    "rememberMe": true
  }'
```

### 2. 使用令牌访问接口

```bash
curl -X GET https://localhost:8080/api/auth/userInfo \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

### 3. 登出

```bash
curl -X POST https://localhost:8080/api/auth/logout \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

### 4. 验证黑名单

```bash
# 登出后再次访问应该返回401
curl -X GET https://localhost:8080/api/auth/userInfo \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

## 生产环境建议

1. **密钥管理**:

   - 使用环境变量或 AWS Secrets Manager
   - 定期轮换密钥
   - 不要将密钥提交到版本控制

2. **令牌刷新**:

   - 考虑实现 refresh token 机制
   - 短期 access token + 长期 refresh token

3. **监控和日志**:

   - 记录令牌验证失败次数
   - 监控异常访问模式
   - 设置告警规则

4. **性能优化**:

   - Redis 连接池配置
   - 黑名单查询缓存
   - 考虑使用本地缓存

5. **安全加固**:
   - 启用 HTTPS
   - 配置 CORS 白名单
   - 实施请求频率限制
   - 添加设备指纹验证

## 维护指南

### 更新 JWT 密钥

1. 修改 `application.yml` 中的 `jwt.secret`
2. 重启应用
3. 所有现有令牌将失效
4. 用户需要重新登录

### 调整令牌有效期

```yaml
jwt:
  expiration: 604800000 # 7天改为14天: 1209600000
  expiration.short: 86400000 # 1天改为12小时: 43200000
```

### 添加新的公开接口

在 `WebMvcConfig.addInterceptors()` 中添加:

```java
.excludePathPatterns(
    "/auth/register",
    "/your/new/public/api"  // 新增
);
```

## 常见问题

### Q: 令牌存储在哪里?

A: 前端存储在 localStorage,后端黑名单存储在 Redis。

### Q: 如何刷新令牌?

A: 当前实现不支持刷新,令牌过期后需要重新登录。可以考虑实现 refresh token 机制。

### Q: 如何在 Controller 中获取当前用户?

A: 从 HttpServletRequest 的属性中获取:

```java
Long userId = (Long) request.getAttribute("userId");
String username = (String) request.getAttribute("username");
```

### Q: 令牌泄露怎么办?

A: 用户登出后令牌立即加入黑名单失效。如需强制失效,可以直接在 Redis 中添加黑名单记录。

### Q: 为什么选择 localStorage 而不是 Cookie?

A:

- 防止 CSRF 攻击
- 更灵活的跨域支持
- 前端完全控制令牌发送
