# Seiko Work Service

基于 Spring Boot 3.4.0 + Java 21 的企业级后端服务脚手架。

## 技术栈

| 分类 | 技术 | 版本 | 说明 |
|------|------|------|------|
| 语言 | Java | 21 | LTS 版本 |
| 框架 | Spring Boot | 3.4.0 | 应用框架 |
| 构建 | Maven | 3.9+ | 项目构建工具 |
| 数据库 | MySQL | 8.x | 关系型数据库 |
| ORM | MyBatis-Plus | 3.5.15 | 分页、逻辑删除、自动填充 |
| 缓存 | Redis | 7.x | Spring Cache + Sa-Token 会话存储 |
| 认证 | Sa-Token | 1.39.0 | UUID Token、角色权限、限流 |
| 文档 | springdoc-openapi | 2.8.3 | OpenAPI 3.x / Swagger UI |
| 工具 | Hutool | 5.8.25 | 工具类库（含 MD5 加密） |
| 邮件 | Spring Boot Mail | 3.4.0 | 邮箱验证码发送（SMTP） |
| 校验 | Spring Validation | - | 参数校验 |
| 切面 | Spring AOP | - | 面向切面日志 |
| 简化 | Lombok | - | 样板代码简化 |

## 项目结构

```
seiko-work-service/
├── src/main/java/com/seiko/work/
│   ├── SeikoWorkApplication.java      # 启动类
│   ├── common/                         # 通用模块
│   │   ├── entity/BaseEntity.java     # 实体基类
│   │   ├── result/                     # 统一返回
│   │   ├── exception/                  # 全局异常
│   │   ├── constant/                   # 常量
│   │   └── util/                       # 工具类
│   ├── config/                         # 配置类
│   │   ├── MybatisPlusConfig.java
│   │   ├── RedisConfig.java
│   │   ├── SaTokenConfig.java
│   │   ├── OpenApiConfig.java
│   │   ├── WebMvcConfig.java
│   │   └── MyMetaObjectHandler.java
│   ├── aspect/                         # AOP 切面
│   │   └── WebLogAspect.java
│   └── module/                         # 业务模块
│       └── system/                     # 系统模块
│           ├── controller/
│           ├── service/
│           ├── mapper/
│           ├── entity/
│           ├── dto/
│           └── vo/
├── src/main/resources/
│   ├── application.yml                  # 主配置
│   ├── application-dev.yml              # 开发环境
│   ├── application-prod.yml             # 生产环境
│   ├── mapper/                          # MyBatis XML
│   └── db/schema.sql                    # 数据库脚本
└── pom.xml
```

## 快速开始

### 1. 环境要求

- JDK 21+
- Maven 3.9+
- MySQL 8.x
- Redis 7.x

### 2. 数据库初始化

```bash
mysql -u root -p < src/main/resources/db/schema.sql
```

### 3. 修改配置

编辑 `src/main/resources/application-dev.yml`，修改数据库和 Redis 连接信息。

### 4. 启动应用

```bash
mvn spring-boot:run
```

或打包后运行：

```bash
mvn clean package -DskipTests
java -jar target/seiko-work-service.jar
```

### 5. 访问接口文档

启动后访问：http://localhost:8080/api/swagger-ui.html

## 核心功能

### 统一响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

### 认证授权

- 发送邮箱验证码：`POST /api/auth/email/code`
- 邮箱注册接口：`POST /api/auth/email/register`
- 邮箱登录接口：`POST /api/auth/email/login`
- 发送手机验证码：`POST /api/auth/phone/code`
- 手机号注册接口：`POST /api/auth/phone/register`
- 手机号验证码登录：`POST /api/auth/phone/login`
- 登出接口：`POST /api/auth/logout`
- 获取当前登录用户信息：`GET /api/auth/info`
- 请求头携带 `Authorization: <token>` 进行认证

### 权限注解

- `@SaCheckLogin` - 校验登录
- `@SaCheckRole("admin")` - 校验角色
- `@SaCheckPermission("user:add")` - 校验权限
- `@SaIgnore` - 忽略认证

### 限流

```java
// 同一 key 1 分钟内最多 5 次
SaRateLimiter.check("login:" + username, 5, 60);
```

### 缓存

```java
@Cacheable(value = "user", key = "#id")
public UserVO getUserById(Long id) { ... }
```

## 默认账号

- 用户名：`admin`
- 密码：`123456`（需通过 `Md5Util.encryptWithDefaultSalt("123456")` 生成密码后写入数据库）

## License

Apache 2.0
