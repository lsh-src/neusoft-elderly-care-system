#!/bin/bash
# ============================================================
# Docker 基础设施连通性测试
# 使用方法：bash docker/test-connectivity.sh
# ============================================================

set -e

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

PASSED=0
FAILED=0
TOTAL=0

pass() {
    PASSED=$((PASSED + 1))
    TOTAL=$((TOTAL + 1))
    echo -e "${GREEN}✓ $1${NC}"
}

fail() {
    FAILED=$((FAILED + 1))
    TOTAL=$((TOTAL + 1))
    echo -e "${RED}✗ $1${NC}"
}

warn() {
    echo -e "${YELLOW}⚠ $1${NC}"
}

echo "=========================================="
echo " 基础设施连通性测试"
echo " $(date '+%Y-%m-%d %H:%M:%S')"
echo "=========================================="
echo ""

# ---- MySQL ----
echo "---------- MySQL ----------"
if command -v mysql &> /dev/null; then
    if mysql -h 127.0.0.1 -P 3306 -u root -p123456 -e "SELECT 1" &> /dev/null; then
        pass "MySQL 连接成功"

        # 检查数据库
        DBS=$(mysql -h 127.0.0.1 -P 3306 -u root -p123456 -e "SHOW DATABASES;" 2>/dev/null | tr '\n' ' ')
        if echo "$DBS" | grep -q "neusoft_elderly_care"; then
            pass "数据库 neusoft_elderly_care 存在"
        else
            fail "数据库 neusoft_elderly_care 不存在"
        fi

        if echo "$DBS" | grep -q "nacos_config"; then
            pass "数据库 nacos_config 存在"
        else
            fail "数据库 nacos_config 不存在"
        fi

        # 检查业务表
        TABLES=$(mysql -h 127.0.0.1 -P 3306 -u root -p123456 neusoft_elderly_care -e "SHOW TABLES;" 2>/dev/null | wc -l)
        if [ "$TABLES" -gt 1 ]; then
            pass "业务数据表已初始化 ($((TABLES - 1)) 张表)"
        else
            fail "业务数据表未初始化"
        fi
    else
        fail "MySQL 连接失败"
    fi
else
    warn "mysql 客户端未安装，跳过命令行测试"
    # 尝试使用 Docker 内的 mysql 客户端
    if docker exec elderlycare-mysql mysql -u root -p123456 -e "SELECT 1" &> /dev/null; then
        pass "MySQL 连接成功（通过 Docker）"
    else
        fail "MySQL 连接失败"
    fi
fi
echo ""

# ---- Redis ----
echo "---------- Redis ----------"
if command -v redis-cli &> /dev/null; then
    RESULT=$(redis-cli -h 127.0.0.1 -p 6379 -a 123456 PING 2>/dev/null)
    if [ "$RESULT" = "PONG" ]; then
        pass "Redis 连接成功 (PING -> PONG)"

        # 测试读写
        redis-cli -h 127.0.0.1 -p 6379 -a 123456 SET test_key "hello" EX 10 &> /dev/null
        GET_RESULT=$(redis-cli -h 127.0.0.1 -p 6379 -a 123456 GET test_key 2>/dev/null)
        if [ "$GET_RESULT" = "hello" ]; then
            pass "Redis 读写测试通过"
        else
            fail "Redis 读写测试失败"
        fi
        redis-cli -h 127.0.0.1 -p 6379 -a 123456 DEL test_key &> /dev/null
    else
        fail "Redis 连接失败"
    fi
else
    warn "redis-cli 未安装，跳过命令行测试"
    if docker exec elderlycare-redis redis-cli -a 123456 PING 2>/dev/null | grep -q "PONG"; then
        pass "Redis 连接成功（通过 Docker）"
    else
        fail "Redis 连接失败"
    fi
fi
echo ""

# ---- RabbitMQ ----
echo "---------- RabbitMQ ----------"
# AMQP 端口检查
if command -v curl &> /dev/null; then
    # 管理界面 API
    RABBIT_API=$(curl -s -u guest:guest http://localhost:15672/api/overview 2>/dev/null)
    if echo "$RABBIT_API" | grep -q "rabbitmq_version"; then
        pass "RabbitMQ 管理界面可访问"

        # 检查队列
        QUEUES=$(curl -s -u guest:guest http://localhost:15672/api/queues/%2F 2>/dev/null)
        QUEUE_COUNT=$(echo "$QUEUES" | grep -o '"name":"elderlycare' | wc -l)
        if [ "$QUEUE_COUNT" -ge 5 ]; then
            pass "RabbitMQ 队列已预配置 ($QUEUE_COUNT 个业务队列)"
        else
            warn "RabbitMQ 队列数量: $QUEUE_COUNT（可能尚未完全初始化）"
        fi
    else
        fail "RabbitMQ 管理界面不可访问"
    fi
else
    warn "curl 未安装，跳过 API 测试"
fi
echo ""

# ---- Nacos ----
echo "---------- Nacos ----------"
if command -v curl &> /dev/null; then
    NACOS_HEALTH=$(curl -s http://localhost:8848/nacos/actuator/health 2>/dev/null)
    if echo "$NACOS_HEALTH" | grep -q '"status":"UP"'; then
        pass "Nacos 健康检查通过"

        # 检查已注册服务
        SERVICES=$(curl -s "http://localhost:8848/nacos/v1/ns/service/list?pageNo=1&pageSize=100" 2>/dev/null)
        if [ -n "$SERVICES" ] && [ "$SERVICES" != "" ]; then
            pass "Nacos API 可访问"
        else
            warn "Nacos API 暂无注册服务（后端微服务尚未启动）"
        fi
    else
        fail "Nacos 健康检查失败"
    fi
else
    warn "curl 未安装，跳过 API 测试"
fi
echo ""

# ---- 汇总 ----
echo "=========================================="
echo " 测试结果汇总"
echo "=========================================="
echo -e " 总计: $TOTAL"
echo -e " 通过: ${GREEN}$PASSED${NC}"
echo -e " 失败: ${RED}$FAILED${NC}"

if [ "$FAILED" -eq 0 ]; then
    echo -e "\n${GREEN}所有基础设施连通性测试通过！${NC}"
    exit 0
else
    echo -e "\n${RED}有 $FAILED 项测试失败，请检查相关服务。${NC}"
    exit 1
fi
