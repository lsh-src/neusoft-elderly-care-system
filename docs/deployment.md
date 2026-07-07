# 东软颐养中心管理系统 — 部署指南

---

## 1. 环境要求

| 软件 | 版本 | 说明 |
|------|------|------|
| JDK | 17+ | 推荐 Oracle JDK 或 OpenJDK |
| Maven | 3.8+ | 后端构建工具 |
| Node.js | 18+ | 前端运行环境 |
| npm | 9+ | 前端包管理 |
| MySQL | 8.0+ | 数据库 |
| IDE（可选） | IntelliJ IDEA 2024 | 后端开发 |
| 编辑器（可选） | VSCode | 前端开发 |

---

## 2. 数据库初始化

### 2.1 创建数据库

```sql
CREATE DATABASE neusoft_elderly_care CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
```

### 2.2 导入表结构和数据

```bash
mysql -u root -p neusoft_elderly_care < database/neusoft_elderly_care.sql
```

或在 Navicat 中右键数据库 → 运行 SQL 文件 → 选择 `database/neusoft_elderly_care.sql`

### 2.3 验证

```sql
USE neusoft_elderly_care;
SHOW TABLES;
-- 应显示 14 张表
```

---

## 3. 后端启动

### 3.1 命令行启动

```bash
cd backend
mvn clean package -DskipTests
java -jar elderlycare-app/target/elderlycare-app-1.0.0.jar
```

### 3.2 IDEA 启动

1. 用 IDEA 打开 `backend` 文件夹
2. 等待 Maven 自动下载依赖
3. 打开 `ElderlyCareApplication.java`
4. 点击 `main` 方法左侧绿色运行按钮

### 3.3 验证

浏览器访问 `http://localhost:8080/api/auth/login`，应返回 405 Method Not Allowed（因为需要 POST）。

---

## 4. 前端启动

### 4.1 安装依赖

```bash
cd frontend
npm install
```

### 4.2 启动开发服务器

```bash
npm run dev
```

### 4.3 验证

浏览器访问 `http://localhost:5173`，应看到登录页面。

---

## 5. 演示账号

| 角色 | 手机号 | 密码 |
|------|--------|------|
| 管理员 | 13800000000 | 123456 |
| 健康管家 | 13800000001 | 123456 |
| 入住老人 | 13800000002 | 123456 |

> 后端启动时 `DataInitializer` 自动创建以上账号。

---

## 6. 常见问题

### 6.1 端口被占用

```bash
# 查看 8080 端口占用
netstat -ano | findstr :8080
# 杀掉进程
taskkill /PID <进程ID> /F
```

### 6.2 Maven 依赖下载慢

使用阿里云镜像，在 `settings.xml` 中添加：

```xml
<mirrors>
  <mirror>
    <id>aliyun</id>
    <mirrorOf>central</mirrorOf>
    <url>https://maven.aliyun.com/repository/central</url>
  </mirror>
</mirrors>
```

### 6.3 npm install 失败

```bash
# 清除缓存
npm cache clean --force
# 使用淘宝镜像
npm config set registry https://registry.npmmirror.com
npm install
```

### 6.4 数据库连接失败

检查 `backend/elderlycare-app/src/main/resources/application.yml` 中的数据库配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/neusoft_elderly_care?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: 123456
```

---

## 7. 生产环境部署（可选）

### 7.1 后端打包

```bash
cd backend
mvn clean package -DskipTests
```

### 7.2 前端打包

```bash
cd frontend
npm run build
```

生成的静态文件在 `frontend/dist/` 目录。

### 7.3 Nginx 配置示例

```nginx
server {
    listen 80;
    server_name your-domain.com;

    # 前端静态文件
    location / {
        root /path/to/frontend/dist;
        try_files $uri $uri/ /index.html;
    }

    # 后端 API 代理
    location /api/ {
        proxy_pass http://localhost:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```
