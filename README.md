# 旅游推荐系统 (Travel System)

## 项目简介

旅游推荐系统是一个前后端分离的全栈项目，包含用户端与管理端两套前端应用，以及 Spring Boot 后端服务。系统围绕景点内容管理、个性化推荐、路线规划、权限治理和运营看板构建完整业务闭环。

当前代码仓库为可运行版本，核心能力已实现并可联调。

## 当前技术栈

### 前端

- Vue 3 + TypeScript + Vite
- Element Plus
- Pinia
- Vue Router
- Axios
- ECharts（管理端数据看板）

### 后端

- Java 21
- Spring Boot 3.5.12
- Spring Security
- MyBatis-Plus 3.5.9
- Redis
- JWT
- Spring AOP / Validation / Mail
- springdoc-openapi

### 数据与脚本

- MySQL 8.x
- Redis 7.x
- Python 脚本（数据采集与图片抓取，位于 [backend/python](backend/python)）

## 功能概览（按项目现状）

### 用户端（homepage）

- 用户注册、登录、登出
- 景点列表/详情浏览
- 收藏管理
- 首页推荐与行为埋点
- 路线规划与结果展示
- 热门城市与轮播图展示
- 个人中心（资料与头像等）

### 管理端（dashboard）

- 管理员登录与退出
- 用户管理
- 景点/城市/类型/标签管理
- 轮播图管理
- 角色-权限（RBAC）管理
- 运营看板（趋势、分布、TOP 数据）
- 操作日志管理

### 后端能力

- REST API 统一返回结构
- JWT 认证 + 权限拦截
- Redis 缓存（热点缓存、空值缓存、TTL 抖动）
- 推荐接口异步化与超时控制
- 路线规划（模拟退火 + 高德路径能力 + 降级估算）
- MySQL 索引优化脚本（含字段存在探测）

## 仓库结构

```text
travel-system/
├── backend/
│   ├── python/                 # Python 数据采集脚本
│   ├── springProject/          # Spring Boot 后端
│   └── sql/                    # 数据库脚本（建表/初始化/索引优化）
├── frontend-apps/
│   ├── homepage/               # 用户端前端
│   └── dashboard/              # 管理端前端
├── docs/                       # 项目文档
├── package.json
└── README.md
```

## 环境要求

| 组件 | 建议版本 |
| --- | --- |
| Java | 21+ |
| Maven | 3.9+ |
| Node.js | 18+ |
| pnpm | 8+ |
| MySQL | 8.0+ |
| Redis | 7.0+ |
| Python | 3.8+ |

## 快速启动

### 1. 克隆项目

```bash
git clone https://github.com/Uchkun07/travel-system.git
cd travel-system
```

### 2. 初始化 MySQL 数据

推荐直接导入完整脚本：

```bash
mysql -u root -p travel_system < backend/sql/all.sql
```

也可使用主建表脚本：

```bash
mysql -u root -p travel_system < backend/sql/travel_system.sql
```

若需要应用性能索引优化脚本：

```bash
mysql -u root -p travel_system < backend/sql/performance_indexes.sql
```

### 3. 启动 Redis

```powershell
redis-server
```

### 4. 启动后端

```powershell
cd backend\springProject
.\mvnw.cmd spring-boot:run
```

默认地址：

- API 基础地址: `http://localhost:8080`
- OpenAPI 文档: `http://localhost:8080/swagger-ui/index.html`

### 5. 启动用户端前端

```powershell
cd frontend-apps\homepage
pnpm install
pnpm dev
```

默认地址：`http://localhost:5173`

### 6. 启动管理端前端

```powershell
cd frontend-apps\dashboard
pnpm install
pnpm dev
```

默认地址：`http://localhost:5174`

## 与旧文档的差异说明

- 根 README 已按当前目录与功能更新。
- Python 目录为 [backend/python](backend/python)，不是 `pythonProject`。
- SQL 脚本目录为 [backend/sql](backend/sql)，不是仓库根目录 `sql`。
- 推荐、路线规划、管理看板等能力当前均已实现，不再是“待实现”。

## 安全与配置说明

- 当前仓库中的部分历史配置文件包含本地开发示例值，部署前请替换为你自己的环境变量与密钥。
- 建议优先通过环境变量管理敏感配置，参考 [docs/ENVIRONMENT_VARIABLES.md](docs/ENVIRONMENT_VARIABLES.md)。

## 相关文档

- [docs/ENVIRONMENT_VARIABLES.md](docs/ENVIRONMENT_VARIABLES.md)
- [docs/backend-performance-optimization.md](docs/backend-performance-optimization.md)
- [docs/commitlint.md](docs/commitlint.md)
- [docs/毕业论文.md](docs/毕业论文.md)

## 开发协作

- 提交流程与分支规范参考 [docs/before-dev.md](docs/before-dev.md)
- Commit Message 规范参考 [commitlint.config.js](commitlint.config.js)

## 联系方式

- 项目维护者: Uchkun07
- Issues: https://github.com/Uchkun07/travel-system/issues
