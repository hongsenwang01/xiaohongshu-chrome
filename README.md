# xiaohongshu-chrome

这是一个基于 Spring Boot 3.3.2 的后端项目。

## 本地运行

推荐环境：
- JDK 17
- MySQL

项目已经自带 Maven Wrapper，所以不需要单独安装 Maven。

### 1. 本地私密配置

真实密钥和数据库配置放在项目根目录的 `application-local.properties` 中。
这个文件不会上传到 Git，并且会覆盖 `src/main/resources/application*.properties` 里的占位配置。

### 2. 准备数据库

按需执行以下脚本：
- `src/main/resources/db/schema.sql`

### 3. 启动项目

Windows：

```powershell
.\mvnw.cmd spring-boot:run
```

如果需要打包：

```powershell
.\mvnw.cmd clean package
```

### 4. 默认访问地址

- `http://localhost:8080`

## 当前保留的本地开发文件

- `pom.xml`
- `mvnw`
- `mvnw.cmd`
- `application-local.properties`
- `src/main/resources/application*.properties`

## 说明

Docker 和外部部署相关文件已经移除。
当前仓库以本地开发和本地运行为主。