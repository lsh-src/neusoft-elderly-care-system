# 现状评估报告

> 生成时间：2026-07-08
> 目标：为 Docker 容器化改造提供基础数据

---

## 1. 项目结构概览

```
neusoftelderlycare1/
├── backend/                          # 后端微服务（12 个 Maven 模块）
│   ├── elderlycare-common/           # 共享层：实体、BaseEntity、ApiResponse、JWT、Feign
│   ├── elderlycare-gateway/          # Spring Cloud Gateway (:8080)
│   ├── elderlycare-auth/             # 认证服务 (:8081)
│   ├── elderlycare-customer/         # 客户管理 (:8082)
│   ├── elderlycare-bed/              # 床位管理 (:8083)
│   ├── elderlycare-checkin/          # 入住管理 (:8084)
│   ├── elderlycare-meal/             # 膳食管理 (:8085)
│   ├── elderlycare-service/          # 服务管理 (:8086)
│   ├── elderlycare-nursing/          # 护理管理 (:8087)
│   ├── elderlycare-dashboard/        # 仪表盘 (:8088)
│   ├── elderlycare-user/             # 用户管理 (:8089)
│   └── elderlycare-ai/               # AI 服务 (:8090)
├── frontend/                         # Vue 3 前端 (:5173)
├── database/                         # 数据库脚本
│   └── neusoft_elderly_care.sql      # 完整 DDL + 种子数据
├── postman/                          # Postman 集合
└── docker/                           # [待创建] Docker 配置
```

## 2. 后端微服务端口分配

| 模块 | 端口 | 服务名 |
|------|------|--------|
| Gateway | 8080 | elderlycare-gateway |
| Auth | 8081 | elderlycare-auth |
| Customer | 8082 | elderlycare-customer |
| Bed | 8083 | elderlycare-bed |
| Checkin | 8084 | elderlycare-checkin |
| Meal | 8085 | elderlycare-meal |
| Service | 8086 | elderlycare-service |
| Nursing | 8087 | elderlycare-nursing |
| Dashboard | 8088 | elderlycare-dashboard |
| User | 8089 | elderlycare-user |
| AI | 8090 | elderlycare-ai |

## 3. 中间件连接配置清单

### 3.1 MySQL

| 配置项 | 当前值 | 环境变量 |
|--------|--------|----------|
| Host | localhost | `DB_URL` (含 host) |
| Port | 3306 | `DB_URL` (含 port) |
| Database | neusoft_elderly_care | `DB_URL` (含 db name) |
| Username | root | `DB_USERNAME` |
| Password | 123456 | `DB_PASSWORD` |
| Charset | utf8mb4 | — |
| Server Timezone | Asia/Shanghai | — |

**配置文件：** `backend/elderlycare-common/src/main/resources/application-common.yml:17-20`

### 3.2 Nacos

| 配置项 | 当前值 | 环境变量 |
|--------|--------|----------|
| Server Address | 127.0.0.1:8848 | `NACOS_ADDR` |
| Namespace | (空) | `NACOS_NAMESPACE` |
| Username | nacos | `NACOS_USERNAME` |
| Password | nacos | `NACOS_PASSWORD` |

**配置文件：** `backend/elderlycare-common/src/main/resources/application-common.yml:9-13`

### 3.3 RabbitMQ

| 配置项 | 当前值 | 环境变量 |
|--------|--------|----------|
| Host | localhost | `RABBITMQ_HOST` |
| Port | 5672 | `RABBITMQ_PORT` |
| Username | guest | `RABBITMQ_USERNAME` |
| Password | guest | `RABBITMQ_PASSWORD` |
| Virtual Host | / | — |

**配置文件：** `backend/elderlycare-common/src/main/resources/application-common.yml:28-35`

### 3.4 Redis

**⚠️ 项目当前未使用 Redis**。搜索整个代码库（排除 node_modules），未发现任何 Redis 相关配置或代码。

### 3.5 阿里云 OSS

| 配置项 | 环境变量 |
|--------|----------|
| Endpoint | `ALIYUN_OSS_ENDPOINT` |
| AccessKey ID | `ALIYUN_OSS_ACCESS_KEY_ID` |
| AccessKey Secret | `ALIYUN_OSS_ACCESS_KEY_SECRET` |
| Bucket Name | `ALIYUN_OSS_BUCKET_NAME` |
| Domain | `ALIYUN_OSS_DOMAIN` |

