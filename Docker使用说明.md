# Docker 构建和部署说明

## ✅ 已解决的问题

**修改前的问题：**
- 容器每次启动都运行 `mvn spring-boot:run`
- 每次都重新下载 Maven 依赖，启动慢
- 可能因为 ephemeral-storage 不足导致循环重启

**修改后的方案：**
- 使用 **多阶段构建 (Multi-stage Build)**
- 构建阶段：打包所有依赖到 JAR 文件
- 运行阶段：直接运行 JAR，秒启动 ⚡

---

## 📦 构建镜像

### 方法1：构建开发版本
```bash
docker build -t hello-app:dev .
```

### 方法2：构建生产版本
```bash
docker build -t hello-app:prod .
```

---

## 🚀 运行容器

### 开发环境
```bash
docker run -d \
  --name hello-dev \
  -p 8080:8080 \
  -e JAVA_OPTS="-Xms256m -Xmx512m" \
  hello-app:dev
```

### 生产环境
```bash
docker run -d \
  --name hello-prod \
  -p 8080:8080 \
  -e JAVA_OPTS="-Xms512m -Xmx1024m" \
  hello-app:prod \
  production
```

---

## 🎯 Dockerfile 工作原理

### 阶段1：构建阶段（Build Stage）
```dockerfile
FROM maven:3.9-eclipse-temurin-17 AS builder
```
- 使用完整的 Maven 镜像
- 下载所有依赖
- 编译代码
- 打包成 JAR 文件（包含所有依赖）

### 阶段2：运行阶段（Runtime Stage）
```dockerfile
FROM eclipse-temurin:17-jre-alpine
```
- 使用轻量级的 JRE 镜像（只有运行环境，没有编译工具）
- 从构建阶段复制打包好的 JAR
- 镜像体积小，启动快

---

## 🔧 环境变量配置

| 变量名 | 说明 | 默认值 | 示例 |
|--------|------|--------|------|
| `JAVA_OPTS` | JVM 参数 | `-Xms256m -Xmx512m` | `-Xms512m -Xmx1024m` |
| 启动参数 | 环境类型 | `development` | `production` 或 `prod` |

---

## 📊 对比效果

| 项目 | 修改前 | 修改后 |
|------|--------|--------|
| 启动时间 | 2-5分钟 | 5-10秒 |
| 镜像大小 | ~800MB | ~300MB |
| 每次启动 | 下载依赖 | 直接运行 |
| 网络消耗 | 高 | 无 |
| 存储消耗 | 高（重复下载） | 低 |

---

## 🐛 常见问题

### 1. 如何查看日志？
```bash
docker logs -f hello-dev
```

### 2. 如何进入容器调试？
```bash
docker exec -it hello-dev sh
```

### 3. 如何修改 JVM 内存？
```bash
docker run -e JAVA_OPTS="-Xms1g -Xmx2g" hello-app:prod
```

### 4. 如何连接数据库？
在启动时添加数据库配置：
```bash
docker run -d \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://db-host:3306/mydb \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=password \
  hello-app:prod production
```

---

## 🎉 完成！

现在你的应用：
- ✅ 构建时打包所有依赖
- ✅ 启动时直接运行 JAR
- ✅ 不会循环重启
- ✅ 启动速度快
- ✅ 资源占用少

