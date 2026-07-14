# Online Education System — 在线教育辅助教学系统

基于 Spring Cloud Alibaba 微服务架构的在线教育平台，支持多角色（学生/教师/管理员）的课程管理、在线考试、作业批改、论坛互动等功能。

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端 | Spring Boot 3.2.0 + Spring Cloud 2023.0.0 + MyBatis 3.0.3 |
| 服务发现 | Nacos 2.3.0（端口 **18848**） |
| 网关 | Spring Cloud Gateway（响应式） |
| 数据库 | MySQL 8.0（Docker 容器 `mysql-edu`，端口 3306） |
| 鉴权 | JJWT 0.12.3（HMAC-SHA，24h 过期） |
| API 文档 | Knife4j 4.5.0（OpenAPI 3） |
| 前端 | Vue 3.4 + Vite 5 + Element Plus + Pinia |
| JDK | 21 |

## 项目结构

```
online-education/
├── pom.xml                        # 父 POM（多模块）
├── sql/                           # 数据库初始化脚本 + 备份
│   ├── init.sql                   # 建表 + 种子数据
│   └── backup/                    # 自动备份脚本（PowerShell + 计划任务）
├── edu-common/                    # 公共模块（实体、JWT、过滤器、异常处理）
├── edu-user/                      # 用户服务（端口 8010）
├── edu-course/                    # 课程服务（端口 8020）
├── edu-exam/                      # 考试/作业服务（端口 8030）
├── edu-gateway/                   # API 网关（端口 8000）
├── frontend/                      # Vue 3 前端（端口 3000）
├── docs/                          # 开发日志
└── uploads/                       # 文件上传存储
```

## 微服务端口

| 服务 | 端口 | 说明 |
|------|------|------|
| edu-gateway | 8000 | API 网关（统一入口） |
| edu-user | 8010 | 用户服务 |
| edu-course | 8020 | 课程服务 |
| edu-exam | 8030 | 考试/作业服务 |
| Nacos | 18848 | 注册中心（默认 8848，本系统改为 18848） |
| 前端 | 3000 | Vue 3 开发服务器 |
| MySQL | 3306 | 数据库 |

## 快速启动

### 1. 启动 MySQL（Docker）

```bash
docker run -d --name mysql-edu -p 3306:3306 -e MYSQL_ROOT_PASSWORD=123456 mysql:8.0 --character-set-server=utf8mb4
# 初始化数据
docker cp sql/init.sql mysql-edu:/tmp/
docker exec mysql-edu mysql -uroot -p123456 --default-character-set=utf8mb4 -e "source /tmp/init.sql"
```

### 2. 启动 Nacos

修改 `conf/application.properties` 中 `server.port=18848`，然后：

```bash
cd D:\ChineseSoftWork\nacos-server-2.3.0\nacos
startup.cmd -m standalone
```

确认：访问 http://127.0.0.1:18848/nacos/

### 3. 构建后端

```bash
mvn clean package -DskipTests
```

### 4. 启动后端（按顺序）

```bash
java -jar edu-gateway/target/edu-gateway-1.0-SNAPSHOT.jar
java -jar edu-user/target/edu-user-1.0-SNAPSHOT.jar
java -jar edu-course/target/edu-course-1.0-SNAPSHOT.jar
java -jar edu-exam/target/edu-exam-1.0-SNAPSHOT.jar
```

### 5. 启动前端

```bash
cd frontend
npm install
npm run dev      # → http://localhost:3000
```

## 测试账号

所有账号密码均为 `123456`

| 用户名 | 角色 | 姓名 |
|--------|------|------|
| zhangsan | 学生 | 张三 |
| zhaosi | 学生 | 赵四 |
| liuxiao | 学生 | 刘小 |
| chenming | 学生 | 陈明 |
| zhouqiang | 学生 | 周强 |
| student_wu | 学生 | 吴学生 |
| lisi | 教师 | 李四（教授） |
| sunli | 教师 | 孙丽（副教授） |
| teacher_zhao | 教师 | 赵老师 |
| wangwu | 管理员 | 王五 |

## 主要功能

- **用户管理** — 登录注册、JWT 鉴权、多角色权限控制
- **课程管理** — 课程 CRUD、章节知识点、选课审批流
- **在线考试** — 创建考试、自动生成题目（选择/判断）、自动批改
- **作业系统** — 布置作业、提交作业（含附件）、教师批改评分
- **论坛互动** — 课程内帖子/回复、点赞功能
- **通知公告** — 课程通知推送、已读追踪
- **学习进度** — 成绩汇总、风险预警
- **智能助手** — 基于关键词的上下文问答（课程推荐、成绩查询等）

## 前端路由概览

| 角色 | 页面 |
|------|------|
| 学生 | 课程广场、我的课程、课程详情、提交作业、我的作业、课程论坛、在线考试、学习进度、个人信息 |
| 教师 | 我的课程、创建课程、作业管理、创建考试、批改作业、论坛管理、选课审批、学生名单、个人信息 |
| 管理员 | 用户管理、课程审核、数据概览 |

## API 文档

各服务启动后可通过 Knife4j 查看 API：

- Gateway: http://127.0.0.1:8000/doc.html
- User: http://127.0.0.1:8010/doc.html
- Course: http://127.0.0.1:8020/doc.html
- Exam: http://127.0.0.1:8030/doc.html
