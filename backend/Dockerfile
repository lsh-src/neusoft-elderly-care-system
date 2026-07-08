# ============================================================
# 东软颐养中心 — 后端多阶段构建
# 构建所有微服务模块，生成可运行的 JAR
# ============================================================

# --- Stage 1: Maven 构建 ---
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build
# 先复制 pom 文件，利用 Docker 缓存层加速依赖下载
COPY pom.xml ./
COPY elderlycare-common/pom.xml ./elderlycare-common/
COPY elderlycare-auth/pom.xml ./elderlycare-auth/
COPY elderlycare-user/pom.xml ./elderlycare-user/
COPY elderlycare-customer/pom.xml ./elderlycare-customer/
COPY elderlycare-bed/pom.xml ./elderlycare-bed/
COPY elderlycare-checkin/pom.xml ./elderlycare-checkin/
COPY elderlycare-meal/pom.xml ./elderlycare-meal/
COPY elderlycare-service/pom.xml ./elderlycare-service/
COPY elderlycare-nursing/pom.xml ./elderlycare-nursing/
COPY elderlycare-dashboard/pom.xml ./elderlycare-dashboard/
COPY elderlycare-gateway/pom.xml ./elderlycare-gateway/
COPY elderlycare-ai/pom.xml ./elderlycare-ai/

# 下载依赖（缓存层 — 只有 pom 变化才重新下载）
RUN mvn dependency:go-offline -B || true

# 复制源码并构建
COPY . .
RUN mvn clean package -DskipTests -B

# --- Stage 2: 轻量运行时 ---
FROM eclipse-temurin:17-jre-alpine

LABEL maintainer="neusoft-elderlycare"
LABEL description="东软颐养中心管理系统 — 后端微服务"

RUN apk add --no-cache tzdata curl \
    && cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
    && echo "Asia/Shanghai" > /etc/timezone

WORKDIR /app

# 从构建阶段复制所有模块的 JAR
COPY --from=builder /build/elderlycare-gateway/target/elderlycare-gateway-*.jar elderlycare-gateway.jar
COPY --from=builder /build/elderlycare-auth/target/elderlycare-auth-*.jar elderlycare-auth.jar
COPY --from=builder /build/elderlycare-user/target/elderlycare-user-*.jar elderlycare-user.jar
COPY --from=builder /build/elderlycare-customer/target/elderlycare-customer-*.jar elderlycare-customer.jar
COPY --from=builder /build/elderlycare-bed/target/elderlycare-bed-*.jar elderlycare-bed.jar
COPY --from=builder /build/elderlycare-checkin/target/elderlycare-checkin-*.jar elderlycare-checkin.jar
COPY --from=builder /build/elderlycare-meal/target/elderlycare-meal-*.jar elderlycare-meal.jar
COPY --from=builder /build/elderlycare-service/target/elderlycare-service-*.jar elderlycare-service.jar
COPY --from=builder /build/elderlycare-nursing/target/elderlycare-nursing-*.jar elderlycare-nursing.jar
COPY --from=builder /build/elderlycare-dashboard/target/elderlycare-dashboard-*.jar elderlycare-dashboard.jar
COPY --from=builder /build/elderlycare-ai/target/elderlycare-ai-*.jar elderlycare-ai.jar

# 通用入口：通过 SERVICE_JAR 环境变量指定要运行的模块
ENV SERVICE_JAR="elderlycare-gateway"

# JVM 优化参数（容器环境）
ENV JAVA_OPTS="-XX:+UseContainerSupport \
  -XX:MaxRAMPercentage=75.0 \
  -XX:InitialRAMPercentage=50.0 \
  -Djava.security.egd=file:/dev/./urandom"

# 健康检查由 docker-compose 按服务端口配置
# 启动时根据 SERVICE_JAR 动态选择 JAR
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app/${SERVICE_JAR}.jar ${SPRING_OPTS}"]
