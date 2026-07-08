# Docker 环境搭建指南

> 本文档说明如何使用 Docker Compose 快速启动项目所需的基础设施服务。

---

## 前提条件

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) 已安装并运行
- Docker Compose v2.x（Docker Desktop 自带）

## 快速启动

### 1. 首次配置

```bash
cd docker

# 复制环境变量模板（如需修改密码，编辑 .env 文件）
cp .env.example .env
```

### 2. 启动所有服务

```bash
docker-compose up -d
```

首次启动会下载镜像，可能需要几分钟。启动后等待所有容器健康检查通过：

```bash
docker-compose ps
```

所有容器状态应为 `running`，健康状态应为 `healthy`。

### 3. 验证服务

| 服务 | 验证方式 |
|------|---------|
| **MySQL** | `mysql -h 127.0.0.1 -P 3306 -u root -p123456 -e "SHOW DATABASES;"` |
| **Redis** | `redis-cli -h 127.0.0.1 -p 6380 -a 123456 PING`（返回 PONG） |
| **RabbitMQ** | 浏览器访问 http://localhost:15673（guest/guest） |
| **Nacos** | 浏览器访问 http://localhost:18848/nacos（nacos/nacos） |

### 4. 运行健康检查脚本

```bash
# Windows (Git Bash)
bash docker/healthcheck.sh

# Linux / macOS
chmod +x docker/healthcheck.sh
./docker/healthcheck.sh
```

## 服务说明

### MySQL 8.0

- **端口：** 3306
- **用户名：** root
- **密码：** 123456（可在 .env 中修改）
- **数据库：**
  - `neusoft_elderly_care` — 业务数据库（含完整种子数据）
  - `nacos_config` — Nacos 注册中心数据库
- **初始化：** 首次启动时自动执行 `docker/mysql/init/` 下的 SQL 脚本
- **数据持久化：** Docker volume `elderlycare-mysql-data`

### Redis 7.x

- **端口：** 6380（因其他项目占用 6379，映射到 6380）
- **密码：** 123456（可在 .env 中修改）
- **说明：** 当前项目未使用 Redis，为后续扩展预留
- **数据持久化：** Docker volume `elderlycare-redis-data`

### RabbitMQ 3.13

- **AMQP 端口：** 5673（因本地 RabbitMQ 占用 5672，映射到 5673）
- **管理界面端口：** 15673
- **用户名：** guest
- **密码：** guest（可在 .env 中修改）
- **预配置队列：**
  - `elderlycare.queue.checkin` — 入住事件
  - `elderlycare.queue.checkout` — 退住事件
  - `elderlycare.queue.service.purchase` — 服务购买事件
  - `elderlycare.queue.ai.analysis` — AI 分析事件
  - `elderlycare.queue.ai.notification` — AI 通知事件
- **数据持久化：** Docker volume `elderlycare-rabbitmq-data`

### Nacos 2.3.2

- **HTTP 端口：** 18848（因 Windows 端口保留范围，映射到 18848）
- **gRPC 端口：** 19848, 19849
- **用户名：** nacos
- **密码：** nacos（可在 .env 中修改）
- **运行模式：** standalone（单机模式）
- **存储：** 外部 MySQL（连接上面的 MySQL 容器）
- **日志持久化：** Docker volume `elderlycare-nacos-logs`

## 常用命令

```bash
# 启动所有服务
docker-compose up -d

# 查看容器状态
docker-compose ps

# 查看日志
docker-compose logs -f              # 所有服务
docker-compose logs -f mysql        # 指定服务

# 停止所有服务（保留数据）
docker-compose down

# 停止所有服务并删除数据卷（⚠️ 数据会丢失）
docker-compose down -v

# 重建某个服务
docker-compose up -d --force-recreate mysql

# 进入容器
docker exec -it elderlycare-mysql bash
docker exec -it elderlycare-redis sh
```

## 端口冲突处理

如果本地已运行 MySQL、Redis、RabbitMQ 或 Nacos，需要先停止本地服务：

```bash
# Windows — 停止本地 MySQL
net stop MySQL80

# Windows — 停止本地 RabbitMQ
net stop RabbitMQ

# Linux
sudo systemctl stop mysql redis rabbitmq-server
```

或者修改 `docker-compose.yml` 中的端口映射（如将 MySQL 改为 `3307:3306`）。

## 与本地开发模式切换

### 使用 Docker 基础设施（推荐）

1. 停止本地中间件服务
2. `cd docker && docker-compose up -d`
3. 启动后端微服务（自动连接 Docker 中的中间件）
4. 启动前端 `npm run dev`

### 恢复本地中间件

1. `cd docker && docker-compose down`
2. 启动本地 MySQL、Redis、RabbitMQ、Nacos
3. 启动后端微服务

**无需修改任何配置文件** — 所有中间件连接使用环境变量 + localhost 默认值，与 Docker 端口映射兼容。

## 故障排查

### MySQL 容器启动失败

```bash
# 查看日志
docker-compose logs mysql

# 常见原因：端口被占用
netstat -ano | findstr :3306
```

### Nacos 连接 MySQL 失败

```bash
# 确认 MySQL 健康检查通过
docker-compose ps

# Nacos 依赖 MySQL，会等待 MySQL 就绪后再启动
# 如果 MySQL 启动慢，Nacos 可能需要手动重启
docker-compose restart nacos
```

### RabbitMQ 管理界面无法访问

```bash
# 等待 30 秒后重试（管理插件需要时间加载）
docker-compose logs rabbitmq
```
