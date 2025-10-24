# 登录注册 API 文档

### Redis Key 存储规范

#### Key 格式说明

| Key 格式                 | 用途           | 过期时间   | 值类型            |
| ------------------------ | -------------- | ---------- | ----------------- |
| `email_code:{邮箱}`      | 存储验证码     | 10 分钟    | String (6 位数字) |
| `ip_minute:{IP}`         | 分钟级 IP 限制 | 1 分钟     | String (计数)     |
| `ip_hour:{IP}`           | 小时级 IP 限制 | 1 小时     | String (计数)     |
| `ip_day:{IP}`            | 天级 IP 限制   | 24 小时    | String (计数)     |
| `email_limit:{邮箱}`     | 邮箱请求限制   | 1 小时     | String (计数)     |
| `email_verified:{邮箱}`  | 验证成功标记   | 30 分钟    | String ("1")      |
| `token_blacklist:{令牌}` | 登出令牌黑名单 | 与令牌同步 | String ("1")      |

### 限制阈值

- **IP 限制**:

  - 1 分钟内最多 1 次
  - 1 小时内最多 5 次
  - 1 天内最多 20 次

- **邮箱限制**:
  - 同一邮箱 1 小时内最多 3 次

## API 接口

### 1. 发送验证码

**接口**: `POST /api/email/sendCode`

**请求体**:

```json
{
  "email": "user@example.com"
}
```

**成功响应**:

```json
{
  "success": true,
  "message": "验证码已发送,请查收邮件"
}
```

**失败响应**:

```json
{
  "success": false,
  "message": "发送过于频繁,请1分钟后再试"
}
```

**错误类型**:

- 邮箱格式不正确
- 发送过于频繁,请 1 分钟后再试
- 发送次数过多,请 1 小时后再试
- 今日发送次数已达上限,请明天再试
- 该邮箱请求过于频繁,请 1 小时后再试

---

### 2. 用户注册

**接口**: `POST /api/auth/register`

**请求体**:

```json
{
  "username": "testuser",
  "password": "password123",
  "confirmPassword": "password123",
  "email": "user@example.com",
  "captcha": "123456"
}
```

**成功响应**:

```json
{
  "success": true,
  "message": "注册成功",
  "userId": 1,
  "username": "testuser"
}
```

**失败响应**:

```json
{
  "success": false,
  "message": "用户名已存在"
}
```

**错误类型**:

- 用户名不能为空
- 密码不能为空
- 邮箱不能为空
- 验证码不能为空
- 两次输入的密码不一致
- 验证码错误或已过期
- 用户名已存在
- 该邮箱已被注册

---

### 3. 用户登录

**接口**: `POST /api/auth/login`

**请求体**:

```json
{
  "username": "testuser",
  "password": "password123",
  "rememberMe": true
}
```

**说明**:

- `username` 字段支持用户名或邮箱登录
- `rememberMe` 为 `true` 时令牌有效期 7 天,否则为 1 天

**成功响应**:

```json
{
  "success": true,
  "message": "登录成功",
  "userId": 1,
  "username": "testuser",
  "email": "user@example.com",
  "avatar": null,
  "fullName": null,
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**令牌格式**: JWT (JSON Web Token)

- Header: `Authorization: Bearer {token}`
- Payload 包含: `userId`, `username`, `iat`(签发时间), `exp`(过期时间)

**失败响应**:

```json
{
  "success": false,
  "message": "用户名或密码错误"
}
```

**错误类型**:

- 用户名不能为空
- 密码不能为空
- 用户名或密码错误
- 账号已被禁用,请联系管理员

---

### 4. 检查用户名

**接口**: `GET /api/auth/checkUsername?username=testuser`

**成功响应**:

```json
{
  "success": true,
  "available": false,
  "message": "用户名已存在"
}
```

---

### 5. 检查邮箱

**接口**: `GET /api/auth/checkEmail?email=user@example.com`

**成功响应**:

```json
{
  "success": true,
  "available": true,
  "message": "邮箱可用"
}
```

---

### 6. 用户登出

**接口**: `POST /api/auth/logout`

**请求头**:

```
Authorization: Bearer {token}
```

**成功响应**:

```json
{
  "success": true,
  "message": "登出成功"
}
```

**说明**: 登出后令牌会被加入黑名单,直到令牌过期

---

### 7. 获取当前用户信息

**接口**: `GET /api/auth/userInfo`

**请求头**:

```
Authorization: Bearer {token}
```

**成功响应**:

```json
{
  "success": true,
  "userId": 1,
  "username": "testuser",
  "email": "user@example.com",
  "fullName": "张三",
  "avatar": "https://example.com/avatar.jpg",
  "status": 1
}
```

**失败响应**:

```json
{
  "success": false,
  "message": "未提供认证令牌"
}
```

## 前端集成示例

### 发送验证码示例 (Vue 3)

```vue
<script setup>
import { ref } from "vue";
import { ElMessage } from "element-plus";

const email = ref("");
const countdown = ref(0);

const sendCode = async () => {
  if (countdown.value > 0) return;

  try {
    const response = await fetch("https://localhost:8080/api/email/sendCode", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ email: email.value }),
    });

    const data = await response.json();

    if (data.success) {
      ElMessage.success(data.message);
      startCountdown();
    } else {
      ElMessage.error(data.message);
    }
  } catch (error) {
    ElMessage.error("发送失败,请检查网络连接");
  }
};

