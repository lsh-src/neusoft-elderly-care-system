# 东软颐养中心管理系统

> 养老机构运营管理平台 — Spring Cloud 微服务 + Vue 3 配置驱动前端

---

## 目录

- [项目简介](#项目简介)
- [技术栈](#技术栈)
- [项目结构](#项目结构)
- [后端架构](#后端架构)
- [前端架构](#前端架构)
- [安全与权限设计](#安全与权限设计)
- [AI 智能服务](#ai-智能服务)
- [数据库设计](#数据库设计)
- [快速启动](#快速启动)
- [演示账号](#演示账号)
- [API 文档](#api-文档)
- [如何扩展新模块](#如何扩展新模块)

---

## 项目简介

东软颐养中心管理系统是面向养老机构的一站式运营管理平台，涵盖入住管理、床位管理、膳食管理、护理管理、服务管理、统计仪表盘、AI 智能服务等核心业务模块。

**核心特性：**

- 🔧 **配置驱动前端** — 13+ CRUD 模块共用一个 `CrudPage.vue`，新增模块无需写 Vue 文件
- 🏗️ **微服务架构** — 12 个 Spring Boot 模块 + Nacos 注册中心 + Spring Cloud Gateway 统一路由
- 🤖 **AI RAG 集成** — 知识库检索增强生成（RAG）+ MiMo 大模型，支持智能问答、健康评估、护理推荐
- 🔐 **三层权限控制** — URL 级 SecurityConfig + 方法级 @PreAuthorize + 前端路由守卫与菜单过滤
- 📊 **实时数据仪表盘** — ECharts 可视化，聚合各模块统计数据

---

## 技术栈

| 层级 | 技术 | 版本 | 说明 |
|------|------|------|------|
| **前端框架** | Vue 3 + Vite | 3.5 / 6.0 | 组合式 API + 极速 HMR |
| **UI 组件库** | Element Plus | 2.9 | 企业级组件，中文生态完善 |
| **状态管理** | Pinia | 2.3 | Vue 3 官方推荐 |
| **路由** | Vue Router | 4.5 | 角色权限守卫 |
| **HTTP 客户端** | Axios | 1.7 | JWT 拦截器 + 统一错误处理 |
| **图表** | ECharts | 5.6 | 仪表盘数据可视化 |
| **后端框架** | Spring Boot | 3.3.5 | Java 17 |
| **微服务** | Spring Cloud + Nacos | 4.1.5 / 2023.0.1.2 | 服务注册发现 + 网关路由 |
| **ORM** | MyBatis-Plus | 3.5.7 | 单表零 SQL，XML 联表查询 |
| **安全** | Spring Security + JWT | 6.x / 0.12.6 | 无状态认证 |
| **消息队列** | RabbitMQ | — | AI 事件异步消费 |
| **数据库** | MySQL | 8.0 | InnoDB，utf8mb4 |
| **文件存储** | 阿里云 OSS | — | 图片上传 CDN 加速 |
| **AI 模型** | MiMo LLM | — | RAG 检索增强 + 智能问答 |

---

## 项目结构

```
neusoftelderlycare1/
├── backend/                              # Spring Cloud 多模块 Maven 项目
│   ├── pom.xml                           # 父 POM（依赖版本管理 + 12 个子模块声明）
│   ├── elderlycare-common/               # 🧱 公共基础层：实体、BaseEntity、ApiResponse、
│   │                                     #    BaseCrudController、SecurityConfig、JWT、
│   │                                     #    Feign 客户端、RabbitMQ 配置、工具类
│   ├── elderlycare-gateway/              # 🚪 Spring Cloud Gateway：统一路由入口
│   ├── elderlycare-auth/                 # 🔐 认证鉴权：登录注册、JWT 签发
│   ├── elderlycare-user/                 # 👤 用户管理：sys_user CRUD
│   ├── elderlycare-customer/             # 👥 客户管理：客户 CRUD + OSS 文件上传
│   ├── elderlycare-bed/                  # 🛏️ 床位管理：床位 CRUD、分配、释放
│   ├── elderlycare-checkin/              # 📋 出入登记：入住/退住/外出登记
│   ├── elderlycare-meal/                 # 🍽️ 膳食管理：膳食计划、日历视图
│   ├── elderlycare-service/              # ⭐ 服务管理：服务项目、购买、服务关系
│   ├── elderlycare-nursing/              # 💊 护理管理：护理级别、内容、记录、区域
│   ├── elderlycare-dashboard/            # 📊 统计仪表盘：Feign 聚合各模块数据
│   └── elderlycare-ai/                   # 🤖 AI 智能服务：RAG 引擎 + LLM 集成
│
├── frontend/                             # Vue 3 单页应用
│   ├── src/
│   │   ├── views/
│   │   │   ├── CrudPage.vue              # ⭐ 通用 CRUD 页面组件（所有业务模块共用）
│   │   │   ├── moduleConfig.js           # ⭐ 模块字段配置表
│   │   │   ├── Layout.vue                # 侧边栏 + 顶栏布局
│   │   │   ├── Dashboard.vue             # ECharts 运营仪表盘
│   │   │   ├── MealCalendar.vue          # 膳食日历视图
│   │   │   ├── Profile.vue               # 个人信息
│   │   │   ├── Login.vue / Register.vue  # 登录注册
│   │   │   └── ai/                       # AI 页面
│   │   │       ├── AiChat.vue            # AI 对话
│   │   │       ├── RagQuery.vue          # RAG 检索问答
│   │   │       ├── KnowledgeBase.vue     # 知识库管理
│   │   │       ├── HealthAnalysis.vue    # 健康评估
│   │   │       └── CareRecommendation.vue # 护理方案推荐
│   │   ├── router/index.js               # 路由 + 菜单配置 + 权限守卫
│   │   ├── api/crud.js                   # 通用 CRUD API 工厂
│   │   ├── stores/user.js                # Pinia 用户状态管理
│   │   ├── utils/request.js              # Axios 实例 + JWT 拦截器
│   │   └── styles/theme.css              # 全局主题变量
│   ├── vite.config.js                    # Vite 配置 + API 代理
│   └── package.json
│
├── database/
│   └── neusoft_elderly_care.sql          # 建表 SQL + 初始数据
├── docker/                               # Docker 基础设施编排
│   ├── docker-compose.yml                # MySQL + Redis + RabbitMQ + Nacos
│   ├── .env.example                      # 环境变量模板
│   ├── mysql/                            # MySQL 配置和初始化脚本
│   ├── redis/                            # Redis 配置
│   ├── rabbitmq/                         # RabbitMQ 配置和队列定义
│   └── healthcheck.sh                    # 健康检查脚本
├── docs/                                 # 项目文档
│   ├── REVIEW_REPORT.md                  # 现状评估报告
│   └── DOCKER_SETUP.md                   # Docker 搭建指南
├── postman/
│   └── collection.json                   # API 测试集合
└── README.md
```

---

## 后端架构

### 微服务拓扑

```
                         ┌─────────────────┐
                         │   前端 (Vite)    │
                         │  localhost:5173  │
                         └────────┬────────┘
                                  │ /api/**
                                  ▼
                         ┌─────────────────┐
                         │  Gateway :8080   │  ← Spring Cloud Gateway
                         │  统一路由入口     │     StripPrefix=1
                         └────────┬────────┘
                                  │
              ┌───────────────────┼───────────────────┐
              │                   │                   │
              ▼                   ▼                   ▼
     ┌──────────────┐   ┌──────────────┐   ┌──────────────┐
     │ Auth :8081   │   │ User         │   │ Customer     │
     │ 登录注册     │   │ 用户管理     │   │ 客户管理     │
     └──────────────┘   └──────────────┘   └──────────────┘
              │                   │                   │
              ▼                   ▼                   ▼
     ┌──────────────┐   ┌──────────────┐   ┌──────────────┐
     │ Bed          │   │ CheckIn      │   │ Meal         │
     │ 床位管理     │   │ 出入登记     │   │ 膳食管理     │
     └──────────────┘   └──────────────┘   └──────────────┘
              │                   │                   │
              ▼                   ▼                   ▼
     ┌──────────────┐   ┌──────────────┐   ┌──────────────┐
     │ Service      │   │ Nursing      │   │ Dashboard    │
     │ 服务管理     │   │ 护理管理     │   │ 统计聚合     │
     └──────────────┘   └──────────────┘   └──────────────┘
              │
              ▼
     ┌──────────────┐        ┌─────────────┐
     │ AI :8090     │◄──────►│  Nacos      │  ← 服务注册发现
     │ 智能服务     │        │  注册中心   │
     └──────────────┘        └─────────────┘
              │
              ▼
     ┌──────────────┐
     │  RabbitMQ    │  ← 业务事件异步消费
     └──────────────┘
```

### Gateway 路由映射

| 前端路径 | 后端服务 | 说明 |
|----------|----------|------|
| `/api/auth/**` | elderlycare-auth | 登录、注册 |
| `/api/users/**` | elderlycare-user | 用户管理 |
| `/api/customers/**` | elderlycare-customer | 客户管理 |
| `/api/beds/**` | elderlycare-bed | 床位管理 |
| `/api/check-ins/**` | elderlycare-checkin | 入住/退住/外出登记 |
| `/api/meals/**` | elderlycare-meal | 膳食管理 |
| `/api/nursing-*/**` | elderlycare-nursing | 护理级别/内容/记录/区域 |
| `/api/care-services/**` | elderlycare-service | 服务管理 |
| `/api/dashboard/**` | elderlycare-dashboard | 统计仪表盘 |
| `/api/ai/**` | elderlycare-ai | AI 智能服务 |
| `/api/upload/**` | elderlycare-customer | 文件上传 |

### 模块依赖关系

```
                        ┌─────────────┐
                        │   common    │  ← 实体、BaseEntity、ApiResponse、
                        │  (基础层)    │    BaseCrudController、SecurityConfig、
                        └──────┬──────┘    JWT、Feign 客户端、RabbitMQ 配置
                               │
                        ┌──────▼──────┐
                        │    auth     │  ← JWT 签发、登录注册、UserDetails
                        │  (认证层)    │
                        └──────┬──────┘
                               │
          ┌────────────────────┼────────────────────┐
          │                    │                    │
    ┌─────▼─────┐      ┌──────▼──────┐      ┌─────▼─────┐
    │   user    │      │  customer   │      │    bed    │
    │  用户管理  │      │  客户管理    │      │  床位管理  │
    └───────────┘      └──────┬──────┘      └─────┬─────┘
                              │                    │
                       ┌──────▼────────────────────▼──────┐
                       │           checkin                │
                       │  入住 / 退住 / 外出登记           │
                       │  (依赖 customer + bed 更新状态)   │
                       └──────────────────────────────────┘

    ┌───────────┐  ┌───────────┐  ┌───────────┐
    │   meal    │  │  service  │  │  nursing  │
    │  膳食管理  │  │  服务管理  │  │  护理管理  │
    └───────────┘  └───────────┘  └───────────┘
          │              │              │
          └──────────────┼──────────────┘
                         │
                  ┌──────▼──────┐
                  │  dashboard  │  ← Feign 聚合各模块统计数据
                  │  统计仪表盘  │
                  └─────────────┘

                  ┌─────────────┐
                  │     ai      │  ← RAG 引擎 + MiMo LLM
                  │  AI 智能服务 │    RabbitMQ 事件消费
                  └─────────────┘
```

### 每个模块内部结构

```
elderlycare-{module}/
└── src/main/java/com/neusoft/elderlycare/
    ├── controller/           # REST 接口（继承 BaseCrudController）
    ├── service/              # 业务逻辑接口
    │   └── impl/             # 业务逻辑实现
    ├── mapper/               # MyBatis Mapper 接口
    └── resources/mapper/     # Mapper XML（联表查询 SQL）
```

> **注意**：实体类（Entity）统一放在 `elderlycare-common`，因为多个模块需要共享。`BaseCrudController` 提供标准 CRUD 接口（page / detail / create / update / delete），业务 Controller 只需 Override 需要定制的方法。

---

## 前端架构

### 配置驱动的通用 CRUD 组件

系统有 13+ 个 CRUD 模块，全部共用一个 `CrudPage.vue`，通过 `moduleConfig.js` 的字段配置区分：

```
moduleConfig.js                    CrudPage.vue
┌─────────────────┐    props     ┌──────────────────────────┐
│ fieldMap        │ ──────────→  │ <el-table>               │
│ dropdownSources │              │   动态列 from fieldMap    │
│ searchHints     │              │ </el-table>              │
│ getExtraColumns │              │                          │
└─────────────────┘              │ <el-dialog>              │
                                 │   动态表单 from fieldMap  │
                                 │   下拉框 from dropdownSrc │
                                 │ </el-dialog>             │
                                 │                          │
                                 │ <el-drawer>              │
                                 │   动态详情 from fieldMap  │
                                 │ </el-drawer>             │
                                 └──────────────────────────┘
```

### 路由与菜单

路由和菜单统一由 `router/index.js` 的 `menuGroups` 配置驱动：

```javascript
export const menuGroups = [
  {
    groupName: '入住管理',
    icon: 'House',
    children: [
      { path: 'customers', title: '客户管理', resource: 'customers', icon: 'Avatar',
        roles: ['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_NURSE', 'ROLE_USER'] },
      { path: 'beds', title: '床位管理', resource: 'beds', icon: 'House',
        roles: ['ROLE_ADMIN', 'ROLE_MANAGER'] },
    ]
  },
  // ... 更多分组
]
```

- `roles` 控制菜单可见性
- `resource` 映射到 `moduleConfig.js` 的字段配置
- 路由守卫根据角色自动拦截无权限访问

---

## 安全与权限设计

### 认证流程

```
用户输入手机号 + 密码 → POST /api/auth/login
        │
        ▼
AuthController.login()
  ├── SysUserMapper 按手机号查用户
  ├── BCrypt.matches(明文密码, 数据库哈希) 校验密码
  ├── JwtTokenProvider.generateToken(手机号, 角色) 生成 JWT
  └── 返回 { token, user }

后续所有请求：
  请求头 Authorization: Bearer <JWT>
        │
        ▼
JwtAuthenticationFilter.doFilterInternal()
  ├── 从 Header 提取 Bearer token
  ├── JwtTokenProvider.validateToken(token) 验证签名和过期
  ├── 从 token 解析出手机号 + 角色
  ├── UserDetailsServiceImpl.loadUserByUsername(手机号) 加载用户
  └── 设置 SecurityContext.authentication
```

### 角色权限矩阵

| 模块 | ADMIN 管理员 | MANAGER 健康管家 | NURSE 护士 | USER 入住老人 |
|------|:---:|:---:|:---:|:---:|
| 运营仪表盘 | ✅ | ✅ | ✅ | ❌ 不显示 |
| 客户管理 | ✅ 全部 | ✅ 全部 | ✅ 全部 | ✅ 只读 |
| 床位管理 | ✅ | ✅ | ✅ 只读 | ❌ |
| 入住登记 | ✅ | ✅ | ✅ 只读 | ❌ |
| 退住登记 | ✅ | ✅ | ❌ | ❌ |
| 外出登记 | ✅ | ✅ | ✅ | ✅ |
| 膳食管理 | ✅ | ✅ | ✅ | ✅ |
| 服务对象 | ✅ | ✅ | ❌ | ❌ |
| 服务关注 | ✅ | ✅ | ❌ | ✅ |
| 服务购买 | ✅ | ✅ | ✅ 只读 | ✅ |
| 护理级别 | ✅ | ✅ | ✅ 只读 | ✅ 只读 |
| 护理内容 | ✅ | ✅ | ✅ 只读 | ✅ 只读 |
| 护理记录 | ✅ | ✅ | ✅ | ✅ 只读 |
| 负责区域 | ✅ | ✅ | ❌ | ❌ |
| 用户管理 | ✅ | ❌ | ❌ | ❌ |
| AI 对话 | ✅ | ✅ | ✅ | ✅ |
| RAG 检索 | ✅ | ✅ | ❌ | ✅ |
| 知识库管理 | ✅ | ✅ | ❌ | ❌ |
| 健康评估 | ✅ | ✅ | ❌ | ❌ |
| 护理推荐 | ✅ | ✅ | ❌ | ❌ |

### 三层权限控制

1. **URL 级别** — `SecurityConfig` 中 `.requestMatchers("/users/**").hasRole("ADMIN")`
2. **方法级别** — Controller 类/方法上 `@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")`
3. **前端级别** — 路由守卫拦截 + 菜单按角色过滤显示

### Feign 调用链路中的权限传递

仪表盘通过 Feign 调用下游服务获取统计数据。`FeignAuthInterceptor` 自动将当前请求的 JWT token 转发给下游服务，确保跨服务调用的权限一致性。

---

## AI 智能服务

### RAG 检索增强生成

```
用户提问 → 知识库检索 → Top-K 相关文档 → 注入系统提示词 → LLM 生成回答
```

**流程：**

1. `KnowledgeBaseService` 将文档分块（500 字符，50 字符重叠）
2. 调用 Embedding API 生成向量，存入 `SimpleVectorStore`（基于 ConcurrentHashMap）
3. 用户查询时，`RagService` 将问题向量化 → 余弦相似度检索 Top-K 文档
4. 将检索结果注入系统提示词，调用 MiMo LLM 生成回答
5. 知识库为空时降级为直接 LLM 问答

### AI 功能模块

| 功能 | 说明 |
|------|------|
| AI 对话 | 基于 MiMo 大模型的智能对话 |
| RAG 检索问答 | 基于知识库的精准问答 |
| 知识库管理 | 文档上传、分块、向量化 |
| 健康评估 | 基于客户数据的 AI 健康分析 |
| 护理方案推荐 | 基于客户状况的个性化护理建议 |

### 事件驱动

AI 模块通过 RabbitMQ 消费业务事件（如新客户入住、护理记录更新），实现异步处理和智能分析。

---

## 数据库设计

### 通用字段（BaseEntity）

所有业务表共享以下字段：

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT AUTO_INCREMENT | 主键 |
| `create_time` | DATETIME | 创建时间（自动填充） |
| `update_time` | DATETIME | 更新时间（自动填充） |
| `deleted` | TINYINT DEFAULT 0 | 逻辑删除（0=正常, 1=已删除） |

### 数据表

| 表名 | 实体 | 说明 |
|------|------|------|
| `sys_user` | SysUser | 系统用户（管理员、健康管家、护士、入住老人） |
| `customer` | Customer | 入住客户（老人） |
| `bed` | Bed | 床位（房间号 + 床位号 + 状态） |
| `check_in` | CheckIn | 入住登记（关联客户 + 床位） |
| `check_out` | CheckOut | 退住登记 |
| `outing` | Outing | 外出登记 |
| `meal` | Meal | 膳食记录（早/午/晚 + 图片） |
| `care_service` | CareService | 服务项目（价格、内容、周期） |
| `service_relation` | ServiceRelation | 服务关系（客户 ↔ 健康管家） |
| `service_purchase` | ServicePurchase | 服务购买记录 |
| `nursing_level` | NursingLevel | 护理级别（名称、费用） |
| `nursing_item` | NursingItem | 护理项目（名称、频率、所属级别） |
| `nursing_record` | NursingRecord | 护理记录（执行人、时间、内容、结果） |
| `nurse_area` | NurseArea | 负责区域 |

### 联表查询

部分实体通过 `@TableField(exist = false)` 声明非数据库字段，Mapper XML 使用 LEFT JOIN 赋值：

```xml
<select id="selectBedPage" resultType="Bed">
    SELECT b.*, c.name AS customer_name
    FROM bed b
    LEFT JOIN customer c ON b.customer_id = c.id AND c.deleted = 0
    WHERE b.deleted = 0
</select>
```

---

## 快速启动

### 环境要求

| 依赖 | 版本 |
|------|------|
| Java | 17+ |
| Maven | 3.8+ |
| Node.js | 18+ |
| Docker Desktop | 4.x+（推荐） |
| MySQL | 8.0 |
| Nacos | 2.x |
| RabbitMQ | 3.x |

### 方式一：Docker Compose 启动基础设施（推荐）

使用 Docker 一键启动 MySQL、Redis、RabbitMQ、Nacos，无需手动安装配置。

```bash
# 1. 配置环境变量
cd docker
cp .env.example .env      # 如需修改密码，编辑 .env 文件

# 2. 启动所有基础设施
docker-compose up -d

# 3. 等待容器健康检查通过
docker-compose ps         # 所有容器应为 healthy

# 4. 运行健康检查脚本
bash healthcheck.sh
```

> **注意：**
> - 启动前需先停掉本地的 MySQL 服务，避免端口 3306 冲突
> - Docker 端口映射使用默认端口：MySQL 3306 / Redis 6379 / RabbitMQ 5672+15672 / Nacos 8848

详细说明见 [`docs/DOCKER_SETUP.md`](docs/DOCKER_SETUP.md)。

### 方式二：手动启动基础设施

如果不使用 Docker，需手动安装和配置各中间件：

```bash
# 启动 Nacos 注册中心
# 默认地址: http://127.0.0.1:8848
# 用户名/密码: nacos/nacos

# 启动 RabbitMQ
# 默认地址: http://localhost:15672
# 用户名/密码: guest/guest

# 初始化数据库
mysql -u root -p -e "CREATE DATABASE neusoft_elderly_care CHARACTER SET utf8mb4;"
mysql -u root -p neusoft_elderly_care < database/neusoft_elderly_care.sql
```

数据库连接配置在 `backend/elderlycare-common/src/main/resources/application-common.yml`。

### 启动后端

```bash
cd backend

# 构建所有模块
mvn clean package -DskipTests

# 依次启动各服务（或在 IDEA 中运行各 Application 类）
java -jar elderlycare-gateway/target/elderlycare-gateway-1.0.0.jar       # :8080
java -jar elderlycare-auth/target/elderlycare-auth-1.0.0.jar             # :8081
java -jar elderlycare-ai/target/elderlycare-ai-1.0.0.jar                 # :8090
# 其余模块通过 Nacos 注册，按需启动
```

### 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端访问：`http://localhost:5173`

---

## 演示账号

系统启动时自动创建以下演示账号，密码均为 `123456`：

| 角色 | 手机号 | 权限范围 |
|------|--------|----------|
| 管理员 | 13800000000 | 全部功能 + 用户管理 |
| 健康管家 | 13800000001 | 业务管理（无用户管理） |
| 护士 | 13900000100 | 护理相关 + 部分只读 |
| 入住老人 | 13800000002 | 查看个人信息、膳食、外出、服务 |

---

## API 文档

后端集成了 Knife4j（OpenAPI 3），启动后端后访问：

```
http://localhost:8080/doc.html
```

也可使用 `postman/collection.json` 导入 Postman 进行接口测试。

---

## 如何扩展新模块

以新增「健康档案」模块为例：

### 第 1 步：后端 — 创建模块

```bash
# 1. 创建 Maven 子模块 elderlycare-health
# 2. 继承 BaseCrudController，自动获得标准 CRUD 接口
@RestController
@RequestMapping("/health-records")
@PreAuthorize("hasAnyRole('ADMIN','MANAGER','NURSE')")
public class HealthRecordController extends BaseCrudController<HealthRecord> {
    // 自动获得 /page, /{id}, POST, PUT, DELETE
}
```

### 第 2 步：前端 — 加字段配置

```javascript
// moduleConfig.js
export const fieldMap = {
  'health-records': [
    ['customerId', '客户', 'select', true],
    ['bloodPressure', '血压', 'text'],
    ['heartRate', '心率', 'number'],
    ['recordDate', '记录日期', 'date', true],
    ['remark', '备注', 'textarea'],
  ],
}
```

### 第 3 步：前端 — 加菜单项

```javascript
// router/index.js → menuGroups
{
  groupName: '健康管理',
  icon: 'FirstAidKit',
  children: [
    { path: 'health-records', title: '健康档案', resource: 'health-records',
      icon: 'Document', roles: ['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_NURSE'] },
  ]
}
```

**不需要写任何新的 `.vue` 文件。** `CrudPage.vue` 会根据配置自动渲染表格、表单、详情。

---

## 项目亮点

| 特性 | 实现方式 |
|------|----------|
| **零重复页面** | 13+ CRUD 模块共用一个 `CrudPage.vue`，配置驱动 |
| **零 SQL 开发** | 单表操作全靠 MyBatis-Plus BaseMapper，联表才写 XML |
| **微服务架构** | Spring Cloud Gateway + Nacos + Feign，模块独立部署 |
| **AI RAG 集成** | 知识库向量检索 + MiMo LLM，支持智能问答和护理推荐 |
| **自动编号** | `NumberGenerator` 按前缀自动生成（如 RZ20260608-001） |
| **逻辑删除** | `@TableLogic` 注解全局生效，删除只是标记 `deleted=1` |
| **自动填充** | `createTime` / `updateTime` 由 MyBatis-Plus MetaObjectHandler 自动写入 |
| **三层权限** | URL 级 SecurityConfig + 方法级 @PreAuthorize + 前端路由守卫与菜单过滤 |
| **文件上传** | 阿里云 OSS 直传，返回 CDN URL |
| **优雅降级** | AI 服务不可用时前端自动降级为演示数据 |
