# InterviewHelper — AI 求职助手

一个面向求职者的 AI 面试辅助平台：在线管理个人简历，并基于简历内容与 DeepSeek 大模型进行 AI 模拟面试，面试结束后自动生成评分与改进建议。

项目采用「Java 业务服务 + Python AI 服务」分离架构，共三个模块：

| 模块 | 说明 |
| --- | --- |
| `interview_helper_backend` | Java 业务服务，Spring Boot 3 + MyBatis-Plus + MySQL，提供 REST API，不直接调用大模型 |
| `interview_helper_ai` | Python AI 服务，FastAPI + OpenAI SDK，统一封装 Prompt 与大模型调用 |
| `interview_helper_frontend` | 前端单页应用，Vue 3 + TypeScript + Vite，生产环境由 Nginx 托管 |

## 系统架构

```text
浏览器
  │  80 端口（唯一对外入口）
  ▼
Nginx（frontend 容器）
  ├─ 静态页面（Vue SPA）
  └─ /api 反向代理 ──► Java 后端（backend 容器，不对外暴露端口）
                            │  AiClient 统一调用
                            ▼
                      Python AI 服务（ai 容器，不对外暴露端口）
                            │
                            ▼
                      DeepSeek 大模型 API

MySQL（db 容器，不对外暴露端口，仅容器间访问）
```

设计原则：

- Java 业务层不直接调用大模型，所有 AI 逻辑统一由 Python 服务提供接口
- Prompt 管理、大模型调用全部收敛在 Python 服务侧，Java 侧不保留任何 Prompt 常量
- Java 通过统一的 `AiClient` 组件调用 Python 服务，业务 Service 中不写 HTTP 调用逻辑
- 部署时仅前端 Nginx 对外暴露 80 端口，其余服务只允许容器间通过服务名互相访问

## 功能特性

### 用户模块
- 注册、登录（邮箱 + 密码，密码使用 BCrypt 加密存储）
- JWT 认证，token 有效期 7 天
- 查看个人资料、退出登录、注销账号
- 登出 / 注销时递增 token 版本号，使旧 token 立即全部失效
- 登录失败限流（同一邮箱 10 分钟内最多失败 5 次、同一 IP 最多 20 次），防止暴力破解

### 简历模块
- 个人基本信息管理（姓名、年龄、学历、期望职位、技能、证书等）
- 教育经历、项目经历、工作经历的增删改查
- AI 简历优化：对简历内容给出优化后文案

### AI 模拟面试模块
- 开始面试：AI 面试官结合简历上下文生成第一道面试题
- 多轮对话：AI 追问与应答，逐条保存会话消息
- 结束面试：AI 对整场对话进行评估，输出评分与整体建议
- 面试记录查看与删除（删除时先清理会话消息，再删除面试记录）

## 技术栈

### Java 业务服务
- Java 21、Spring Boot 3.4.4
- MyBatis-Plus 3.5.10、MySQL
- JWT（jjwt 0.12.6）、jBCrypt / spring-security-crypto
- MapStruct 1.6.3、Lombok
- 测试：JUnit（spring-boot-starter-test）
- 安全扫描：OWASP dependency-check-maven

### Python AI 服务
- Python 3.12、FastAPI、Uvicorn
- OpenAI SDK（对接 DeepSeek 接口）
- Pydantic（请求 / 响应模型，字段与 Java DTO 保持一致）

### 前端
- Vue 3.5、TypeScript、Vite 8
- Vue Router 4（路由级登录守卫）
- Axios（统一 API 客户端，自动携带 Authorization 头，接口超时 120 秒适配 AI 长耗时）
- 生产环境：Nginx（静态资源 + `/api` 反向代理）

## 项目结构

```text
InterviewHelper/
├── docker-compose.yml       # 容器编排（db / ai / backend / frontend）
├── .env                     # 部署环境变量（不提交 git）
├── interview_helper_ai/
│   ├── main.py              # FastAPI 应用：4 个 AI 接口 + Prompt 管理
│   ├── requirements.txt     # Python 依赖
│   └── Dockerfile
├── interview_helper_backend/
│   ├── src/main/java/com/example/interview_helper_backend/
│   │   ├── client/          # AiClient：调用 Python AI 服务的统一入口
│   │   ├── config/          # CORS、拦截器注册、安全响应头过滤器
│   │   ├── controller/      # User / Resume / Interview 三组 REST 接口
│   │   ├── service/         # 业务逻辑
│   │   ├── mapper/          # MyBatis-Plus 数据访问层
│   │   ├── model/           # entity / dto / vo 分层模型
│   │   ├── interceptor/     # JWT 认证拦截器
│   │   ├── exception/       # 业务异常体系
│   │   ├── hander/          # 全局异常处理器
│   │   ├── converter/       # MapStruct 对象转换
│   │   ├── result/          # 统一响应结构 Result
│   │   └── util/            # JwtUtil、BCrypt、登录限流、UserContext
│   ├── src/main/resources/
│   │   ├── application.yml  # 配置（敏感项全部走环境变量）
│   │   ├── application-dev.yml / application-prod.yml
│   │   └── db/init.sql      # 数据库初始化脚本（7 张表）
│   ├── settings.xml         # Maven 阿里云镜像源（国内加速）
│   └── Dockerfile
└── interview_helper_frontend/
    ├── src/
    │   ├── api/             # auth / resume / interview 接口封装
    │   ├── views/           # 登录、注册、首页、简历、面试、个人中心
    │   ├── components/      # 布局、Toast、Confirm 等通用组件
    │   ├── composables/     # useToast / useConfirm
    │   └── router/          # 路由与登录守卫
    ├── nginx.conf           # Nginx 配置（路由 + /api 反向代理）
    └── Dockerfile
```

