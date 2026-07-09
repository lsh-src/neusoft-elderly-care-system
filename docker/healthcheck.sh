#!/bin/bash
# ============================================================
# Docker 基础设施健康检查脚本
# 使用方法：bash docker/healthcheck.sh
# ============================================================

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo "=========================================="
echo " 东软颐养中心 — Docker 环境健康检查"
echo " $(date '+%Y-%m-%d %H:%M:%S')"
echo "=========================================="
echo ""

# 检查 Docker 是否运行
if ! docker info > /dev/null 2>&1; then
    echo -e "${RED}✗ Docker 未运行，请先启动 Docker Desktop${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Docker 正在运行${NC}"

# 检查 docker-compose 是否可用
if ! docker-compose version > /dev/null 2>&1; then
    echo -e "${RED}✗ docker-compose 不可用${NC}"
    exit 1
fi
echo -e "${GREEN}✓ docker-compose 可用${NC}"
echo ""

# 切换到 docker 目录
cd "$(dirname "$0")"

# 检查容器状态
echo "---------- 容器状态 ----------"
docker-compose ps
echo ""

# 检查各容器健康状态
check_container() {
    local name=$1
    local display_name=$2
    local status=$(docker inspect --format='{{.State.Health.Status}}' "$name" 2>/dev/null || echo "not_found")

    case $status in
        healthy)
            echo -e "${GREEN}✓ $display_name: healthy${NC}"
            return 0
            ;;
        unhealthy)
            echo -e "${RED}✗ $display_name: unhealthy${NC}"
            return 1
            ;;
        starting)
            echo -e "${YELLOW}⟳ $display_name: starting...${NC}"
            return 1
            ;;
        *)
            echo -e "${RED}✗ $display_name: $status${NC}"
            return 1
            ;;
    esac
}

echo "---------- 组件健康检查 ----------"
check_container "elderlycare-mysql" "MySQL"
check_container "elderlycare-redis" "Redis"
check_container "elderlycare-rabbitmq" "RabbitMQ"
check_container "elderlycare-nacos" "Nacos"
echo ""

# 端口连通性检查
check_port() {
    local port=$1
    local name=$2
    if netstat -an 2>/dev/null | grep -q ":${port}.*LISTEN" || \
       ss -tlnp 2>/dev/null | grep -q ":${port}" || \
       powershell -Command "Test-NetConnection -ComputerName localhost -Port $port -WarningAction SilentlyContinue | Select-Object -ExpandProperty TcpTestSucceeded" 2>/dev/null | grep -q "True"; then
        echo -e "${GREEN}✓ $name (端口 $port): 可连接${NC}"
        return 0
    else
        echo -e "${RED}✗ $name (端口 $port): 不可连接${NC}"
        return 1
    fi
}

echo "---------- 端口连通性检查 ----------"
check_port 3306 "MySQL"
check_port 6379 "Redis"
check_port 5672 "RabbitMQ AMQP"
check_port 15672 "RabbitMQ Management"
check_port 8848 "Nacos"
echo ""

# Nacos 注册服务检查
echo "---------- Nacos 服务注册检查 ----------"
NACOS_RESPONSE=$(curl -s "http://localhost:8848/nacos/v1/ns/service/list?pageNo=1&pageSize=100" 2>/dev/null || echo "")
if [ -n "$NACOS_RESPONSE" ]; then
    echo -e "${GREEN}✓ Nacos API 可访问${NC}"
    echo "  已注册服务: $NACOS_RESPONSE"
else
    echo -e "${YELLOW}⟳ Nacos API 暂不可访问（可能正在启动中）${NC}"
fi
echo ""

echo "=========================================="
echo " 检查完成"
echo "=========================================="
