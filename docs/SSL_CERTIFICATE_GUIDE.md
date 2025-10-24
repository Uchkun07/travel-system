# 🔒 SSL 证书配置指南

## ✅ 已完成的配置

### 1. 前端自签名证书导入到 Java 信任库

```bash
# 证书信息
- 证书文件: localhost+2.pem
- 证书别名: frontend-localhost
- 导入位置: $JAVA_HOME/lib/security/cacerts
- 证书指纹: E9:BA:58:C5:57:9C:A4:A6:39:09:88:44:56:D8:22:54:A6:62:2D:7E:B8:F2:71:08:94:90:B5:64:C7:AD:DE:C8
```

**导入命令:**

```bash
keytool -import -alias frontend-localhost \
  -file localhost+2.pem \
  -keystore "$env:JAVA_HOME\lib\security\cacerts" \
  -storepass changeit \
  -noprompt
```

**验证命令:**

```bash
keytool -list -alias frontend-localhost \
  -keystore "$env:JAVA_HOME\lib\security\cacerts" \
  -storepass changeit
```

---

## 🔍 网络问题排查

### 问题 1: SSL Handshake 失败

**症状:**

```
javax.net.ssl.SSLHandshakeException: PKIX path building failed
```

**解决方案:** ✅ 已解决

- 已将前端证书导入到 Java 信任库

---

### 问题 2: CORS 错误

**症状:**

```
Access to XMLHttpRequest blocked by CORS policy
```

**当前配置:** ✅ 已配置

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOriginPatterns("http://localhost:*", "https://localhost:*")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600);
    }
}
```

---

### 问题 3: 连接超时

**症状:**

```
Connection timeout after 15000ms
```

**检查项:**

1. ✅ 后端是否启动 (端口 8080)
2. ✅ 前端是否启动 (端口 5173)
3. ✅ 防火墙是否允许端口
4. ✅ SSL 证书是否有效

**调整超时时间:**

```typescript
// src/apis/request.ts
const service = axios.create({
  timeout: 30000, // 增加到 30 秒
});
```

---

### 问题 4: Cookie 未携带

**症状:**

- 前端发送请求时 Cookie 未被携带
- 后端收不到 Authorization header

**解决方案:**

```typescript
// ✅ 确保 axios 配置了 withCredentials
axios.defaults.withCredentials = true;

// ✅ 确保 Cookie sameSite 设置正确
Cookies.set("token", token, {
  sameSite: "strict",
  secure: true, // HTTPS 环境
});
```

---

## 🧪 测试步骤

### 1. 测试后端 HTTPS

```bash
# PowerShell
Invoke-WebRequest -Uri "https://localhost:8080/api/auth/checkUsername?username=test" -SkipCertificateCheck

# 或使用 curl
curl -k https://localhost:8080/api/auth/checkUsername?username=test
```

**预期结果:**

```json
{
  "success": true,
  "available": true,
  "message": "用户名可用"
}
```

### 2. 测试前端到后端请求

```javascript
// 浏览器控制台
fetch("https://localhost:8080/api/auth/checkUsername?username=test", {
  credentials: "include",
})
  .then((res) => res.json())
  .then((data) => console.log(data));
```

### 3. 检查证书信任

```bash
# 列出所有信任的证书
keytool -list -keystore "$env:JAVA_HOME\lib\security\cacerts" -storepass changeit | Select-String "frontend"
```

---

## 🔧 常见命令

### 证书管理

```bash
# 导入证书
keytool -import -alias <别名> -file <证书文件> \
  -keystore "$env:JAVA_HOME\lib\security\cacerts" \
  -storepass changeit

# 查看证书
keytool -list -alias <别名> \
  -keystore "$env:JAVA_HOME\lib\security\cacerts" \
  -storepass changeit

# 删除证书
keytool -delete -alias <别名> \
  -keystore "$env:JAVA_HOME\lib\security\cacerts" \
  -storepass changeit

# 列出所有证书
keytool -list -keystore "$env:JAVA_HOME\lib\security\cacerts" \
  -storepass changeit
```

### 网络调试

```bash
# 测试端口是否开放
Test-NetConnection -ComputerName localhost -Port 8080
Test-NetConnection -ComputerName localhost -Port 5173

# 查看进程占用端口
netstat -ano | findstr :8080
netstat -ano | findstr :5173

# 杀死进程
Stop-Process -Id <PID> -Force
```

---

## 📋 环境配置

### 前端环境变量 (.env.development)

```env
VITE_API_BASE_URL=https://localhost:8080/api
```

### 后端配置 (application.yml)

```yaml
server:
  port: 8080
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-password: 123456
    key-store-type: PKCS12
    key-alias: springboot
```

---

## 🚨 重启服务

修改证书配置后需要重启:

### 1. 重启后端

```bash
# 停止 Spring Boot
Ctrl + C

# 重新启动
./mvnw spring-boot:run
```

### 2. 重启前端

```bash
# 停止 Vite
Ctrl + C

# 重新启动
pnpm run dev
```

---

## 🔐 安全建议

### 生产环境

1. **使用真实证书**

   - 从受信任的 CA (Let's Encrypt, DigiCert 等) 获取
   - 不要使用自签名证书

2. **更新 CORS 配置**

   ```java
   // 只允许生产域名
   .allowedOrigins("https://yourdomain.com")
   ```

3. **启用 HTTPS 强制**

   ```java
   @Configuration
   public class SecurityConfig {
       @Bean
       public SecurityFilterChain filterChain(HttpSecurity http) {
           http.requiresChannel()
               .anyRequest()
               .requiresSecure();
           return http.build();
       }
   }
   ```

4. **Cookie 安全设置**
   ```typescript
   Cookies.set("token", token, {
     secure: true, // 仅 HTTPS
     sameSite: "strict", // 防 CSRF
     httpOnly: false, // 如果可能,改为 true
   });
   ```

---

## 📚 相关文档

- [Java Keytool 文档](https://docs.oracle.com/en/java/javase/21/docs/specs/man/keytool.html)
- [Spring Boot SSL 配置](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.webserver.configure-ssl)
- [mkcert - 本地 HTTPS 证书工具](https://github.com/FiloSottile/mkcert)

---

## ✅ 检查清单

- [x] 前端证书导入到 Java 信任库
- [x] CORS 配置正确
- [x] Cookie sameSite 设置正确
- [x] axios withCredentials 启用
- [x] 后端 SSL 配置正确
- [x] 前端 HTTPS 配置正确
- [ ] 测试完整登录流程
- [ ] 测试跨标签页同步
- [ ] 测试退出登录

---

## 🎯 下一步

1. **重启后端服务**

   ```bash
   cd backend/springProject
   ./mvnw spring-boot:run
   ```

2. **重启前端服务**

   ```bash
   cd frontend-apps/homepage
   pnpm run dev
   ```

3. **测试登录流程**

   - 注册账号
   - 登录
   - 检查 Cookie
   - 测试 API 调用
   - 退出登录

4. **检查浏览器控制台**
   - 是否有 CORS 错误
   - 是否有证书警告
   - 是否有网络错误

现在网络问题应该解决了!🎉
