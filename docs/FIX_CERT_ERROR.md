# 🔐 解决 ERR_CERT_AUTHORITY_INVALID 错误

## ✅ 已完成的操作

1. **安装 mkcert 工具** ✅
2. **安装根证书到系统** ✅
3. **安装根证书到 Java** ✅

```bash
✅ The local CA is already installed in the system trust store!
✅ The local CA is already installed in Java's trust store!
```

---

## 🚀 解决步骤

### 步骤 1: 手动信任后端证书 (必须!)

虽然系统已经信任了 mkcert 的根证书,但你需要在浏览器中手动访问后端一次:

1. **打开新标签页,访问:**

   ```
   https://localhost:8080/api/auth/checkUsername?username=test
   ```

2. **浏览器会显示警告:**

   - Chrome: "您的连接不是私密连接" (NET::ERR_CERT_AUTHORITY_INVALID)
   - Firefox: "警告: 潜在的安全风险"
   - Edge: "您的连接不是私密连接"

3. **点击 "高级" → "继续前往 localhost (不安全)"**

4. **看到 JSON 响应:**

   ```json
   {
     "success": true,
     "available": true,
     "message": "用户名可用"
   }
   ```

5. **回到前端页面 (https://localhost:5173) 刷新**

现在 API 调用就能正常工作了!

---

## 🔍 为什么需要这一步?

1. **mkcert 根证书已安装:** ✅

   - 系统信任 mkcert 签发的所有证书
   - Java 信任 mkcert 签发的所有证书

2. **但浏览器需要首次访问:**
   - Chrome/Edge 有额外的安全检查
   - 第一次访问自签名证书的域名时需要手动确认
   - 确认后会记住这个证书

---

## 🧪 测试方法

### 方法 1: 浏览器控制台测试

```javascript
// 1. 先手动访问并信任: https://localhost:8080/api/auth/checkUsername?username=test
// 2. 然后在控制台运行:

fetch("https://localhost:8080/api/auth/checkUsername?username=test")
  .then((res) => res.json())
  .then((data) => {
    console.log("✅ 成功!", data);
  })
  .catch((err) => {
    console.error("❌ 失败!", err);
  });
```

### 方法 2: 前端页面测试

1. 确保已手动访问并信任后端证书
2. 打开前端页面: https://localhost:5173
3. 点击"注册"按钮
4. 输入邮箱,点击"获取验证码"
5. F12 → Network → 检查请求状态

**预期结果:**

- ✅ 状态码: 200
- ✅ 没有 ERR_CERT_AUTHORITY_INVALID 错误
- ✅ 成功收到验证码提示

---

## 🐛 如果还有错误

### 错误 1: 仍然显示 ERR_CERT_AUTHORITY_INVALID

**原因:** 没有手动访问后端并信任证书

**解决:**

```
1. 新标签页打开: https://localhost:8080/api/auth/checkUsername?username=test
2. 点击 "高级" → "继续前往 localhost (不安全)"
3. 回到前端页面刷新
```

### 错误 2: NET::ERR_CONNECTION_REFUSED

**原因:** 后端没有启动

**解决:**

```bash
cd backend/springProject
./mvnw spring-boot:run
```

### 错误 3: CORS 错误

**原因:** CORS 配置问题 (但你的配置是正确的)

**检查:**

```java
// backend/springProject/src/main/java/.../config/CorsConfig.java
.allowedOriginPatterns("http://localhost:*", "https://localhost:*")
.allowCredentials(true)
```

---

## 📋 快速检查清单

- [x] mkcert 已安装
- [x] 根证书已安装到系统
- [x] 根证书已安装到 Java
- [ ] **手动访问后端并信任证书** ← 这一步必须做!
- [ ] 前端 API 调用正常
- [ ] Cookie 正常携带
- [ ] 注册/登录流程正常

---

## 💡 一键测试脚本

复制以下内容到浏览器控制台,一键测试所有功能:

```javascript
(async function testAll() {
  console.log("🧪 开始测试...\n");

  // 测试 1: 检查用户名
  try {
    const res1 = await fetch(
      "https://localhost:8080/api/auth/checkUsername?username=test"
    );
    const data1 = await res1.json();
    console.log("✅ 测试 1 - 检查用户名:", data1);
  } catch (err) {
    console.error("❌ 测试 1 失败:", err.message);
    console.log(
      "💡 请先访问: https://localhost:8080/api/auth/checkUsername?username=test"
    );
    return;
  }

  // 测试 2: 检查邮箱
  try {
    const res2 = await fetch(
      "https://localhost:8080/api/auth/checkEmail?email=test@example.com"
    );
    const data2 = await res2.json();
    console.log("✅ 测试 2 - 检查邮箱:", data2);
  } catch (err) {
    console.error("❌ 测试 2 失败:", err.message);
  }

  // 测试 3: 检查 Cookie
  console.log("\n📦 Cookie 状态:");
  console.log(
    "  - token:",
    document.cookie.includes("token") ? "✅ 存在" : "❌ 不存在"
  );

  // 测试 4: 检查 localStorage
  console.log("\n💾 localStorage 状态:");
  console.log(
    "  - userInfo:",
    localStorage.getItem("userInfo") ? "✅ 存在" : "❌ 不存在"
  );

  console.log("\n🎉 测试完成!");
})();
```

---

## 🎯 最终步骤

1. **访问后端:** https://localhost:8080/api/auth/checkUsername?username=test
2. **信任证书:** 点击 "高级" → "继续前往"
3. **回到前端:** https://localhost:5173
4. **测试功能:** 注册 → 登录 → 退出

完成!🎉
