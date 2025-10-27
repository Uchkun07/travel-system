# 旅游推荐系统 (Travel System)

## 📖 项目介绍

该项目是一款功能丰富的旅游推荐系统,采用前后端分离架构开发:

- **前端**: Vue 3 + TypeScript + Element Plus + Pinia
- **后端**: Spring Boot 3.5.7 + MyBatis-Plus + Redis + JWT
- **数据库**: MySQL 8.0+
- **算法支持**:
  - Fun-rec 模型：用于景点推荐
  - 模拟退火算法：用于最优路线规划
  - LSTM 模型：用于景点人流量预测

系统基于用户偏好和历史数据,智能提供个性化旅行路线和景点推荐。

## 🏗️ 软件架构

### 技术栈

**前端**

- Vue 3.5.22 + TypeScript 5.7.3
- Element Plus 2.11.5 (UI 组件库)
- Pinia 3.0.3 (状态管理)
- Vue Router 4 (路由管理)
- Axios 1.12.2 (HTTP 客户端)
- Vite 6.0.11 (构建工具)

**后端**

- Spring Boot 3.5.7
- Spring Security (安全框架)
- MyBatis-Plus 3.5.10.1 (数据访问)
- Redis (缓存、Session 管理)
- JWT (用户认证)
- QQ 邮箱 SMTP (邮件服务)
- Maven (项目管理)

**数据库**

- MySQL 8.0+ (主数据库)
- Redis (缓存数据库)

### 系统架构层次

```
┌─────────────────────────────────────────┐
│          前端层 (Vue 3)                 │
│  ┌─────────────────────────────────┐   │
│  │ 用户界面 │ 路由管理 │ 状态管理 │   │
│  └─────────────────────────────────┘   │
└─────────────────────────────────────────┘
                    ↓ HTTPS/HTTP
┌─────────────────────────────────────────┐
│       服务层 (Spring Boot)              │
│  ┌─────────────────────────────────┐   │
│  │ 用户服务 │ 认证服务 │ 邮件服务 │   │
│  └─────────────────────────────────┘   │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│          算法层 (Python)                │
│  ┌─────────────────────────────────┐   │
│  │ PDA-GNN │ 模拟退火 │ LSTM预测  │   │
│  └─────────────────────────────────┘   │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│          数据层                          │
│  ┌──────────────┐  ┌──────────────┐   │
│  │    MySQL     │  │    Redis     │   │
│  └──────────────┘  └──────────────┘   │
└─────────────────────────────────────────┘
```

## 📋 环境要求

| 软件    | 版本要求 | 说明                |
| ------- | -------- | ------------------- |
| Java    | 21+      | JDK 环境            |
| Maven   | 3.9+     | Java 项目管理       |
| Node.js | 18.0+    | JavaScript 运行环境 |
| pnpm    | 8.0+     | Node 包管理器       |
| MySQL   | 8.0+     | 主数据库            |
| Redis   | 7.0+     | 缓存数据库          |
| Python  | 3.8+     | 算法模型运行环境    |

## 🚀 快速开始

### 1. 克隆项目

```bash
git clone https://github.com/Uchkun07/travel-system.git
cd travel-system
```

### 2. 数据库配置

#### 2.1 安装并启动 MySQL

1. 确保 MySQL 服务正在运行
2. 创建数据库:

```sql
# 登录 MySQL
mysql -u root -p

# 创建数据库
CREATE DATABASE travel_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 切换到数据库
USE travel_system;
```

#### 2.2 导入数据表

```bash
# 导入用户表
mysql -u root -p travel_system < backend/sql/user.sql

# 导入景点表
mysql -u root -p travel_system < backend/sql/attraction.sql
```

或者在 MySQL 命令行中:

```sql
USE travel_system;
SOURCE backend/sql/user.sql;
SOURCE backend/sql/attraction.sql;
```

#### 2.3 启动 Redis

**Windows:**

```powershell
# 启动 Redis 服务
redis-server

# 或使用 Windows 服务
net start Redis
```

**Linux/Mac:**

```bash
# 启动 Redis
redis-server

# 后台运行
redis-server --daemonize yes
```

### 3. 后端配置与启动

#### 3.1 配置后端环境变量

> ⚠️ **重要提示**: 本项目使用环境变量管理敏感配置,不再将密码等信息直接写入配置文件。

