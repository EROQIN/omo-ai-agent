# omo-ai-agent 项目文档

## 项目概述
一个基于AI的智能代理系统，专注于恋爱建议与互动，结合多种AI模型和工具提供个性化服务。系统支持文档解析、网络搜索、文件操作、向量存储等功能。

## 系统功能
- 🤖 智能恋爱建议与互动
- 📄 文档解析（Markdown、网页等）
- 🔍 网络搜索与网页抓取
- 📁 文件操作与PDF生成
- 🧠 向量存储与检索（PGVector）
- 🧩 多种AI调用方式（DashScope、LangChain4j、Spring AI）

## 技术架构
### 技术栈
| 类型       | 技术选型                          |
|------------|-----------------------------------|
| 前端       | Vue 3 + Vite                      |
| 后端       | Spring Boot 3.4.8 + Spring AI     |
| 数据库     | PostgreSQL（PGVector扩展）        |
| AI模型     | DashScope、LangChain4j、Spring AI |
| 文档处理   | iText（PDF）、Jsoup（HTML解析）   |
| 构建工具   | Maven 3.x                         |
| 开发语言   | Java 21                           |

### 核心设计模式
- 代理模式（Advisor模块）
- 工厂模式（组件创建）
- 策略模式（AI调用策略）
- 微服务架构（基于Spring Boot）

## 项目结构
```bash
omo-ai-agent/
├── src/main/java/com/erokin/omoaiagent # 核心代码
│   ├── advisor        # AI代理逻辑
│   ├── app            # 主应用入口
│   ├── chatMemory     # 会话记忆管理
│   ├── tools          # 工具类（文件/PDF/搜索等）
│   ├── rag            # RAG模块（向量存储/检索）
│   └── config         # 配置类
├── resources          # 配置文件与文档资源
│   ├── application.yml      # 主配置文件
│   ├── application-prod.yml # 生产环境配置文件
│   └── document             # 示例文档
```

## 配置说明

项目使用Spring Profiles进行环境管理，默认使用`local`环境，生产环境使用`prod`。

### 本地开发环境配置
在`src/main/resources/application.yml`中配置：
- DashScope API密钥
- 数据库连接信息
- 服务器端口(8123)
- 其他相关配置

### 生产环境配置
在`src/main/resources/application-prod.yml`中配置：
- 生产环境的DashScope API密钥
- 生产数据库连接信息
- 其他生产环境特定配置

## 部署方式

### Docker部署（推荐）
项目包含Dockerfile，可直接构建Docker镜像：

```bash
# 构建Docker镜像
docker build -t omo-ai-agent .

# 运行容器
docker run -p 8123:8123 omo-ai-agent
```

Docker镜像将使用生产环境配置(`prod` profile)启动应用。

### 传统部署方式
```bash
# 编译打包
mvn clean package -DskipTests

# 使用生产环境配置运行
java -jar target/omo-ai-agent-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod

# 或使用默认配置运行（本地开发）
java -jar target/omo-ai-agent-0.0.1-SNAPSHOT.jar
```

## 开发环境
### 必备工具
- JDK 21
- Maven 3.x
- PostgreSQL 15+（需安装PGVector扩展）
- IDE（推荐IntelliJ IDEA 2025.1.3）

### 运行指南
1. 环境准备
```bash
# 安装依赖
mvn clean package

# 启动应用
mvn spring-boot:run

# 或构建后运行
java -jar target/omo-ai-agent-0.0.1-SNAPSHOT.jar
```

2. 配置修改
```yaml
# application.yml 配置示例
spring:
  ai:
    dashscope:
      api-key: 你的api-key  # 替换为你的DashScope API密钥
      chat:
        options:
          model: qwen-plus
```

## API文档
项目集成了Swagger UI，可通过以下地址访问API文档：
```
http://localhost:8123/api/doc.html
```

## 贡献指南
1. Fork仓库并创建新分支
2. 遵循Spring Boot编码规范
3. 添加单元测试（参考src/test目录）
4. 提交PR时说明修改内容

## 许可证
本项目采用 [Apache License 2.0](LICENSE) 开源协议