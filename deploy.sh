#!/bin/bash
# ============================================================
# 东软颐养中心管理系统 — 一键部署脚本
#
# 使用方式：
#   chmod +x deploy.sh
#   ./deploy.sh          # 完整部署（构建 + 启动）
#   ./deploy.sh build    # 仅构建镜像
#   ./deploy.sh start    # 仅启动服务
#   ./deploy.sh stop     # 停止服务
#   ./deploy.sh logs     # 查看日志
#   ./deploy.sh status   # 查看状态
#   ./deploy.sh update   # 更新并重启（不重新构建基础设施）
# ============================================================

set -e

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
DOCKER_DIR="${PROJECT_DIR}/docker"
COMPOSE_FILE="docker-compose.prod.yml"

# 颜色
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

log() { echo -e "${GREEN}[INFO]${NC} $1"; }
warn() { echo -e "${YELLOW}[WARN]${NC} $1"; }
error() { echo -e "${RED}[ERROR]${NC} $1"; exit 1; }

# 检查前置条件
check_prereqs() {
    log "检查前置条件..."
    command -v docker >/dev/null 2>&1 || error "未安装 Docker，请先安装 Docker 20.10+"
    docker compose version >/dev/null 2>&1 || error "未安装 Docker Compose V2"

    # 检查 .env 文件
    if [ ! -f "${DOCKER_DIR}/.env" ]; then
        warn ".env 文件不存在，从模板创建..."
        cp "${DOCKER_DIR}/.env.example" "${DOCKER_DIR}/.env"
        warn "请编辑 ${DOCKER_DIR}/.env 修改所有默认密码后重新运行"
        exit 1
    fi

    log "前置条件检查通过 ✓"
}

# 构建镜像
build() {
    log "构建后端镜像..."
    docker compose -f "${DOCKER_DIR}/${COMPOSE_FILE}" build gateway
    log "后端镜像构建完成 ✓"

    log "构建前端镜像..."
    docker compose -f "${DOCKER_DIR}/${COMPOSE_FILE}" build web
    log "前端镜像构建完成 ✓"
}

# 启动服务
start() {
    log "启动所有服务..."
    cd "${DOCKER_DIR}" && docker compose -f "${COMPOSE_FILE}" up -d
    log "所有服务已启动 ✓"
    echo ""
    log "访问地址："
    log "  前端:     http://localhost"
    log "  Nacos:    http://localhost:18848/nacos"
    log "  RabbitMQ: http://localhost:15673"
    echo ""
    log "查看日志: docker compose -f ${COMPOSE_FILE} logs -f"
}

# 停止服务
stop() {
    log "停止所有服务..."
    cd "${DOCKER_DIR}" && docker compose -f "${COMPOSE_FILE}" down
    log "所有服务已停止 ✓"
}

# 查看日志
logs() {
    cd "${DOCKER_DIR}" && docker compose -f "${COMPOSE_FILE}" logs -f --tail=100
}

# 查看状态
status() {
    cd "${DOCKER_DIR}" && docker compose -f "${COMPOSE_FILE}" ps
}

# 更新部署（不重建基础设施）
update() {
    log "更新部署..."
    log "重新构建后端镜像..."
    docker compose -f "${DOCKER_DIR}/${COMPOSE_FILE}" build gateway

    log "重启后端微服务..."
    cd "${DOCKER_DIR}" && docker compose -f "${COMPOSE_FILE}" up -d \
        gateway auth user customer bed checkin meal service nursing dashboard ai

    log "重新构建并重启前端..."
    docker compose -f "${COMPOSE_FILE}" build web
    docker compose -f "${COMPOSE_FILE}" up -d web

    log "更新完成 ✓"
}

# 主逻辑
case "${1:-all}" in
    build)  check_prereqs; build ;;
    start)  check_prereqs; start ;;
    stop)   stop ;;
    logs)   logs ;;
    status) status ;;
    update) check_prereqs; update ;;
    all)
        check_prereqs
        build
        start
        ;;
    *)
        echo "用法: $0 {build|start|stop|logs|status|update|all}"
        exit 1
        ;;
esac