**注意：** 阿里云 OSS 为云服务，不纳入 Docker 容器化范围。

## 4. 硬编码 localhost 地址清单

| 文件 | 行号 | 内容 | 是否需要修改 |
|------|------|------|-------------|
| `application-common.yml` | 18 | `jdbc:mysql://localhost:3306/...` | ❌ 有环境变量默认值，Docker 端口映射后不变 |
| `application-common.yml` | 29 | `host: ${RABBITMQ_HOST:localhost}` | ❌ 有环境变量默认值 |
| `application-common.yml` | 10 | `server-addr: ${NACOS_ADDR:127.0.0.1:8848}` | ❌ 有环境变量默认值 |
| `elderlycare-gateway/application.yml` | 103 | CORS: `localhost:5173, localhost:8080` | ❌ 前端开发地址，无需修改 |
| `frontend/vite.config.js` | 10 | `target: 'http://localhost:8080'` | ❌ 代理到 Gateway，无需修改 |

**结论：** 所有中间件连接都已使用环境变量 + localhost 默认值的模式。Docker 端口映射到 localhost 后，**无需修改任何配置文件**。

## 5. 数据库现状

- **数据库名：** `neusoft_elderly_care`
- **字符集：** utf8mb4
- **引擎：** InnoDB
- **MySQL 版本：** 8.0.11
- **现有表（12 张）：**
  - `bed` — 床位管理
  - `care_service` — 服务关注
  - `check_in` — 入住登记
  - `check_out` — 退住登记
  - `customer` — 客户管理
  - `meal` — 膳食管理
  - `nurse_area` — 护理区域
  - `nursing_item` — 护理项目
  - `nursing_level` — 护理等级
  - `nursing_record` — 护理记录
  - `service_purchase` — 服务购买
  - `service_relation` — 服务关系
  - `sys_user` — 系统用户
  - `outing` — 外出登记
- **种子数据：** 已包含在 `database/neusoft_elderly_care.sql` 中
- **逻辑删除：** 所有表使用 `deleted` 字段（0=未删除，1=已删除）

## 6. 前端配置

- **开发服务器：** Vite :5173
- **API 代理：** `/api` → `http://localhost:8080`（Gateway）
- **认证方式：** JWT Bearer Token，存储在 localStorage
- **状态管理：** Pinia

## 7. Docker 容器化可行性评估

### ✅ 完全可行

| 评估项 | 状态 | 说明 |
|--------|------|------|
| 配置灵活性 | ✅ | 所有中间件连接已用环境变量，localhost 默认值与 Docker 端口映射兼容 |
| 数据持久化 | ✅ | MySQL 数据可通过 Docker volume 持久化 |
| 端口冲突 | ⚠️ | 需先停掉本地 MySQL(3306)、Nacos(8848)、RabbitMQ(5672) |
| 网络互通 | ✅ | Docker 内部网络 `project-net` 让容器互通 |
| 业务代码 | ✅ | 无需修改任何 Java/Vue 代码 |
| 扩展性 | ✅ | 预留 Seata 集成空间 |

### 关键发现

1. **Redis 未使用** — 项目不依赖 Redis，Docker 配置中可选择性包含（为未来扩展预留）
2. **配置零侵入** — 现有环境变量默认值完美适配 Docker 端口映射模式
3. **SQL 脚本完整** — `database/neusoft_elderly_care.sql` 包含完整 DDL + 种子数据，可直接用于 MySQL 容器初始化

## 8. 端口占用检查（待执行）

启动 Docker 前需确认以下端口未被本地服务占用：

| 端口 | 服务 | 检查命令 |
|------|------|---------|
| 3306 | MySQL | `netstat -ano \| findstr :3306` |
| 5672 | RabbitMQ | `netstat -ano \| findstr :5672` |
| 15672 | RabbitMQ Management | `netstat -ano \| findstr :15672` |
| 8848 | Nacos | `netstat -ano \| findstr :8848` |
| 9848 | Nacos gRPC | `netstat -ano \| findstr :9848` |
| 9849 | Nacos gRPC | `netstat -ano \| findstr :9849` |