**方式一: 使用一键启动脚本 (推荐)**

项目提供了 PowerShell 脚本,可以一键配置环境变量并启动:

```powershell
# 在项目根目录运行
.\quick-start.ps1
```

该脚本会自动:

- ✅ 配置所有必需的环境变量
- ✅ 检查 MySQL 和 Redis 服务状态
- ✅ 进入后端项目目录
- ✅ 启动 Spring Boot 应用

> 📧 **获取完整配置**: 如需获取完整的环境变量配置,请联系项目负责人:
>
> - GitHub: @Uchkun07
> - Email: SuperMartian0413@outlook.com

详细的环境变量配置说明请参考: [docs/ENVIRONMENT_VARIABLES.md](docs/ENVIRONMENT_VARIABLES.md)

#### 3.2 启动后端服务

**使用脚本启动 (推荐):**

# 在 backend/springProject 目录

cd backend\springProject
.\start.ps1

```
**启动成功标志:**

```

. \_**\_ \_ ** \_ \_
/\\ / **_'_ ** \_ _(_)_ \_\_ \_\_ _ \ \ \ \
( ( )\_** | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/ \_**)| |_)| | | | | || (_| | ) ) ) )
' |\_**\_| .**|_| |_|_| |_\__, | / / / /
=========|_|==============|_\_\_/=/_/_/_/

Started SpringProjectApplication in 3.2 seconds

````

**后端服务地址:**

- HTTPS: `https://localhost:8080/api`
- HTTP: 不可用 (仅支持 HTTPS)

### 4. 前端配置与启动

#### 4.1 安装根目录依赖

```bash
# 在项目根目录
pnpm install
````

#### 4.2 启动主页前端

```bash
# 进入主页前端目录
cd frontend-apps/homepage

# 安装依赖
pnpm install

# 启动开发服务器
pnpm run dev
```

**访问地址:**

- 开发环境: `https://localhost:5173`

#### 4.3 启动后台前端 (可选)

```bash
# 进入仪表盘目录
cd frontend-apps/dashboard

# 安装依赖
pnpm install

# 启动开发服务器
pnpm run dev
```

### 5. Python 算法服务 (可选)

```bash
# 进入 Python 项目目录
cd backend/pythonProject

# 安装依赖
pip install -r requirements.txt

# 启动算法服务
python main.py
```

## 📖 使用说明

### 系统访问地址

| 服务     | 地址                       | 说明            |
| -------- | -------------------------- | --------------- |
| 前端主页 | https://localhost:5173     | 用户界面        |
| 后端 API | https://localhost:8080/api | RESTful API     |
| 仪表盘   | https://localhost:5174     | 管理后台 (可选) |

### 核心功能使用

#### 1. 用户注册与登录

**注册流程:**

1. 访问主页,点击"注册"按钮
2. 填写用户名、邮箱、密码
3. 点击"获取验证码",系统会发送 6 位验证码到邮箱
4. 输入验证码并完成注册
5. 系统自动分配默认头像

**登录方式:**

- 支持用户名登录
- 支持邮箱登录
- 登录失败保护: 最多允许 5 次失败尝试

#### 2. 景点推荐 (待实现)

#### 3. 路线规划 (待实现)

#### 4. 人流量预测 (待实现)

### API 接口文档

#### 认证相关

## 🔧 开发指南

### 项目结构

```
travel-system/
├── backend/
│   ├── pythonProject/          # Python 算法服务
│   ├── springProject/          # Spring Boot 后端
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/
│   │   │   │   │   └── org/example/springproject/
│   │   │   │   │       ├── config/      # 配置类
│   │   │   │   │       ├── controller/  # 控制器
│   │   │   │   │       ├── entity/      # 实体类
│   │   │   │   │       ├── mapper/      # MyBatis Mapper
│   │   │   │   │       ├── service/     # 业务逻辑
│   │   │   │   │       └── util/        # 工具类
│   │   │   │   └── resources/
│   │   │   │       ├── application.yml  # 配置文件
│   │   │   │       └── keystore.p12     # SSL证书
│   │   │   └── test/                    # 测试代码
│   │   ├── pom.xml                      # Maven配置
│   │   └── start.ps1                    # 启动脚本
│   └── sql/                             # 数据库脚本
├── frontend-apps/
│   ├── homepage/                        # 主页前端
│   │   ├── src/
│   │   │   ├── apis/         # API封装
│   │   │   ├── assets/       # 静态资源
│   │   │   ├── components/   # Vue组件
│   │   │   ├── routers/      # 路由配置
│   │   │   ├── stores/       # Pinia状态管理
│   │   │   ├── styles/       # 样式文件
│   │   │   └── views/        # 页面视图
│   │   └── package.json
│   └── dashboard/                       # 仪表盘前端
├── docs/                                # 文档
│   └── ENVIRONMENT_VARIABLES.md         # 环境变量配置说明
├── quick-start.ps1                      # 一键启动脚本
├── set-actual-env.ps1                   # 环境变量配置脚本
└── README.md
```

