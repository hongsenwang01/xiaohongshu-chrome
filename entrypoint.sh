#!/bin/bash

app_env=${1:-development}

# 获取脚本所在目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# JAR 文件路径
JAR_FILE="$SCRIPT_DIR/target/hello-0.0.1-SNAPSHOT.jar"

# 检查 JAR 文件是否存在
if [ ! -f "$JAR_FILE" ]; then
    echo "错误：JAR 文件不存在！"
    echo "请先运行：mvn clean package"
    exit 1
fi

# 设置 JVM 参数
# 可以通过环境变量 JAVA_OPTS 来覆盖
JAVA_OPTS="${JAVA_OPTS:--Xms256m -Xmx512m}"

# 设置 Spring Profile
# 只有明确指定环境时才通过命令行覆盖，否则使用 application.properties 中的配置
if [ "$app_env" = "production" ] || [ "$app_env" = "prod" ]; then
    echo "Starting application in PRODUCTION mode..."
    SPRING_PROFILE_ARG="-Dspring.profiles.active=prod"
elif [ "$app_env" = "dev" ] || [ "$app_env" = "development" ]; then
    echo "Starting application in DEVELOPMENT mode..."
    SPRING_PROFILE_ARG="-Dspring.profiles.active=dev"
else
    echo "Starting application (using application.properties profile)..."
    SPRING_PROFILE_ARG=""
fi

# 直接运行打包好的 JAR 文件，不再使用 Maven
echo "JAR File: $JAR_FILE"
echo "Java Options: $JAVA_OPTS"
echo "Profile Override: ${SPRING_PROFILE_ARG:-'None (using application.properties)'}"
echo "=========================================="

exec java $JAVA_OPTS \
    $SPRING_PROFILE_ARG \
    -jar "$JAR_FILE"
