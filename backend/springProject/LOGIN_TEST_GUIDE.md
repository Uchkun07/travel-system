# 登录功能说明与测试指南

## 问题描述

用户反馈只能使用邮箱登录,不支持用户名登录。

## 问题分析

### 可能的原因

1. **注册时使用了邮箱作为用户名**

   - 如果注册时 `username` 字段填写的是邮箱地址,那么用户名实际上就是邮箱
   - 例如: `username = "user@example.com"`

2. **数据库中没有独立的用户名**

   - 检查数据库 `user` 表中的 `username` 列
   - 确认是否存在非邮箱格式的用户名

3. **测试数据问题**
   - 可能测试时只创建了邮箱登录的账号

## 解决方案

### 已优化的登录逻辑

修改了 `UserServiceImpl.login()` 方法,增加智能识别功能:

```java
// 智能判断输入是用户名还是邮箱
if (loginInput.contains("@")) {
    // 包含@符号,优先当作邮箱查询
    user = getUserByEmail(loginInput);
    if (user == null) {
        // 邮箱查不到,再尝试用户名
        user = getUserByUsername(loginInput);
    }
} else {
    // 不包含@符号,优先当作用户名查询
    user = getUserByUsername(loginInput);
    if (user == null) {
        // 用户名查不到,再尝试邮箱
        user = getUserByEmail(loginInput);
    }
}
```

### 新增调试日志

添加了详细的日志输出,方便排查问题:

- 识别输入类型(用户名/邮箱)
- 查询过程
- 用户信息

## 测试步骤

### 1. 检查现有用户数据

```sql
-- 查看所有用户的用户名和邮箱
SELECT user_id, username, email, status FROM user;

-- 检查用户名是否为邮箱格式
SELECT
    user_id,
    username,
    email,
    CASE
        WHEN username LIKE '%@%' THEN '邮箱格式用户名'
        ELSE '普通用户名'
    END as username_type
FROM user;
```

### 2. 创建测试账号

#### 方式 1: 通过注册接口创建(推荐)

**注册一个用户名为 testuser 的账号:**

```bash
curl -X POST https://localhost:8080/api/email/sendCode \
  -H "Content-Type: application/json" \
  -d '{"email":"testuser@example.com"}'

# 等待邮件,获取验证码后:

curl -X POST https://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "testuser@example.com",
    "password": "Test123456",
    "confirmPassword": "Test123456",
    "captcha": "验证码"
  }'
```

#### 方式 2: 直接在数据库插入(仅用于测试)

```sql
-- 注意: 这里的password_hash是 "password123" 经过PBKDF2加密的结果
-- 在生产环境应该通过注册接口创建用户
INSERT INTO user (username, email, password_hash, salt, iterations, status, register_source)
VALUES (
    'testuser',
    'test@example.com',
    '生成的密码哈希',
    '生成的盐值',
    100000,
    1,
    'web'
);
```

### 3. 测试登录功能

#### 测试 1: 使用用户名登录

```bash
curl -X POST https://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "Test123456",
    "rememberMe": false
  }'
```

**预期结果:**

```json
{
  "success": true,
  "message": "登录成功",
  "userId": 1,
  "username": "testuser",
  "email": "testuser@example.com",
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

#### 测试 2: 使用邮箱登录

```bash
curl -X POST https://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser@example.com",
    "password": "Test123456",
    "rememberMe": false
  }'
```

**预期结果:** 同样应该登录成功

#### 测试 3: 检查日志

启动应用后查看日志输出:

```
DEBUG - 检测到用户名格式,优先按用户名查询: testuser
DEBUG - 找到用户,userId: 1, username: testuser, email: testuser@example.com
INFO  - 用户登录成功,用户名:testuser
```

## 常见问题

### Q1: 为什么之前只能用邮箱登录?

**A:** 可能的原因:

1. 注册时在"用户名"字段填入了邮箱地址
2. 数据库中 `username` 字段实际存储的是邮箱
3. 只测试了邮箱登录,没有测试用户名登录

### Q2: 如何验证用户名功能是否正常?

**A:** 按照以下步骤:

1. 查询数据库确认有非邮箱格式的用户名
2. 使用该用户名登录
3. 查看应用日志确认查询流程

### Q3: username 字段可以填邮箱吗?

**A:**

- **技术上可以**,但不推荐
- **推荐做法**: username 填写简短的用户名(如 "testuser"),email 单独存储
- **系统支持**: 即使 username 是邮箱格式,登录时也能正常工作

## 数据规范建议

### 推荐的用户数据格式

```
username: "testuser"      ✅ (简短、唯一、易记)
email: "test@example.com" ✅ (完整邮箱地址)

username: "test@example.com"  ⚠️ (可以工作但不推荐)
email: "test@example.com"     ⚠️ (与username重复)
```

### 注册表单建议

前端注册表单应该:

1. 明确区分"用户名"和"邮箱"两个输入框
2. 用户名:
   - 提示: "请输入用户名(字母、数字、下划线)"
   - 验证: 3-20 字符,不允许@符号
3. 邮箱:
   - 提示: "请输入邮箱地址"
   - 验证: 标准邮箱格式

## 启用调试日志

如果需要查看详细的登录过程,修改 `application.yml`:

```yaml
logging:
  level:
    org.example.springproject.service.impl.UserServiceImpl: DEBUG
```

## 验证清单

- [ ] 创建了至少一个用户名非邮箱格式的测试账号
- [ ] 测试用户名登录成功
- [ ] 测试邮箱登录成功
- [ ] 查看日志确认查询流程正确
- [ ] 确认数据库 username 字段存储规范

## 结论

经过优化后,系统**同时支持用户名和邮箱登录**,并且会智能判断输入类型。

如果仍然无法使用用户名登录,请:

1. 检查数据库中的 username 字段值
2. 确认密码是否正确
3. 查看应用日志排查问题