### 技术要点

**后端安全特性:**

- ✅ JWT Token 认证 (7 天有效期)
- ✅ 密码加密存储 (PBKDF2WithHmacSHA256, 100,000 次迭代)
- ✅ 邮箱验证码 (5 分钟有效期, SecureRandom 生成)
- ✅ Token 黑名单 (Redis 管理)
- ✅ 登录失败限制 (最多 5 次)
- ✅ HTTPS 强制加密
- ✅ Spring Security 集成

**前端安全特性:**

- ✅ Token 存储在 Cookie (HttpOnly 级别)
- ✅ 路由守卫保护
- ✅ 请求拦截器自动添加 Token
- ✅ 双击提交防护
- ✅ 敏感信息脱敏显示

### 常见问题

**Q: 启动时提示环境变量未设置?**

A: 请确保已运行环境变量配置脚本或在 IDE 中配置了所有必需的环境变量。详见 [docs/ENVIRONMENT_VARIABLES.md](docs/ENVIRONMENT_VARIABLES.md)

**Q: 邮件发送失败?**

A:

1. 确认 `MAIL_PASSWORD` 使用的是 QQ 邮箱 SMTP 授权码,不是邮箱密码
2. 检查 QQ 邮箱是否开启了 SMTP 服务
3. 授权码获取: QQ 邮箱设置 → 账户 → POP3/IMAP/SMTP 服务 → 生成授权码

**Q: 前端无法访问后端 API?**

A:

1. 检查后端是否正常启动 (https://localhost:8080/api)
2. 确认 SSL 证书已配置
3. 浏览器可能提示证书不受信任,点击"高级"→"继续访问"即可

**Q: Redis 连接失败?**

A:

1. 确认 Redis 服务已启动: `redis-cli ping` (应返回 PONG)
2. Windows 用户检查 Redis 服务: `net start Redis`
3. 检查端口 6379 是否被占用

**Q: MySQL 连接失败?**

A:

1. 确认 MySQL 服务已启动
2. 检查数据库名称是否为 `travel_system`
3. 确认环境变量 `DB_PASSWORD` 设置正确

## 🤝 参与贡献

我们欢迎任何形式的贡献!

### 贡献流程

1. **Fork 本仓库**
2. **创建功能分支**
   ```bash
   git checkout -b feature/AmazingFeature
   # 或修复bug
   git checkout -b fix/SomeBug
   ```
3. **提交更改**
   ```bash
   git commit -m 'Add some AmazingFeature'
   ```
4. **推送到分支**
   ```bash
   git push origin feature/AmazingFeature
   ```
5. **创建 Pull Request**

### 代码规范

- 遵循项目现有的代码风格
- 添加必要的注释
- 确保代码通过所有测试
- 更新相关文档

### 提交信息规范

使用语义化的提交信息:

```
feat: 添加新功能
fix: 修复bug
docs: 文档更新
style: 代码格式调整
refactor: 重构代码
test: 测试相关
chore: 构建/工具链更新
```

## 📄 许可证

本项目采用 [MIT 许可证](LICENSE) 进行许可。

Copyright (c) 2025 Uchkun07

## 📧 联系方式

- **项目负责人**: Uchkun07
- **Email**: SuperMartian0413@outlook.com
- **GitHub Issues**: [提交问题](https://github.com/Uchkun07/travel-system/issues)

## ⭐ Star History

如果这个项目对你有帮助,请给它一个 Star ⭐

---

**Built with ❤️ by Uchkun07**
