#!/bin/bash

# ============================================
# DevBox 项目构建脚本
# 功能：下载依赖、编译代码、打包 JAR
# ============================================

set -e  # 遇到错误立即退出

echo "======================================"
echo "🚀 开始构建项目..."
echo "======================================"

# 获取脚本所在目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# 显示 Maven 版本
echo ""
echo "📦 Maven 版本信息："
mvn --version
echo ""

# 清理之前的构建
echo "🧹 清理之前的构建产物..."
mvn clean

echo ""
echo "⬇️  下载依赖并打包项目..."
echo "   (这可能需要几分钟时间，请稍候...)"
echo ""

# 下载依赖、编译代码、打包 JAR（跳过测试以加快速度）
mvn package -DskipTests

# 检查 JAR 是否生成成功
JAR_FILE="$SCRIPT_DIR/target/hello-0.0.1-SNAPSHOT.jar"

if [ -f "$JAR_FILE" ]; then
    echo ""
    echo "======================================"
    echo "✅ 构建成功！"
    echo "======================================"
    echo ""
    echo "📦 JAR 文件信息："
    ls -lh "$JAR_FILE"
    echo ""
    echo "🎯 下一步操作："
    echo "   开发环境启动：./entrypoint.sh"
    echo "   生产环境启动：./entrypoint.sh production"
    echo ""
else
    echo ""
    echo "======================================"
    echo "❌ 构建失败！"
    echo "======================================"
    echo "JAR 文件未生成，请检查错误信息"
    exit 1
fi