## 快速开始

### 方式一：本地开发

#### 环境要求

- JDK 21、Maven 3.9+
- Python 3.12+
- MySQL 8.x
- Node.js `^22.18.0 || >=24.12.0`（npm 随附）

#### 1. 初始化数据库

```bash
mysql -u root -p < interview_helper_backend/src/main/resources/db/init.sql
```

脚本会自动创建 `interview_helper` 库及 7 张表（users、resume、resume_education、resume_project、resume_work、interview、session_message）。

#### 2. 配置环境变量

Java 服务参考 [interview_helper_backend/.env.example](interview_helper_backend/.env.example)：

| 变量 | 说明 | 示例 |
| --- | --- | --- |
| `DB_URL` | 数据库连接串 | `jdbc:mysql://localhost:3306/interview_helper?useSSL=false&serverTimezone=Asia/Shanghai` |
| `DB_USERNAME` | 数据库用户名 | `root` |
| `DB_PASSWORD` | 数据库密码 | — |
| `AI_SERVICE_URL` | Python AI 服务地址 | `http://localhost:8000` |
| `JWT_SECRET` | JWT 密钥（随机 Base64 字符串），不要提交进 git | — |
| `CORS_ALLOWED_ORIGINS` | 前端跨域白名单，多个用逗号分隔 | `http://localhost:5173` |
| `SPRING_PROFILES_ACTIVE` | 环境（默认 `dev`，生产填 `prod`） | `dev` |

Python AI 服务需要以下变量（可在 `interview_helper_ai/.env` 中配置）：

