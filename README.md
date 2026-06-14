# LBY AI 零代码应用生成平台

> 作者：lby

基于 Spring Boot 3 + LangChain4j + Vue 3 的企业级 AI 代码生成平台，支持智能代码生成、可视化编辑、一键部署分享和企业级管理。

## 项目结构

```
lby-ai-code-mother/          # 单体后端（主模块）
lby-ai-code-frontend/        # Vue 3 前端
lby-ai-code-microservice/    # 微服务版本（可选）
sql/                         # 数据库初始化脚本
```

## 技术栈

- **后端**：Spring Boot 3、LangChain4j、LangGraph4j、MyBatis-Flex、Redis、MySQL
- **前端**：Vue 3、TypeScript、Ant Design Vue、Vite
- **AI**：DeepSeek、阿里云 DashScope、Pexels 图片搜索
- **部署**：腾讯云 COS、Prometheus + Grafana 监控

## 快速开始

### 1. 数据库初始化

```bash
mysql -u root -p < sql/create_table.sql
```

### 2. 后端配置

复制本地配置模板并填写实际值：

```bash
cp src/main/resources/application-local-sample.yml src/main/resources/application-local.yml
```

`application-local.yml` 已被 `.gitignore` 忽略，不会上传到 GitHub。

需要配置的内容包括：MySQL、Redis、DeepSeek API Key、DashScope API Key、腾讯云 COS 等。

### 3. 启动后端

```bash
./mvnw spring-boot:run
```

后端默认运行在 `http://localhost:8123/api`

### 4. 前端配置与启动

```bash
cd lby-ai-code-frontend
cp .env.example .env.development
npm install
npm run dev
```

## 敏感文件说明

以下文件/目录**不会**被 Git 跟踪，上传 GitHub 前请确认：

| 文件 | 说明 |
|------|------|
| `application-local.yml` | 本地开发配置（含 API Key、数据库密码） |
| `application-prod.yml` | 生产环境配置 |
| `.env.development` / `.env.production` | 前端环境变量 |
| `tmp/` | 代码生成临时目录 |
| `.idea/` | IDE 配置 |
| `node_modules/` | 前端依赖 |

## 核心功能

1. **智能代码生成**：输入需求描述，AI 流式生成代码
2. **可视化编辑**：选中页面元素与 AI 对话修改
3. **一键部署分享**：部署到云端并生成访问链接
4. **企业级管理**：用户管理、应用管理、系统监控

## License

MIT
