# 环境变量配置指南

## 概述

为了提高安全性,本项目将敏感配置信息从 `application.yml` 移到了环境变量中。这样可以避免在版本控制中泄露密钥和密码。

## 配置步骤

### 1. 创建 .env 文件

复制 `.env.example` 文件并重命名为 `.env`:

```bash
cp .env.example .env
```

### 2. 填写环境变量

编辑 `.env` 文件,填入真实的配置信息:

> **⚠️ 安全提示**  
> 以下配置信息包含敏感数据,实际值已被隐藏。  
> 如需获取真实的配置信息,请联系项目负责人:
>
> - **负责人**: Uchkun07
> - **Email**: SuperMartian@outlook.com

```properties
# 数据库配置
DB_USERNAME=root
DB_PASSWORD=your_database_password

# Redis 配置
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=

# SSL 配置
SSL_KEYSTORE_PASSWORD=your_keystore_password

# 邮箱配置
MAIL_HOST=smtp.qq.com
MAIL_PORT=587
MAIL_USERNAME=your_email@qq.com
MAIL_PASSWORD=your_qq_smtp_authorization_code

# JWT 配置
JWT_SECRET=your_jwt_secret_key_at_least_256_bits
```

### 3. 在 IDE 中配置环境变量

#### IntelliJ IDEA:

1. 打开 `Run/Debug Configurations`
2. 选择 `SpringProjectApplication`
3. 在 `Environment variables` 中添加所有环境变量
4. 格式: `KEY1=value1;KEY2=value2`

#### VS Code:

在 `launch.json` 中添加:

> **⚠️ 安全提示**  
> 以下配置信息包含敏感数据,实际值已被隐藏。  
> 如需获取真实的配置信息,请联系项目负责人:
>
> - **负责人**: Uchkun07
> - **Email**: SuperMartian@outlook.com

```json
{
  "configurations": [
    {
      "type": "java",
      "name": "SpringProjectApplication",
      "request": "launch",
      "mainClass": "org.example.springproject.SpringProjectApplication",
      "env": {
        "DB_USERNAME": "root",
        "DB_PASSWORD": "your_database_password",
        "REDIS_HOST": "localhost",
        "REDIS_PORT": "6379",
        "REDIS_PASSWORD": "",
        "SSL_KEYSTORE_PASSWORD": "your_keystore_password",
        "MAIL_HOST": "smtp.qq.com",
        "MAIL_PORT": "587",
        "MAIL_USERNAME": "your_email@qq.com",
        "MAIL_PASSWORD": "your_qq_smtp_authorization_code",
        "JWT_SECRET": "your_jwt_secret_key_at_least_256_bits"
      }
    }
  ]
}
```

### 4. 使用 Spring Boot 的默认值

`application.yml` 中使用了默认值语法 `${ENV_VAR:default_value}`:

- 如果环境变量存在,使用环境变量的值
- 如果环境变量不存在,使用默认值

带默认值的配置项:

- `DB_USERNAME` - 默认: `root`
- `REDIS_HOST` - 默认: `localhost`
- `REDIS_PORT` - 默认: `6379`
- `REDIS_PASSWORD` - 默认: 空字符串
- `SSL_KEYSTORE_PASSWORD` - 默认: `123456`
- `MAIL_HOST` - 默认: `smtp.qq.com`
- `MAIL_PORT` - 默认: `587`
- `JWT_SECRET` - 默认: 预设密钥(生产环境必须修改!)

必须设置的环境变量(无默认值):

- `DB_PASSWORD`
- `MAIL_USERNAME`
- `MAIL_PASSWORD`

## 生产环境配置

### Docker

在 `docker-compose.yml` 中:

```yaml
services:
  backend:
    environment:
      - DB_PASSWORD=${DB_PASSWORD}
      - MAIL_USERNAME=${MAIL_USERNAME}
      - MAIL_PASSWORD=${MAIL_PASSWORD}
      - JWT_SECRET=${JWT_SECRET}
```

### 系统环境变量

Linux/Mac:

```bash
export DB_PASSWORD=your_password
export MAIL_USERNAME=your_email@qq.com
export MAIL_PASSWORD=your_smtp_code
export JWT_SECRET=your_jwt_secret
```

Windows PowerShell:

```powershell
$env:DB_PASSWORD="your_password"
$env:MAIL_USERNAME="your_email@qq.com"
$env:MAIL_PASSWORD="your_smtp_code"
$env:JWT_SECRET="your_jwt_secret"
```

## 安全提示

⚠️ **重要**:

1. 永远不要将 `.env` 文件提交到 Git 仓库
2. 确保 `.env` 已添加到 `.gitignore`
3. 生产环境必须使用强密码和随机生成的 JWT 密钥
4. 定期更换密钥和密码
5. 使用 secrets management 服务(如 AWS Secrets Manager, Azure Key Vault)

## 环境变量说明

> **📧 获取配置信息**  
> 实际的配置值已被隐藏以保护安全。如需获取真实配置,请联系:
>
> - **项目负责人**: Uchkun07
> - **Email**: SuperMartian@outlook.com

| 变量名                | 说明            | 示例值                          | 必填             |
| --------------------- | --------------- | ------------------------------- | ---------------- |
| DB_USERNAME           | 数据库用户名    | root                            | 否(默认 root)    |
| DB_PASSWORD           | 数据库密码      | your_database_password          | 是               |
| REDIS_HOST            | Redis 主机地址  | localhost                       | 否               |
| REDIS_PORT            | Redis 端口      | 6379                            | 否               |
| REDIS_PASSWORD        | Redis 密码      | -                               | 否               |
| SSL_KEYSTORE_PASSWORD | SSL 密钥库密码  | your_keystore_password          | 否               |
| MAIL_HOST             | SMTP 服务器地址 | smtp.qq.com                     | 否               |
| MAIL_PORT             | SMTP 端口       | 587                             | 否               |
| MAIL_USERNAME         | 发件邮箱        | your_email@qq.com               | 是               |
| MAIL_PASSWORD         | SMTP 授权码     | your_qq_smtp_authorization_code | 是               |
| JWT_SECRET            | JWT 签名密钥    | 至少 256 位                     | 否(强烈建议设置) |

## 故障排查

### 应用无法启动

- 检查必填的环境变量是否已设置
- 查看 IDE 的环境变量配置是否正确
- 确认环境变量值中没有多余的空格或引号

### 邮件发送失败

- 确认 `MAIL_USERNAME` 和 `MAIL_PASSWORD` 正确
- `MAIL_PASSWORD` 应该是 QQ 邮箱的 SMTP 授权码,不是邮箱密码
- 检查 QQ 邮箱是否开启了 SMTP 服务

### 数据库连接失败

- 确认 `DB_PASSWORD` 与实际数据库密码一致
- 检查数据库服务是否正在运行
