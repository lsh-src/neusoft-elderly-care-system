# ============================================================
# 东软颐养中心 — 前端多阶段构建
# Stage 1: Node 构建 Vue 3 应用
# Stage 2: Nginx 托管静态文件 + 反向代理
# ============================================================

# --- Stage 1: 构建 ---
FROM node:18-alpine AS builder

WORKDIR /build

# 先复制依赖文件，利用缓存层
COPY package.json package-lock.json* ./
RUN npm ci --registry=https://registry.npmmirror.com

# 复制源码并构建
COPY . .
RUN npm run build

# --- Stage 2: Nginx 托管 ---
FROM nginx:1.25-alpine

LABEL maintainer="neusoft-elderlycare"
LABEL description="东软颐养中心管理系统 — 前端"

# 移除默认配置
RUN rm /etc/nginx/conf.d/default.conf

# 复制自定义 Nginx 配置
COPY nginx.conf /etc/nginx/conf.d/

# 复制构建产物
COPY --from=builder /build/dist /usr/share/nginx/html

# 暴露端口
EXPOSE 80

HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
  CMD wget -qO- http://localhost/ || exit 1

CMD ["nginx", "-g", "daemon off;"]
