-- ==================== 在线教育辅助教学系统 数据库初始化 ====================
-- 数据库：edu

CREATE DATABASE IF NOT EXISTS edu DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE edu;

-- 用户表
DROP TABLE IF EXISTS tb_user;
CREATE TABLE tb_user (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE COMMENT '用户名',
    password    VARCHAR(100) NOT NULL COMMENT '密码',
    real_name   VARCHAR(50)  DEFAULT NULL COMMENT '真实姓名',
    role        TINYINT      NOT NULL DEFAULT 1 COMMENT '1-学生 2-教师 3-管理员',
    email       VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    phone       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    title       VARCHAR(50)  DEFAULT NULL COMMENT '职称 如教授/副教授/讲师',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '0-禁用 1-启用',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 课程表
DROP TABLE IF EXISTS tb_course;
CREATE TABLE tb_course (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_name     VARCHAR(100) NOT NULL COMMENT '课程名称',
    description     TEXT         DEFAULT NULL COMMENT '课程简介',
    teacher_id      BIGINT       NOT NULL COMMENT '授课教师ID',
    credit          INT          DEFAULT 0 COMMENT '学分',
    max_students    INT          DEFAULT 100 COMMENT '最大选课人数',
    enrolled_count  INT          DEFAULT 0 COMMENT '已选人数',
    status          VARCHAR(2)   DEFAULT '1' COMMENT '0-待审核 1-即将开课 2-授课中 3-已结课 4-审核不通过',
    homework_ratio  INT          DEFAULT 50 COMMENT '平时作业占比%',
    exam_ratio      INT          DEFAULT 50 COMMENT '期末考试占比%',
    exam_time       DATETIME     DEFAULT NULL COMMENT '考试时间',
    start_time      DATETIME     DEFAULT NULL COMMENT '开课时间',
    end_time        DATETIME     DEFAULT NULL COMMENT '结课时间',
    create_time     DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';

-- 通知表
DROP TABLE IF EXISTS tb_notification;
CREATE TABLE tb_notification (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '通知ID',
    course_id   BIGINT       NOT NULL COMMENT '所属课程ID',
    title       VARCHAR(200) NOT NULL COMMENT '通知标题',
    content     TEXT         DEFAULT NULL COMMENT '通知内容',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知表';

-- 通知已读记录表
DROP TABLE IF EXISTS tb_notification_read;
CREATE TABLE tb_notification_read (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '已读记录ID',
    notification_id BIGINT NOT NULL COMMENT '通知ID',
    student_id      BIGINT NOT NULL COMMENT '学生ID',
    read_time       DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '已读时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知已读记录表';

-- 课程资料表
DROP TABLE IF EXISTS tb_material;
CREATE TABLE tb_material (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '资料ID',
    course_id       BIGINT       NOT NULL COMMENT '所属课程ID',
    teacher_id      BIGINT       NOT NULL COMMENT '发布教师ID',
    title           VARCHAR(200) NOT NULL COMMENT '资料标题',
    content         TEXT         DEFAULT NULL COMMENT '资料内容',
    required_seconds INT         DEFAULT 60 COMMENT '最低阅读时长(秒)',
    create_time     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程资料表';

-- 资料已读记录表
DROP TABLE IF EXISTS tb_material_read;
CREATE TABLE tb_material_read (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    material_id     BIGINT NOT NULL COMMENT '资料ID',
    student_id      BIGINT NOT NULL COMMENT '学生ID',
    read_time       DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '开始阅读时间',
    completed       TINYINT  DEFAULT 0 COMMENT '0-未完成 1-已完成',
    completed_time  DATETIME DEFAULT NULL COMMENT '完成阅读时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资料已读记录表';

-- 选课记录表
DROP TABLE IF EXISTS tb_course_selection;
CREATE TABLE tb_course_selection (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id  BIGINT   NOT NULL COMMENT '学生ID',
    course_id   BIGINT   NOT NULL COMMENT '课程ID',
    score       INT      DEFAULT NULL COMMENT '总评成绩',
    status      VARCHAR(2) DEFAULT '0' COMMENT '0-待审核 1-已选课 2-已退选 3-已结课 4-审核不通过',
    select_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '选课时间',
    drop_time   DATETIME DEFAULT NULL COMMENT '退选时间',
    UNIQUE KEY uk_stu_course (student_id, course_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='选课记录表';

-- 作业表
DROP TABLE IF EXISTS tb_assignment;
CREATE TABLE tb_assignment (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id   BIGINT       NOT NULL COMMENT '所属课程ID',
    teacher_id  BIGINT       NOT NULL COMMENT '发布教师ID',
    title       VARCHAR(200) NOT NULL COMMENT '作业标题',
    content     TEXT         DEFAULT NULL COMMENT '作业内容',
    full_score  INT          DEFAULT 100 COMMENT '满分',
    deadline    DATETIME     DEFAULT NULL COMMENT '截止时间',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作业表';

-- 作业提交表
DROP TABLE IF EXISTS tb_submission;
CREATE TABLE tb_submission (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    assignment_id   BIGINT   NOT NULL COMMENT '作业ID',
    student_id      BIGINT   NOT NULL COMMENT '学生ID',
    content         TEXT     DEFAULT NULL COMMENT '提交内容',
    attachment_url  VARCHAR(500) DEFAULT NULL COMMENT '附件地址',
    score           INT      DEFAULT NULL COMMENT '得分',
    comment         VARCHAR(500) DEFAULT NULL COMMENT '教师评语',
    status          TINYINT  DEFAULT 0 COMMENT '0-未提交 1-已提交 2-已批改',
    submit_time     DATETIME DEFAULT NULL COMMENT '提交时间',
    grade_time      DATETIME DEFAULT NULL COMMENT '批改时间',
    UNIQUE KEY uk_assign_stu (assignment_id, student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作业提交表';

-- 论坛帖子表
DROP TABLE IF EXISTS tb_forum_post;
CREATE TABLE tb_forum_post (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id   BIGINT       NOT NULL COMMENT '所属课程ID',
    user_id     BIGINT       NOT NULL COMMENT '发帖人ID',
    title       VARCHAR(200) NOT NULL COMMENT '帖子标题',
    content     TEXT         DEFAULT NULL COMMENT '帖子内容',
    reply_count INT          DEFAULT 0 COMMENT '回复数',
    status      TINYINT      DEFAULT 1 COMMENT '0-待审核 1-已发布 2-已置顶',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='论坛帖子表';

-- 章节表
DROP TABLE IF EXISTS tb_chapter;
CREATE TABLE tb_chapter (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id   BIGINT       NOT NULL COMMENT '所属课程ID',
    title       VARCHAR(200) NOT NULL COMMENT '章节标题',
    summary     TEXT         DEFAULT NULL COMMENT '章节摘要',
    sort_order  INT          DEFAULT 0 COMMENT '排序',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_course (course_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='章节表';

-- 知识点表
DROP TABLE IF EXISTS tb_knowledge_point;
CREATE TABLE tb_knowledge_point (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    chapter_id  BIGINT       NOT NULL COMMENT '所属章节ID',
    content     VARCHAR(500) NOT NULL COMMENT '知识点内容',
    importance  VARCHAR(10)  DEFAULT 'medium' COMMENT '重要性: high/medium/low',
    sort_order  INT          DEFAULT 0 COMMENT '排序',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_chapter (chapter_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识点表';

-- ==================== 测试数据 ====================

-- 用户（密码均为 123456）
INSERT INTO tb_user(username, password, real_name, role, email, phone, title, status) VALUES
('zhangsan', '123456', '张三', 1, NULL, NULL, NULL, 1),
('lisi',     '123456', '李四', 2, 'lisi@edu.cn', '13800138000', '教授', 1),
('wangwu',   '123456', '王五', 3, NULL, NULL, NULL, 1),
('zhaosi',   '123456', '赵四', 1, NULL, NULL, NULL, 1);

-- 课程
INSERT INTO tb_course(course_name, description, teacher_id, credit, max_students, status, homework_ratio, exam_ratio, exam_time, start_time, end_time) VALUES
('Java程序设计',   'Java基础与面向对象编程',     2, 4, 100, '2', 40, 60, '2026-06-20 14:00:00', '2026-03-01', '2026-07-01'),
('数据结构',       '常见数据结构与算法',           2, 3, 80,  '2', 50, 50, '2026-06-25 14:00:00', '2026-03-01', '2026-07-01'),
('数据库原理',     '关系数据库与SQL语言',          2, 3, 80,  '1', 50, 50, NULL,                '2026-09-01', '2027-01-01');

-- 选课
INSERT INTO tb_course_selection(student_id, course_id, status) VALUES
(1, 1, '1'),
(1, 2, '1');

-- 章节（Java程序设计）
INSERT INTO tb_chapter(course_id, title, summary, sort_order) VALUES
(1, 'Java语言基础',    'Java发展史、环境搭建、基本语法、数据类型、运算符、流程控制', 1),
(1, '面向对象编程',    '类与对象、封装、继承、多态、接口、抽象类',                  2),
(1, '异常处理与常用类', '异常机制、String类、集合框架初步',                          3),
(1, 'Java输入输出',    'File类、字节流与字符流、缓冲流',                            4);

-- 章节（数据结构）
INSERT INTO tb_chapter(course_id, title, summary, sort_order) VALUES
(2, '线性表',    '顺序表、链表、栈、队列的定义、存储结构与基本操作', 1),
(2, '树与二叉树', '树的定义、二叉树遍历、哈夫曼树',                 2),
(2, '图',        '图的存储、遍历、最小生成树、最短路径',            3),
(2, '查找与排序', '二分查找、哈希表、冒泡排序、快速排序、归并排序', 4);

-- 知识点（Java程序设计-第1章）
INSERT INTO tb_knowledge_point(chapter_id, content, importance, sort_order) VALUES
(1, 'Java发展历史与语言特点', 'low', 1),
(1, 'JDK安装与环境变量配置', 'medium', 2),
(1, '基本数据类型与类型转换', 'high', 3),
(1, '运算符：算术、关系、逻辑、位运算', 'high', 4),
(1, '流程控制语句：if、switch、for、while', 'high', 5);
-- 知识点（Java程序设计-第2章）
INSERT INTO tb_knowledge_point(chapter_id, content, importance, sort_order) VALUES
(2, '类与对象的定义和创建', 'high', 1),
(2, '封装：private/getter/setter', 'high', 2),
(2, '继承：extends、super、方法重写', 'high', 3),
(2, '多态：编译时与运行时多态', 'high', 4),
(2, '接口与抽象类的区别', 'medium', 5);
-- 知识点（Java程序设计-第3章）
INSERT INTO tb_knowledge_point(chapter_id, content, importance, sort_order) VALUES
(3, '异常处理机制：try-catch-finally', 'high', 1),
(3, '自定义异常', 'medium', 2),
(3, 'String/StringBuilder/StringBuffer', 'high', 3),
(3, 'ArrayList与HashMap基本使用', 'medium', 4);
-- 知识点（Java程序设计-第4章）
INSERT INTO tb_knowledge_point(chapter_id, content, importance, sort_order) VALUES
(4, 'File类常用操作', 'medium', 1),
(4, '字节流：FileInputStream/FileOutputStream', 'high', 2),
(4, '字符流：FileReader/FileWriter', 'high', 3),
(4, '缓冲流：BufferedReader/BufferedWriter', 'medium', 4);

-- 知识点（数据结构-第1章）
INSERT INTO tb_knowledge_point(chapter_id, content, importance, sort_order) VALUES
(5, '顺序表的插入与删除操作', 'high', 1),
(5, '单链表的建立与基本操作', 'high', 2),
(5, '栈的先进后出特性与应用', 'high', 3),
(5, '队列的先进先出特性与应用', 'high', 4);
-- 知识点（数据结构-第2章）
INSERT INTO tb_knowledge_point(chapter_id, content, importance, sort_order) VALUES
(6, '二叉树的性质与存储结构', 'high', 1),
(6, '前序/中序/后序遍历', 'high', 2),
(6, '哈夫曼树与哈夫曼编码', 'medium', 3);
-- 知识点（数据结构-第3章）
INSERT INTO tb_knowledge_point(chapter_id, content, importance, sort_order) VALUES
(7, '图的邻接矩阵与邻接表存储', 'high', 1),
(7, '深度优先与广度优先遍历', 'high', 2),
(7, 'Prim与Kruskal最小生成树算法', 'medium', 3),
(7, 'Dijkstra最短路径算法', 'medium', 4);
-- 知识点（数据结构-第4章）
INSERT INTO tb_knowledge_point(chapter_id, content, importance, sort_order) VALUES
(8, '二分查找算法', 'high', 1),
(8, '哈希表原理与冲突处理', 'medium', 2),
(8, '冒泡排序与快速排序', 'high', 3),
(8, '归并排序算法', 'medium', 4);

-- 论坛回复表
DROP TABLE IF EXISTS tb_forum_reply;
CREATE TABLE tb_forum_reply (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id     BIGINT   NOT NULL COMMENT '帖子ID',
    user_id     BIGINT   NOT NULL COMMENT '回复人ID',
    content     TEXT     NOT NULL COMMENT '回复内容',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='论坛回复表';

-- 作业与提交
INSERT INTO tb_assignment(course_id, teacher_id, title, content, full_score, deadline) VALUES
(1, 2, 'Java基础练习',    '编写一个Java程序实现学生成绩管理系统', 100, '2026-04-15 23:59:59'),
(1, 2, '面向对象编程作业',  '设计一个银行账户系统，使用继承与多态', 100, '2026-05-15 23:59:59'),
(2, 2, '线性表练习',      '实现顺序表和链表的插入、删除操作',    100, '2026-04-20 23:59:59');

INSERT INTO tb_submission(assignment_id, student_id, content, status, submit_time) VALUES
(1, 1, 'public class StudentManager { private String name; public StudentManager(String name) { this.name = name; } public static void main(String[] args) { System.out.println(\"Hello\"); } }', 1, '2026-04-10 10:30:00');

-- 论坛帖子
INSERT INTO tb_forum_post(course_id, user_id, title, content, status) VALUES
(1, 1, 'Java作业求助',     '请问老师，封装和继承的区别是什么？',     1),
(1, 1, '多态概念不理解',    '运行时多态和编译时多态有什么区别？',   1),
(2, 1, '二叉树遍历问题',    '前序、中序、后序遍历的递归实现怎么写？', 1);

INSERT INTO tb_forum_reply(post_id, user_id, content) VALUES
(1, 2, '封装是隐藏内部实现细节，继承是子类获取父类的属性和方法。'),
(1, 4, '补充一下：封装通过private实现，继承通过extends实现。'),
(3, 2, '前序：根左右；中序：左根右；后序：左右根。递归实现很简单。');

-- 通知（模拟教师开课、设置考试时间时自动生成的通知）
INSERT INTO tb_notification(course_id, title, content, create_time) VALUES
(1, '课程开课通知', '课程【Java程序设计】已开课，请同学们按时参加学习。', '2026-03-01 08:00:00'),
(1, '考试时间通知', '课程【Java程序设计】考试时间已确定为 2026-06-20 14:00。', '2026-06-01 10:00:00'),
(2, '课程开课通知', '课程【数据结构】已开课，请同学们按时参加学习。', '2026-03-01 08:00:00'),
(2, '考试时间通知', '课程【数据结构】考试时间已确定为 2026-06-25 14:00。', '2026-06-01 10:00:00');

-- 通知已读记录
INSERT INTO tb_notification_read(notification_id, student_id, read_time) VALUES
(1, 1, '2026-03-01 08:30:00'),
(2, 1, '2026-06-01 10:30:00'),
(3, 1, '2026-03-01 08:30:00'),
(4, 1, '2026-06-01 10:30:00');

-- 课程资料样例
INSERT INTO tb_material(course_id, teacher_id, title, content, required_seconds) VALUES
(1, 2, 'Java第一章讲义', 'Java是一种面向对象的编程语言，由Sun Microsystems公司于1995年推出。\n\n特点：\n1. 跨平台性\n2. 面向对象\n3. 安全性\n4. 多线程\n\n基本数据类型包括：int、double、boolean、char等。', 120),
(1, 2, 'Java第二章讲义', '面向对象编程三大特征：封装、继承、多态。\n\n封装：将数据和行为包装在一起，对外隐藏实现细节。\n继承：子类继承父类的属性和方法，实现代码复用。\n多态：同一方法在不同对象上有不同表现。', 180);

-- 资料已读记录
INSERT INTO tb_material_read(material_id, student_id, read_time, completed, completed_time) VALUES
(1, 1, '2026-04-01 10:00:00', 1, '2026-04-01 10:02:30'),
(2, 1, '2026-04-05 14:00:00', 0, NULL);

-- 同步已选人数
UPDATE tb_course SET enrolled_count = (SELECT COUNT(*) FROM tb_course_selection WHERE course_id = tb_course.id AND status = '1');
