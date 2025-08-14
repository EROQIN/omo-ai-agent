# omo-image-search-mcp-server 模块文档

## 模块概述
图像搜索MCP服务模块，为AI代理系统提供图像检索能力。基于Spring Boot和OpenCV实现图像特征提取与相似度匹配，通过MCP协议提供标准化接口。

## 核心功能
- 🖼️ 图像特征提取（SIFT/SURF算法）
- 🔍 多模态图像搜索
- 📡 MCP协议服务端实现
- 📦 与主系统无缝集成

## 技术架构
### 技术栈
| 类型       | 技术选型                          |
|------------|-----------------------------------|
| 框架       | Spring Boot 3.4.8                 |
| 协议       | MCP 1.0                           |
| 构建工具   | Maven 3.x                         |
| 开发语言   | Java 21                           |

### 核心设计
- 代理模式（图像特征提取代理）
- 策略模式（匹配算法选择）
- 事件驱动架构（图像处理事件）

## 目录结构
```bash
omo-image-search-mcp-server/
├── src/main/java/com/erokin/omoimagesearchmcpserver
│   ├── tools        # 图像处理工具类
│   ├── OmoImageSearchMcpServerApplication.java # 启动类
│   └── config       # MCP服务配置
├── resources
│   ├── application.yml      # 主配置文件
│   ├── application-sse.yml  # 流式传输配置
│   └── application-stdio.yml # 标准IO配置
```

## 开发环境
### 必备工具
- JDK 21
- Maven 3.x
- IDE（IntelliJ IDEA 2025.1.3）

### 运行指南
1. 本地开发
```bash
# 启动MCP服务（标准IO模式）
mvn spring-boot:run -Dspring.profiles.active=stdio

# 启动MCP服务（SSE模式）
mvn spring-boot:run -Dspring.profiles.active=sse
```

2. 配置示例
```yaml
# application.yml 配置示例
spring:
  ai:
    mcp:
      server:
        port: 8081
  opencv:
    library-path: /usr/local/opt/opencv@4/share/java
```

## 依赖管理
推荐使用阿里云Maven仓库配置：
```xml
<repositories>
  <repository>
    <id>aliyun-repo</id>
    <url>https://maven.aliyun.com/repository/public</url>
  </repository>
</repositories>
```

## 贡献指南
1. Fork仓库并创建新分支
2. 遵循Spring Boot编码规范
3. 添加单元测试（参考src/test目录）
4. 提交PR时说明修改内容

## 许可证
本项目采用 [Apache License 2.0](LICENSE) 开源协议