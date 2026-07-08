-- ============================================================
-- 创建数据库
-- 由 MySQL Docker 容器在首次启动时自动执行
-- ============================================================

-- 业务数据库
CREATE DATABASE IF NOT EXISTS `neusoft_elderly_care`
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;

-- Nacos 注册中心数据库
CREATE DATABASE IF NOT EXISTS `nacos_config`
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;

-- 授权（确保 root 用户可从任意主机连接）
ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY '123456';
FLUSH PRIVILEGES;
