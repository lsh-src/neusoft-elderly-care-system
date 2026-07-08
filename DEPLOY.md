# 部署指南

## 方案选择

| 方案 | 适用场景 | 月费估算 | 推荐度 |
|---|---|---|---|
| **云服务器 + Docker Compose** | 中小规模项目、快速上线 | ¥200-400 | ⭐⭐⭐⭐⭐ |
| 轻量应用服务器 | 个人项目、演示环境 | ¥50-100 | ⭐⭐⭐⭐ |
| Kubernetes (ACK/TKE) | 大规模、需要弹性伸缩 | ¥1000+ | ⭐⭐⭐ |

**本项目推荐方案：云服务器 + Docker Compose**（12 个微服务，单机 4核8G 完全够用）

---

## 快速部署（3 步）

### 1. 准备云服务器

推荐配置：**4 核 8G 内存，100G SSD 系统盘**

```bash
# 购买后 SSH 登录
ssh root@your-server-ip

# 安装 Docker
curl -fsSL https://get.docker.com | sh
systemctl enable docker && systemctl start docker

# 安装 Docker Compose V2
apt install docker-compose-plugin  # Ubuntu/Debian
# 或
yum install docker-compose-plugin  # CentOS/RHEL
```

### 2. 上传代码并配置

```bash
# 方式一：Git 克隆（推荐）
git clone <your-repo-url> /opt/elderlycare
cd /opt/elderlycare

# 方式二：scp 上传
scp -r ./* root@your-server-ip:/opt/elderlycare/

# 配置环境变量
cd docker
cp .env.prod.example .env
vim .env  # ⚠️ 必须修改所有默认密码！
```

### 3. 一键部署

```bash
# 方式一：使用部署脚本
chmod +x deploy.sh
./deploy.sh

# 方式二：直接 docker compose
cd docker
docker compose -f docker-compose.prod.yml up -d --build
```

部署完成后访问：
- **前端**: `http://your-server-ip`
- **Nacos**: `http://your-server-ip:18848/nacos`
- **RabbitMQ**: `http://your-server-ip:15673`

---

## 架构图

```
                    ┌─────────────────────────────────────────┐
                    │           Cloud Server (4C8G)           │
                    │                                         │
  用户浏览器 ──────►│  ┌─────────┐    ┌──────────────────┐    │
       :80          │  │  Nginx  │───►│  Gateway :8080   │    │
                    │  │  (web)  │    └────────┬─────────┘    │
                    │  └─────────┘             │              │
                    │                    ┌─────┴──────┐       │
                    │                    ▼     ▼      ▼       │
                    │               ┌────┐ ┌────┐ ┌─────┐    │
                    │               │auth│ │user│ │ ... │    │
                    │               └────┘ └────┘ └─────┘    │
                    │                    │                     │
                    │         ┌──────────┼──────────┐         │
                    │         ▼          ▼          ▼         │
                    │    ┌────────┐ ┌────────┐ ┌─────────┐   │
                    │    │ MySQL  │ │  Nacos │ │ RabbitMQ│   │
                    │    │ :3306  │ │ :8848  │ │ :5672   │   │
                    │    └────────┘ └────────┘ └─────────┘   │
                    └─────────────────────────────────────────┘
```

---

## 常用运维命令

```bash
cd /opt/elderlycare/docker

# 查看所有服务状态
docker compose -f docker-compose.prod.yml ps

# 查看某个服务日志
docker compose -f docker-compose.prod.yml logs -f gateway

# 重启某个服务
docker compose -f docker-compose.prod.yml restart auth

# 更新部署（代码变更后）
cd /opt/elderlycare && ./deploy.sh update

# 停止所有服务
docker compose -f docker-compose.prod.yml down

# 停止并删除数据卷（⚠️ 慎用，会丢失数据）
docker compose -f docker-compose.prod.yml down -v
```

---

## 数据备份

```bash
# 备份 MySQL
docker exec prod-mysql mysqldump -u root -p"${MYSQL_ROOT_PASSWORD}" \
  --all-databases > backup_$(date +%Y%m%d).sql

# 恢复 MySQL
docker exec -i prod-mysql mysql -u root -p"${MYSQL_ROOT_PASSWORD}" \
  < backup_20260708.sql
```

---

## 域名 + HTTPS（可选）

```bash
# 1. 域名解析到服务器 IP

# 2. 安装 Certbot
apt install certbot

# 3. 获取 SSL 证书
certbot certonly --standalone -d your-domain.com

# 4. 修改 frontend/nginx.conf，添加 443 监听
# 5. 重启 web 容器
docker compose -f docker-compose.prod.yml restart web
```

---

## 资源估算

| 服务 | 内存限制 | 说明 |
|---|---|---|
| MySQL | 1G | 数据库 |
| Redis | 256M | 缓存 |
| RabbitMQ | 512M | 消息队列 |
| Nacos | 768M | 注册中心 |
| Gateway | 1G | 网关 |
| 11 个业务服务 | 各 512M | 总计 ~5.5G |
| Nginx (前端) | 128M | 静态资源 |
| **总计** | **~9G** | 推荐 16G 内存 |

如果服务器只有 8G 内存，可以降低各服务的 `memory` 限制，或合并部分低频服务。
