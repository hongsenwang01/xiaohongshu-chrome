# ============================================
# DevBox 优化版 Dockerfile
# 在 DevBox 中已经提前打包好 JAR，这里直接使用
# ============================================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# 直接复制 DevBox 中已打包好的 JAR 文件
# 这样避免了容器启动时重新下载依赖
COPY target/hello-0.0.1-SNAPSHOT.jar /app/app.jar

# 复制启动脚本
COPY entrypoint.sh /app/entrypoint.sh
RUN chmod +x /app/entrypoint.sh

# 暴露端口（根据你的应用配置调整）
EXPOSE 8080

# 使用启动脚本
ENTRYPOINT ["/app/entrypoint.sh"]

