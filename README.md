# 旅游推荐系统 (Travel System)

## 介绍
该项目是一款功能丰富的旅游推荐系统，基于PDA-GNN模型根据用户行为数据集为用户提供高精度的旅游景点推荐，利用模拟退火算法进行路线规划，并通过LSTM实现景点人流量预测。系统基于用户偏好和历史数据，智能提供个性化旅行路线和景点推荐，助力开发者构建高效的旅游应用。


## 软件架构
- **前端层**：用户界面展示、交互操作
- **服务层**：推荐服务、路线规划服务、预测服务
- **算法层**：
  - PDA-GNN模型：用于景点推荐
  - 模拟退火算法：用于最优路线规划
  - LSTM模型：用于景点人流量预测
- **数据层**：用户行为数据、景点信息数据、历史流量数据


## 安装教程

### 环境要求
- Python 3.8+
- pip 21.0+
- Node.js 16.0+
- pnpm 8.0+
- MySQL 8.0+
- Java 21


### 安装步骤

### 根目录配置
1. **安装模组包**
   ```bash
   pnpm install
   ```
### python项目配置
1. **用pycharm或者vscode打开项目**
2. **安装模组包**
  ```bash
  pip install ...
  ```
3.**启动项目** 

### 后端项目启动
1. **克隆仓库**
   ```bash
   git clone https://github.com/Uchkun07/travel-system.git
   cd Travel-System
   ```

2. **创建数据库并导入数据**
   ```bash
   # 登录MySQL
   mysql -u root -p
   
   # 创建数据库
   CREATE DATABASE travel_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   
   # 退出MySQL命令行
   exit
   
   # 导入SQL数据
   导入backend/sql里面的sql文件
   ```

3. **配置数据库连接**
   ```bash
   # 编辑配置文件
   打开backend/src/main/resources/application.yml
   
   # 修改数据库密码（找到以下部分并更新密码）
   spring:
     datasource:
       username: root
       password: 你的数据库密码  # 修改为你的MySQL root密码
       url: jdbc:mysql://localhost:3306/travel_system?useUnicode=true&characterEncoding=utf-8&serverTimezone=UTC
   ```

4. **启动后端服务**
   ```bash
   # 进入后端目录
   cd backend
   
   # 使用Maven启动项目
   mvn spring-boot:run
   

### 前端项目启动

1. **安装依赖**
   ```bash
   # 回到项目根目录
   cd ..
   
   # 进入前端目录
   cd frontend/
   
   # 安装npm依赖
   pnpm install
   ```

2. **启动前端服务**
   ```bash
   # 开发环境启动
   pnpm run dev
   ```

## 使用说明

### 基本使用流程

1. **访问系统**
   - 后端API默认地址：http://localhost:5000
   - 前端界面默认地址：http://localhost:8080

2. **用户操作**
   - 注册/登录账号
   - 设置旅行偏好（如景点类型、预算、旅行天数等）
   - 浏览推荐景点列表

3. **景点推荐**
   ```python
   import requests
   
   response = requests.get(
       "http://localhost:5000/api/recommend",
       params={"user_id": "123", "num_recommendations": 10}
   )
   print(response.json())
   ```

4. **路线规划**
   - 选择感兴趣的景点
   - 点击"生成路线"按钮
   - 系统会基于模拟退火算法生成最优旅行路线
   - 可调整路线参数（如交通方式、时间限制等）

5. **人流量预测**
   - 查看特定景点的人流量预测图表
   - 选择日期范围，获取未来人流量趋势预测
   - 根据预测结果调整旅行计划

6. **数据导出**
   - 支持导出推荐结果为CSV格式
   - 支持导出旅行路线为PDF格式


## 参与贡献

1. Fork 本仓库
2. 新建 Feat_xxx 分支（feature分支）或 Fix_xxx 分支（修复bug）
3. 提交代码（确保代码风格一致，添加必要注释）
4. 运行测试确保无错误
5. 新建 Pull Request 并描述修改内容


## 许可证
本项目采用 [MIT许可证](LICENSE) 进行许可。详情请参阅 LICENSE 文件。


## 联系方式
如有问题或建议，请联系：SuperMartian0413@outlook.com