| 变量 | 说明 | 示例 |
| --- | --- | --- |
| `OPENAI_API_KEY` | DeepSeek API Key（[platform.deepseek.com](https://platform.deepseek.com) 申请） | `sk-xxxx` |
| `OPENAI_API_BASE` | DeepSeek 接口地址 | `https://api.deepseek.com` |

PowerShell 生成 JWT 密钥：

```powershell
[Convert]::ToBase64String((1..32 | ForEach-Object { Get-Random -Maximum 256 }))
```

> Spring Boot 不会自动读取 `.env` 文件，请将变量注入系统环境变量（或 IDE 运行配置）后再启动。环境变量仅在当前终端会话生效，切换终端后需要重新设置。

#### 3. 启动 Python AI 服务

```powershell
cd interview_helper_ai
.venv\Scripts\Activate.ps1     # 激活虚拟环境（终端出现 (.venv) 前缀即成功）
uvicorn main:app --reload --port 8000
```

服务监听 `http://localhost:8000`，接口文档见 `http://localhost:8000/docs`。

#### 4. 启动 Java 业务服务

```bash
cd interview_helper_backend
mvn spring-boot:run
```

后端默认监听 `http://localhost:8080`。

#### 5. 启动前端

```bash
cd interview_helper_frontend
npm install
npm run dev
```

前端默认运行在 `http://localhost:5173`，`/api` 请求由 Vite 开发代理转发到 `http://localhost:8080`（见 [vite.config.ts](interview_helper_frontend/vite.config.ts)）。

> 启动顺序建议：Python AI 服务 → Java 后端 → 前端。

### 方式二：Docker Compose 一键部署

#### 环境要求

- Docker + Docker Compose

#### 1. 配置环境变量

在项目根目录创建 `.env`（已被 `.gitignore` 排除，不会提交）：

```text
DB_PASSWORD=你的数据库密码
JWT_SECRET=随机Base64串
OPENAI_API_KEY=sk-xxxx
OPENAI_API_BASE=https://api.deepseek.com
```

#### 2. 构建并启动

```bash
docker compose up -d --build
```

四个容器启动后，浏览器访问 `http://localhost` 即可使用。查看状态：

```bash
docker compose ps
docker compose logs -f backend
```

#### 部署说明

- 仅 `frontend` 容器对外映射 `80:80`，`db` / `ai` / `backend` 均不对外映射端口，只允许容器间通过服务名（`db`、`ai`、`backend`）互相访问
- 数据库数据通过 `db-data` 卷持久化，容器删除数据不丢
- 首次启动 MySQL 时自动执行 `init.sql` 建库建表
- `backend` 依赖数据库健康检查通过后才启动；MySQL 8 的 JDBC 连接串需带 `allowPublicKeyRetrieval=true`（已在编排文件中配置）
- 各服务 Dockerfile 均采用多阶段构建，仅保留运行时依赖
- 国内部署已配置 Docker Hub 镜像加速、npm 淘宝源、Maven 阿里云源

## AI 服务接口契约

Python AI 服务提供 4 个固定接口，请求 / 响应字段与 Java 侧 `AiClient` 保持一致：

| 方法 | 路径 | 入参 | 出参 | 说明 |
| --- | --- | --- | --- | --- |
| POST | `/ai/interview/first-question` | `resumeContext`（可选） | `content` | 结合简历生成第一道面试题 |
| POST | `/ai/interview/reply` | `conversation` | `content` | 针对最新回答点评并追问 |
| POST | `/ai/interview/evaluate` | `conversation` | `content` | 对整场面试输出「评分 + 建议」 |
| POST | `/ai/resume/optimize` | `summary` / `project_description` / `education_appendix` / `work_description`（均可选） | 同名四个字段 | 逐段优化简历文案 |

健康检查：`GET /ai/health`。

## API 概览（Java 对外接口）

统一响应结构为 `Result`（`code` / `message` / `data`）。除登录、注册外，所有接口均需在请求头携带 `Authorization: Bearer <token>`。

### 用户 `/api/user`

| 方法 | 路径 | 说明 | 鉴权 |
| --- | --- | --- | --- |
| POST | `/api/user/register` | 注册 | 否 |
| POST | `/api/user/login` | 登录，返回 token | 否 |
| GET | `/api/user/profile` | 查看个人资料 | 是 |
| POST | `/api/user/logout` | 退出登录 | 是 |
| DELETE | `/api/user/delete` | 注销账号 | 是 |

### 简历 `/api/resume`

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/api/resume/createResume` | 创建简历 |
| GET | `/api/resume/viewResume` | 查看简历 |
| PUT | `/api/resume/updateResume` | 更新简历 |
| DELETE | `/api/resume/deleteResume` | 删除简历 |
| POST/GET/PUT/DELETE | `/api/resume/createResumeEducation` 等 | 教育经历增删改查 |
| POST/GET/PUT/DELETE | `/api/resume/createResumeProject` 等 | 项目经历增删改查 |
| POST/GET/PUT/DELETE | `/api/resume/createResumeWork` 等 | 工作经历增删改查 |
| POST | `/api/resume/optimizeResume` | AI 优化简历内容 |

### 面试 `/api/interview`

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/api/interview/startInterview` | 开始面试，返回 AI 第一道题 |
| POST | `/api/interview/sendMessage` | 发送回答，返回最新会话消息 |
| GET | `/api/interview/viewMessages?interviewId=` | 查看会话消息 |
| GET | `/api/interview/viewInterview` | 查看面试记录 |
| POST | `/api/interview/endInterview` | 结束面试，返回评分与建议 |
| DELETE | `/api/interview/deleteInterview` | 删除面试记录 |

## 安全设计

- **认证**：JWT（HS256），拦截器拦截 `/api/**`，放行登录、注册；支持 `Bearer` 前缀
- **token 失效控制**：`users.token_version` 版本号校验，登出 / 注销立即失效全部旧 token
- **密码安全**：BCrypt 加密存储，明文不落库、不进日志
- **暴力破解防护**：登录失败按邮箱 / IP 双维度限流
- **跨域**：CORS 白名单来自配置，生产环境必须替换为真实域名
- **响应头**：`SecurityHeadersFilter` 添加安全响应头
- **数据隔离**：简历、面试等资源校验归属用户，越权访问被拒绝
- **生产日志**：`prod` profile 关闭 SQL 打印，收敛日志级别，避免敏感数据落盘
- **网络隔离**：部署时仅 Nginx 对外，数据库 / AI 服务 / Java 后端均不对外暴露端口
- **密钥管理**：所有敏感配置走环境变量，`.env` 不提交版本控制

## 测试

后端单元测试位于 `interview_helper_backend/src/test`：

```bash
cd interview_helper_backend
mvn test
```

已覆盖：JWT 工具类、认证拦截器、登录限流等核心逻辑。

## 依赖漏洞扫描

```bash
cd interview_helper_backend
mvn dependency-check:check
```

首次执行需下载 NVD 漏洞库，耗时较长；报告生成在 `target/dependency-check-report.html`，仅当存在高危及以上漏洞（CVSS ≥ 7）时构建失败。
