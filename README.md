# Seiko Work

面向个人的工作站：接收邮件、安排每日工作与日程。

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端 | Spring Boot 3.4 + Java 21 + MyBatis-Plus 3.5 + MySQL 8 + Redis |
| 认证 | Sa-Token 1.39（UUID Token，会话存 Redis） |
| 邮件 | Jakarta Mail（IMAP 实时拉取，不入库） |
| 前端 | Next.js 15 + React 19 + TypeScript + Tailwind CSS 4 + axios |
| 文档 | springdoc-openapi 2.8（Swagger UI） |

## 项目结构

```
seiko-work-service/   # 后端（Maven，端口 1001）
seiko-work-ui/        # 前端（Next.js，开发端口 3000）
doc/                  # 项目文档
```

## 快速开始

### 环境要求

- JDK 21+、Maven 3.9+、MySQL 8.x、Redis 7.x、Node.js 18+

### 1. 初始化数据库

```bash
mysql -u root -p < seiko-work-service/src/main/resources/db/schema.sql
```

创建 4 张表：`work_user`（用户）、`work_mail`（邮箱授权配置）、`work_task`（工作事项）、`work_event`（日程事件）。

### 2. 配置环境变量

| 变量 | 说明 | 默认值 |
|------|------|--------|
| `MYSQL_PASSWORD` | MySQL 密码 | `123456` |
| `CRYPTO_KEY` | 敏感字段 AES-256-GCM 密钥 | `seikowork`（生产必须修改） |
| `MAIL_USERNAME` / `MAIL_PASSWORD` | 发件邮箱账号 / SMTP 授权码 | 空 |
| `CORS_ALLOWED_ORIGINS` | 允许跨域来源 | `http://localhost:3000` |
| `NEXT_PUBLIC_API_BASE_URL` | 前端请求的后端地址 | `http://localhost:1001` |

### 3. 启动后端

```bash
cd seiko-work-service
mvn spring-boot:run
```

接口文档：http://localhost:1001/swagger-ui.html

### 4. 启动前端

```bash
cd seiko-work-ui
npm install
npm run dev
```

访问 http://localhost:3000

## 常用命令

```bash
# 后端
mvn test                                    # 运行测试
mvn test -Dtest=CryptoUtilTest              # 运行单个测试
mvn clean package -DskipTests               # 打包 -> target/seiko-work-service.jar

# 前端
npm run dev / build / lint
```

## API 概览

统一返回格式 `{ "code": 200, "message": "...", "data": {...} }`，`code == 200` 表示成功。认证方式为请求头携带 `token: <uuid>`（登录接口返回）。

| 模块 | 路径 | 说明 |
|------|------|------|
| 认证 | `POST /auth/email/code` `/auth/email/register` `/auth/email/login` | 邮箱验证码 / 注册 / 登录 |
| 认证 | `POST /auth/phone/code` `/auth/phone/register` `/auth/phone/login` | 手机验证码（短信当前为 Mock） |
| 认证 | `POST /auth/password/code` `/auth/password/reset` `/auth/password/change` | 找回密码 / 修改密码 |
| 认证 | `GET /auth/info`、`PUT /auth/profile`、`POST /auth/logout` | 当前用户 / 改资料 / 登出 |
| 任务 | `GET/POST /api/tasks`、`GET /api/tasks/today`、`PUT/DELETE /api/tasks/{id}` | 工作事项 CRUD、今日工作 |
| 日程 | `GET/POST /api/events`、`GET /api/events/range`、`PUT/DELETE /api/events/{id}` | 日程 CRUD、时间范围查询 |
| 邮件 | `GET/POST /api/mails/account`、`GET /api/mails/account/providers` | 邮箱授权配置（自动识别服务商） |
| 邮件 | `GET /api/mails`、`GET /api/mails/{uid}`、`POST /api/mails/{uid}/read` | IMAP 实时拉取列表 / 详情 / 标记已读 |
| 节假日 | `GET /api/holidays/{year}` | 法定节假日（服务端代理第三方 API，Redis 缓存 3 天） |

注意：`/auth/**` 无前缀，其余业务接口均为 `/api` 前缀；登录、注册、验证码接口公开，其余一律要求登录。

## 前端说明

单页应用，采用 **hash 路由**（非 Next.js 文件路由）：`#` 首页、`#mail` 邮箱、`#schedule` 日程、`#login` 登录。页面以 Panel 组件形式挂载在 `app/page.tsx`，导航入口在 `components/SideNav.tsx`。API 调用统一走 `lib/axios.ts`（自动附带 token、统一错误处理与登录过期跳转）。