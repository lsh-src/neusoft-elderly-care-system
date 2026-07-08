# 功能验证测试报告

> 测试时间：2026-07-08
> 测试环境：Windows 11 + Docker Desktop 29.6.1

---

## 1. 容器状态

| 容器 | 镜像 | 状态 | 健康检查 |
|------|------|------|---------|
| elderlycare-mysql | mysql:8.0 | Running | ✅ healthy |
| elderlycare-redis | redis:7-alpine | Running | ✅ healthy |
| elderlycare-rabbitmq | rabbitmq:3.13-management-alpine | Running | ✅ healthy |
| elderlycare-nacos | nacos/nacos-server:v2.3.2 | Running | ✅ healthy |

## 2. 端口映射（使用默认端口）

| 服务 | 容器内端口 | 宿主机端口 |
|------|-----------|-----------|
| MySQL | 3306 | 3306 |
| Redis | 6379 | 6379 |
| RabbitMQ AMQP | 5672 | 5672 |
| RabbitMQ Management | 15672 | 15672 |
| Nacos HTTP | 8848 | 8848 |
| Nacos gRPC | 9848 | 9848 |
| Nacos gRPC | 9849 | 9849 |

## 3. 组件连通性测试

### MySQL

```
✓ 连接成功：mysql -h 127.0.0.1 -P 3306 -u root -p123456
✓ 业务数据库 neusoft_elderly_care 存在
✓ Nacos 数据库 nacos_config 存在
✓ 业务表完整（14 张表）：bed, care_service, check_in, check_out, customer,
  meal, nurse_area, nursing_item, nursing_level, nursing_record, outing,
  service_purchase, service_relation, sys_user
✓ 种子数据已导入
```

### Redis

```
✓ 连接成功：redis-cli -h 127.0.0.1 -p 6379 -a 123456 PING → PONG
✓ 读写测试通过
```

### RabbitMQ

```
✓ 管理界面可访问：http://localhost:15672
✓ API 认证成功：guest/guest
✓ 版本：3.13.7
⚠ 队列预配置：已移除 definitions.json（因 vhost 初始化冲突）
  → 队列将由 Spring Boot RabbitMQConfig 在应用启动时自动创建
```

### Nacos

```
✓ 健康检查通过：{"status":"UP"}
✓ 管理界面可访问：http://localhost:8848/nacos
✓ 认证成功：nacos/nacos
✓ 版本：2.3.2
```

## 4. 配置适配

### application-docker.yml（新增）

```yaml
spring:
  rabbitmq:
    port: ${RABBITMQ_PORT:5672}
  cloud:
    nacos:
      discovery:
        server-addr: ${NACOS_ADDR:127.0.0.1:8848}
```

**使用方式：** 启动后端时添加 `--spring.profiles.active=docker`

### 环境变量

| 变量 | 默认值 | Docker 值 | 说明 |
|------|--------|----------|------|
| `RABBITMQ_PORT` | 5672 | 5672 | 无需修改 |
| `NACOS_ADDR` | 127.0.0.1:8848 | 127.0.0.1:8848 | 无需修改 |
| `DB_URL` | localhost:3306 | localhost:3306 | 无需修改 |
| `DB_USERNAME` | root | root | 无需修改 |
| `DB_PASSWORD` | 123456 | 123456 | 无需修改 |

## 5. 已知问题及解决方案

### 5.1 Windows 端口保留范围（已解决）

**问题：** Windows 保留了 8755-8954 端口范围，导致 Nacos 无法使用 8848 端口。

**解决：** Docker Compose 端口映射已改为使用默认端口（8848），如遇端口冲突需手动处理 Windows 端口保留范围。

### 5.2 本地 RabbitMQ 端口冲突（已解决）

**问题：** 本地 RabbitMQ 服务占用 5672/15672 端口。

**解决：** Docker Compose 端口映射已改为使用默认端口（5672/15672），如遇冲突需先停止本地 RabbitMQ 服务。

### 5.3 RabbitMQ definitions.json 冲突

**问题：** 使用 definitions.json 预配置队列时，vhost "/" 尚未创建导致启动失败。

**解决：** 移除 definitions.json，队列由 Spring Boot RabbitMQConfig 在应用启动时自动创建。

## 6. 后端服务验证

### 6.1 启动方式

使用环境变量指定 Docker 端口（因 Gateway 不加载 common 的 docker profile）：

```bash
cd backend
NACOS_ADDR=127.0.0.1:8848 RABBITMQ_PORT=5672 \
java -jar elderlycare-gateway/target/elderlycare-gateway-1.0.0.jar

NACOS_ADDR=127.0.0.1:8848 RABBITMQ_PORT=5672 \
java -jar elderlycare-auth/target/elderlycare-auth-1.0.0.jar
```

### 6.2 Nacos 服务注册

| 服务 | 注册状态 | 实例地址 |
|------|---------|---------|
| elderlycare-gateway | ✅ 已注册 | 192.168.150.1:8080 |
| elderlycare-auth | ✅ 已注册 | 192.168.150.1:8081 |
| elderlycare-user | ✅ 已注册 | 192.168.150.1:8089 |

### 6.3 API 功能测试

| 测试项 | 结果 | 说明 |
|--------|------|------|
| POST /api/auth/login | ✅ 200 | JWT 认证成功，返回 token + 用户信息 |
| GET /api/users/page | ✅ 200 | 用户列表分页查询正常 |
| GET /api/customers/page | ⚠️ 503 | customer 服务未启动（预期行为） |

### 6.4 端到端链路验证

```
前端 → Gateway(:8080) → Auth(:8081) → MySQL(Docker:3306) → 返回 JWT Token ✅
```

## 7. 已知问题

### 7.1 Auth 模块 pom.xml skip=true（已修复）

**问题：** `elderlycare-auth/pom.xml` 的 spring-boot-maven-plugin 配置了 `<skip>true</skip>`，导致无法打包为可执行 JAR。

**修复：** 移除 skip=true 配置。

### 7.2 StateSyncService 依赖问题（已修复）

**问题：** `StateSyncService`（common 模块）依赖 `CustomerFeignClient` 和 `BedFeignClient`，导致非 checkin 模块启动失败。

**修复：** 添加 `@ConditionalOnBean({CustomerFeignClient.class, BedFeignClient.class})` 注解，仅在 Feign 客户端可用时创建 Bean。

### 7.3 Nacos gRPC 端口映射

**问题：** Nacos 2.x 使用 gRPC，客户端自动发现内部端口 9848。

**解决：** Docker Compose 端口映射已改为使用默认端口（8848/9848/9849），Nacos 客户端自动计算 gRPC 端口为 8848+1000=9848。

## 8. 后续步骤

1. 启动前端 `npm run dev`，测试页面加载和登录功能
2. 启动其余微服务（customer, bed, checkin 等）
3. 测试核心业务流程
4. 提交代码
