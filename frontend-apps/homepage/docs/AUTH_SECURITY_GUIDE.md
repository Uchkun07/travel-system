# 🔐 认证与安全指南

## 📊 当前方案: localStorage + Token

### ✅ 已实现的安全特性

1. **JWT Token 机制**
   - Token 存储在 `localStorage`
   - 自动注入到请求头: `Authorization: Bearer {token}`
   - 支持"记住我"功能(7天/1天过期时间)

2. **Token 黑名单**
   - 用户登出时,token 加入 Redis 黑名单
   - 防止被盗用的 token 继续使用

3. **用户信息分离**
   - **Token**: 敏感凭证,用于身份验证
   - **UserInfo**: 非敏感展示数据(用户名、头像等)

4. **登录状态判断**
   ```typescript
   // ✅ 双重验证: token 存在 && userInfo 存在
   const isLoggedIn = computed(() => !!token.value && !!userInfo.value);
   ```

5. **默认头像处理**
   ```typescript
   // ✅ 如果用户头像为空,自动使用默认头像
   const avatar = computed(() => userInfo.value?.avatar || "/img/defaultavatar.png");
   ```

---

## 🎯 推荐方案对比

### 方案 A: localStorage (当前方案) ⭐⭐⭐

**优点:**
- ✅ 实现简单,前后端分离友好
- ✅ 跨标签页共享登录状态
- ✅ 容量大(5-10MB)
- ✅ 支持"记住我"功能

**缺点:**
- ⚠️ 容易被 XSS 攻击窃取 token
- ⚠️ 需要手动管理过期时间
- ⚠️ JavaScript 可直接访问 token

**适用场景:**
- 学习项目 / MVP 快速开发
- 内部系统 / 低风险应用
- 前后端完全分离的应用

---

### 方案 B: HttpOnly Cookie (推荐生产环境) ⭐⭐⭐⭐⭐

**优点:**
- ✅ 防 XSS 攻击 (JavaScript 无法读取)
- ✅ 浏览器自动携带,无需手动添加 header
- ✅ 可设置 Secure(HTTPS only)、SameSite(防 CSRF)
- ✅ 自动管理过期时间

**缺点:**
- ⚠️ 需要前后端配合改造
- ⚠️ 跨域需要配置 CORS `credentials: true`
- ⚠️ 容量小(4KB)

**适用场景:**
- 生产环境 / 金融/支付应用
- 高安全要求的系统
- 同域或子域部署

---

## 🚀 升级到 HttpOnly Cookie 方案(未来)

### 后端改造 (Spring Boot)

```java
@PostMapping("/login")
public Map<String, Object> login(
    @RequestBody LoginRequest request,
    HttpServletResponse response  // 添加 response 参数
) {
    // ... 验证逻辑 ...
    
    // 生成 JWT token
    String token = jwtUtil.generateToken(...);
    
    // ✅ 设置 HttpOnly Cookie
    Cookie cookie = new Cookie("token", token);
    cookie.setHttpOnly(true);        // 防止 JavaScript 访问
    cookie.setSecure(true);          // 仅 HTTPS 传输
    cookie.setPath("/");             // 全站可用
    cookie.setMaxAge(7 * 24 * 60 * 60); // 7天过期
    cookie.setAttribute("SameSite", "Strict"); // 防 CSRF
    response.addCookie(cookie);
    
    // ❌ 不再返回 token 到响应体
    result.remove("token");
    return result;
}
```

### 前端改造 (Vue 3 + Axios)

```typescript
// ✅ axios 配置添加
axios.defaults.withCredentials = true; // 允许携带 cookie

// ✅ 登录时不再保存 token
async function login(loginData: LoginRequest) {
  const res = await apiLogin(loginData);
  
  if (res.success) {
    // ❌ 不再保存 token (由浏览器自动管理 cookie)
    // setToken(res.token); 
    
    // ✅ 只保存用户信息
    setUserInfo({
      userId: res.userId!,
      username: res.username!,
      // ...
    });
    
    return true;
  }
}

// ✅ 请求拦截器不需要手动添加 token
request.interceptors.request.use((config) => {
  // ❌ 不需要手动添加 Authorization header
  // config.headers.Authorization = `Bearer ${token}`;
  
  // ✅ 浏览器自动携带 cookie
  return config;
});
```

---

## 🛡️ 安全最佳实践

### 1. XSS 防护

```html
<!-- ❌ 不要这样做 -->
<div v-html="userInput"></div>

<!-- ✅ 使用文本插值,Vue 自动转义 -->
<div>{{ userInput }}</div>
```

### 2. CSRF 防护

```typescript
// ✅ 后端配置 CSRF Token
@Configuration
public class SecurityConfig {
    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        return CookieCsrfTokenRepository.withHttpOnlyFalse();
    }
}
```

### 3. HTTPS 强制

```yaml
# application.yml
server:
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-password: 123456
```

### 4. Token 过期处理

```typescript
// ✅ 401 自动跳转登录
axios.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      userStore.clearUserInfo();
      router.push("/login");
    }
    return Promise.reject(error);
  }
);
```

---

## 📝 isLogin 参数设计

### ❌ 不推荐: 后端返回 isLogin

```java
// ❌ 不推荐
result.put("isLogin", true);
```

**问题:**
- 冗余字段,可以前端计算
- 后端需要额外维护
- 前端可能不同步

### ✅ 推荐: 前端 computed 计算

```typescript
// ✅ 推荐
const isLoggedIn = computed(() => {
  return !!token.value && !!userInfo.value;
});
```

**优点:**
- 实时反映登录状态
- 逻辑简单清晰
- 自动同步,无需手动维护

---

## 🔄 数据存储策略

| 数据类型 | 存储位置 | 原因 |
|---------|---------|------|
| **JWT Token** | localStorage (当前)<br>Cookie (推荐) | 身份凭证 |
| **用户ID** | localStorage (userInfo) | 快速访问 |
| **用户名** | localStorage (userInfo) | 展示使用 |
| **邮箱** | localStorage (userInfo) | 展示使用 |
| **头像** | localStorage (userInfo) | 展示使用 |
| **密码** | ❌ 永不存储 | 安全风险 |
| **敏感信息** | ❌ 不存储 | 每次请求获取 |

---

## 📚 参考资源

- [OWASP JWT Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/JSON_Web_Token_for_Java_Cheat_Sheet.html)
- [MDN: HTTP Cookies](https://developer.mozilla.org/en-US/docs/Web/HTTP/Cookies)
- [JWT.io](https://jwt.io/)
- [Vue 3 Security Best Practices](https://vuejs.org/guide/best-practices/security.html)

---

## ✅ 当前状态总结

- ✅ Token 存储在 `localStorage`
- ✅ 用户信息存储在 `localStorage`
- ✅ `isLoggedIn` 由前端 computed 计算
- ✅ 默认头像自动处理
- ✅ Token 黑名单机制
- ✅ 401 自动登出

**下一步建议:**
1. 生产环境升级到 HttpOnly Cookie
2. 添加 CSRF Token 防护
3. 启用 HTTPS 强制
4. 实现刷新 Token 机制
