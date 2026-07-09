-- ========================================
-- 补充测试数据（由 opencode 自动生成）
-- 使用方法: docker cp 此文件到容器后执行
-- ========================================

USE edu;

-- 1. 新增用户
INSERT INTO tb_user(id, username, password, real_name, role, title, email, phone, status) VALUES
(5, 'liuxiao',   'e10adc3949ba59abbe56e057f20f883e', '刘晓',   1, NULL, 'liuxiao@edu.com',   '13800000005', 1),
(6, 'chenming', 'e10adc3949ba59abbe56e057f20f883e', '陈明',   1, NULL, 'chenming@edu.com', '13800000006', 1),
(7, 'sunli',    'e10adc3949ba59abbe56e057f20f883e', '孙丽',   2, '副教授',  'sunli@edu.com',    '13900000007', 1),
(8, 'zhouqiang','e10adc3949ba59abbe56e057f20f883e', '周强',   1, NULL, 'zhouqiang@edu.com','13800000008', 1);

-- 2. 新增课程（孙丽老师开设）
INSERT INTO tb_course(id, course_name, teacher_id, description, status) VALUES
(4, 'Python程序设计', 7, 'Python语言基础、面向对象、数据分析入门', '2'),
(5, '操作系统原理',   7, '进程管理、内存管理、文件系统、I/O管理', '2');

-- 3. 新增章节
INSERT INTO tb_chapter(id, course_id, title, sort_order) VALUES
(9,  4, 'Python基础语法与数据类型', 1),
(10, 4, 'Python面向对象编程',       2),
(11, 4, 'Python文件操作与异常处理', 3),
(12, 4, 'Python数据分析入门',        4),
(13, 5, '进程与线程管理',           1),
(14, 5, '内存管理',                 2),
(15, 5, '文件系统',                 3),
(16, 5, 'I/O设备管理',             4);

-- 4. 新增知识点
INSERT INTO tb_knowledge_point(chapter_id, content, importance, sort_order) VALUES
(9,  '变量、数据类型与运算符',          'high',   1),
(9,  '列表、元组、字典与集合',          'high',   2),
(9,  '条件与循环语句',                  'high',   3),
(10, '类与对象的定义和使用',            'high',   1),
(10, '继承与多态',                      'high',   2),
(10, '装饰器与魔术方法',                'medium', 3),
(11, '文件读写操作',                    'high',   1),
(11, '异常捕获与自定义异常',            'medium', 2),
(12, 'NumPy数组基础',                   'medium', 1),
(12, 'Pandas数据处理入门',              'medium', 2),
(13, '进程与线程的概念',                'high',   1),
(13, '进程调度算法',                    'high',   2),
(13, '线程同步与互斥',                  'high',   3),
(14, '分区与分页存储管理',              'high',   1),
(14, '虚拟内存与页面置换算法',          'high',   2),
(15, '文件目录结构',                    'medium', 1),
(15, '磁盘空间管理',                    'medium', 2),
(16, 'I/O控制方式',                     'medium', 1),
(16, 'SPOOLing技术',                    'medium', 2);

-- 5. 新增作业
INSERT INTO tb_assignment(id, course_id, teacher_id, title, content, full_score, deadline, allow_submit_count) VALUES
(4, 4, 7, 'Python基础编程练习',      '编写一个学生成绩统计程序：输入多个学生的成绩，输出最高分、最低分、平均分及及格人数', 100, '2026-07-20 23:59:59', 2),
(5, 4, 7, 'Python数据分析作业',      '使用Pandas读取CSV文件，完成数据清洗、统计分析和可视化',                            100, '2026-08-01 23:59:59', 1),
(6, 5, 7, '进程调度算法模拟',        '编程实现FCFS、SJF、RR三种进程调度算法，比较其平均周转时间',                         100, '2026-07-25 23:59:59', 2),
(7, 5, 7, '内存管理作业',            '模拟分页存储管理中的地址转换过程，实现LRU页面置换算法',                             100, '2026-08-05 23:59:59', 1),
(8, 1, 2, 'Java异常处理练习',        '编写程序演示try-catch-finally的使用，自定义一个业务异常类',                        100, '2026-06-30 23:59:59', 2);

-- 6. 新增选课记录
INSERT INTO tb_course_selection(id, student_id, course_id, status, select_time) VALUES
(3, 5, 4, '1', '2026-06-20 08:30:00'),
(4, 5, 5, '1', '2026-06-21 09:00:00'),
(5, 6, 4, '1', '2026-06-22 10:00:00'),
(6, 8, 5, '1', '2026-06-23 11:00:00'),
(7, 1, 4, '1', '2026-06-24 08:00:00'),
(8, 4, 5, '1', '2026-06-25 09:30:00');

-- 7. 新增提交记录
INSERT INTO tb_submission(assignment_id, student_id, content, status, submit_time, submit_count) VALUES
(4, 5, 'def calc_score():\n    scores = list(map(int, input("请输入成绩(空格分隔): ").split()))\n    print(f"最高分: {max(scores)}, 最低分: {min(scores)}, 平均分: {sum(scores)/len(scores):.2f}")\n    pass_cnt = sum(1 for s in scores if s >= 60)\n    print(f"及格人数: {pass_cnt}, 不及格人数: {len(scores)-pass_cnt}")\n\nif __name__ == "__main__":\n    calc_score()', 1, '2026-07-15 14:30:00', 1),
(5, 6, 'import pandas as pd\ndf = pd.read_csv("students.csv")\n# 数据清洗\ndf = df.dropna()\ndf = df[df["score"] >= 0]\n# 统计分析\nprint(df.describe())\n# 可视化\nimport matplotlib.pyplot as plt\ndf["score"].hist()\nplt.savefig("score_dist.png")', 1, '2026-07-28 16:00:00', 1),
(6, 5, 'class Scheduler:\n    def fcfs(self, processes):\n        # 先来先服务\n        processes.sort(key=lambda p: p["arrival"])\n        time = 0\n        for p in processes:\n            if time < p["arrival"]:\n                time = p["arrival"]\n            time += p["burst"]\n            p["completion"] = time\n        return processes', 1, '2026-07-20 10:00:00', 1);

-- 8. 新增通知
INSERT INTO tb_notification(course_id, title, content, create_time) VALUES
(4, 'Python课程开课通知', '课程【Python程序设计】已开课，请同学们按时参加学习。', '2026-06-20 08:00:00'),
(5, '操作系统课程开课通知', '课程【操作系统原理】已开课，请同学们按时参加学习。', '2026-06-21 08:00:00'),
(4, '第一次作业发布', 'Python基础编程练习已发布，截止日期：2026-07-20 23:59:59', '2026-07-01 10:00:00');

-- 9. 新增论坛帖子
INSERT INTO tb_forum_post(course_id, user_id, title, content, status) VALUES
(4, 5, 'Python数据分析求助', '请问老师，Pandas读取CSV时中文乱码怎么解决？', 1),
(4, 6, 'NumPy数组运算问题',  '广播机制不太理解，有没有通俗的解释？', 1),
(5, 8, '进程调度问题',       'RR调度的时间片大小如何选择？', 1);

INSERT INTO tb_forum_reply(post_id, user_id, content) VALUES
(4, 7, '在read_csv()中添加encoding="utf-8"或encoding="gbk"参数即可。'),
(5, 7, '广播机制简单来说就是形状不同的数组进行运算时，NumPy会自动扩展维度较小的数组。'),
(6, 7, '时间片通常设为20-50ms，太短会导致频繁上下文切换，太长会降低响应速度。');