const startCountdown = () => {
  countdown.value = 60;
  const timer = setInterval(() => {
    countdown.value--;
    if (countdown.value <= 0) {
      clearInterval(timer);
    }
  }, 1000);
};
</script>
```

### 注册示例

```javascript
const register = async (formData) => {
  try {
    const response = await fetch("https://localhost:8080/api/auth/register", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(formData),
    });

    const data = await response.json();

    if (data.success) {
      ElMessage.success(data.message);
      // 跳转到登录页或自动登录
    } else {
      ElMessage.error(data.message);
    }
  } catch (error) {
    ElMessage.error("注册失败,请检查网络连接");
  }
};
```

### 登录示例

```javascript
const login = async (formData) => {
  try {
    const response = await fetch("https://localhost:8080/api/auth/login", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(formData),
    });

    const data = await response.json();

    if (data.success) {
      // 保存token到localStorage
      localStorage.setItem("token", data.token);
      localStorage.setItem(
        "userInfo",
        JSON.stringify({
          userId: data.userId,
          username: data.username,
          email: data.email,
          avatar: data.avatar,
          fullName: data.fullName,
        })
      );

      ElMessage.success(data.message);
      // 跳转到首页
    } else {
      ElMessage.error(data.message);
    }
  } catch (error) {
    ElMessage.error("登录失败,请检查网络连接");
  }
};
```

### 带 Token 的请求示例

```javascript
// 获取用户信息
const getUserInfo = async () => {
  const token = localStorage.getItem("token");

  try {
    const response = await fetch("https://localhost:8080/api/auth/userInfo", {
      method: "GET",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
    });

    const data = await response.json();

    if (data.success) {
      console.log("用户信息:", data);
    } else {
      ElMessage.error(data.message);
      // 如果token无效,跳转到登录页
      if (data.message.includes("令牌")) {
        localStorage.removeItem("token");
        router.push("/login");
      }
    }
  } catch (error) {
    ElMessage.error("获取用户信息失败");
  }
};

// 登出
const logout = async () => {
  const token = localStorage.getItem("token");

  try {
    const response = await fetch("https://localhost:8080/api/auth/logout", {
      method: "POST",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
    });

    const data = await response.json();

    if (data.success) {
      localStorage.removeItem("token");
      localStorage.removeItem("userInfo");
      ElMessage.success("登出成功");
      router.push("/login");
    }
  } catch (error) {
    // 即使请求失败也清除本地token
    localStorage.removeItem("token");
    localStorage.removeItem("userInfo");
    router.push("/login");
  }
};
```

### Axios 拦截器配置

```javascript
import axios from "axios";
import { ElMessage } from "element-plus";
import router from "@/routers";

const api = axios.create({
  baseURL: "https://localhost:8080/api",
  timeout: 10000,
});

// 请求拦截器 - 自动添加token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器 - 处理token过期
api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    if (error.response) {
      if (error.response.status === 401) {
        ElMessage.error("登录已过期,请重新登录");
        localStorage.removeItem("token");
        localStorage.removeItem("userInfo");
        router.push("/login");
      } else {
        ElMessage.error(error.response.data.message || "请求失败");
      }
    } else {
      ElMessage.error("网络错误,请检查连接");
    }
    return Promise.reject(error);
  }
);

export default api;
```

## 安全说明

1. **密码加密**: 使用 PBKDF2WithHmacSHA256 算法,迭代 100,000 次
2. **随机盐值**: 每个用户使用独立的 32 字节随机盐值
3. **JWT 令牌**:
   - 使用 HS256 算法签名
   - 记住我: 7 天有效期
   - 不记住: 1 天有效期
   - 登出后加入黑名单
4. **验证码**: 6 位数字,10 分钟有效期
5. **频率限制**:
   - IP 级别:分钟/小时/天三级限制
   - 邮箱级别:小时级限制
6. **HTTPS**: 生产环境必须使用 HTTPS
7. **跨域**: 已配置 CORS,生产环境需要限制具体域名
8. **令牌传输**: 使用 `Authorization: Bearer {token}` 头部

## 测试

### 测试账号创建流程

1. 访问注册页面
2. 输入用户名、邮箱、密码
3. 点击"获取验证码"
4. 查收邮件,输入验证码
5. 点击"注册"按钮
6. 注册成功后可以使用用户名或邮箱登录

### API 测试工具

推荐使用:

- Postman
- Swagger UI: https://localhost:8080/swagger-ui/index.html
- curl 命令行

## 注意事项

1. 所有时间字段使用服务器时间
2. 验证码发送后 10 分钟内有效
3. 验证成功后标记保留 30 分钟
4. IP 限制基于真实客户端 IP(支持代理)
5. 邮箱格式验证使用正则表达式
6. 密码强度建议前端额外验证


## 错误码说明

| HTTP状态码 | 说明 | 处理方式 |
| ---------- | ---------------------- | ---------------------- |
| 200 | 请求成功 | 正常处理 |
| 401 | 未授权(令牌无效/过期) | 跳转到登录页 |
| 500 | 服务器内部错误 | 提示用户稍后重试 |

## 公开接口列表

以下接口无需携带令牌即可访问:
- `POST /api/auth/register` - 注册
- `POST /api/auth/login` - 登录
- `GET /api/auth/checkUsername` - 检查用户名
- `GET /api/auth/checkEmail` - 检查邮箱
- `POST /api/email/sendCode` - 发送验证码

## 需要认证的接口

以下接口必须在请求头中携带有效令牌:
- `POST /api/auth/logout` - 登出
- `GET /api/auth/userInfo` - 获取用户信息
- 其他所有业务接口(除公开接口外)
