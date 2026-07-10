-- ==================== 补充教学数据 ====================
SET NAMES utf8mb4;
USE edu;

-- ===== 新用户（密码均为 123456） =====
INSERT INTO tb_user(id, username, password, real_name, role, title, email, phone, status) VALUES
(10, 'madongmei',  '123456', '马冬梅',  1, NULL, 'madongmei@edu.com',  '13800000010', 1),
(11, 'tangren',    '123456', '唐仁',    1, NULL, 'tangren@edu.com',    '13800000011', 1),
(12, 'linyue',     '123456', '林月',    1, NULL, 'linyue@edu.com',     '13800000012', 1),
(13, 'zhangguoli', '123456', '张国立',  2, '教授',   'zhangguoli@edu.com','13900000013', 1),
(14, 'chendaoming','123456', '陈道明',  2, '副教授', 'chendaoming@edu.com','13900000014', 1);

-- ===== 新课程 =====
INSERT INTO tb_course(id, course_name, description, teacher_id, credit, max_students, status, homework_ratio, exam_ratio, exam_time, start_time, end_time) VALUES
(6, '计算机网络', '计算机网络体系结构、TCP/IP协议栈、网络安全基础',               13, 3, 100, '1', 50, 50, NULL,                '2026-09-01', '2027-01-01'),
(7, '软件工程',   '软件生命周期、需求分析、设计模式、项目管理',                   13, 4, 80,  '1', 50, 50, NULL,                '2026-09-01', '2027-01-01'),
(8, '人工智能基础', '机器学习基础、神经网络、深度学习入门、AI应用实践',           14, 3, 100, '2', 40, 60, '2026-08-15 14:00:00', '2026-03-01', '2026-07-01'),
(9, '编译原理',   '词法分析、语法分析、语义分析、中间代码生成、代码优化',         14, 4, 60,  '1', 50, 50, NULL,                '2026-09-01', '2027-01-01');

-- ===== 新章节 =====
INSERT INTO tb_chapter(id, course_id, title, summary, sort_order) VALUES
(17, 6, '计算机网络概述',       '计算机网络定义、分类、性能指标', 1),
(18, 6, '物理层',               '数据通信基础、传输介质、复用技术', 2),
(19, 6, '数据链路层',           '帧、差错控制、MAC协议、以太网', 3),
(20, 6, '网络层',               'IP协议、路由算法、子网划分', 4),
(21, 6, '传输层',               'TCP/UDP协议、流量控制、拥塞控制', 5),
(22, 7, '软件工程概述',         '软件危机、软件生命周期、开发模型', 1),
(23, 7, '需求分析',             '需求获取、用例建模、数据流图', 2),
(24, 7, '软件设计',             '模块化设计、UML建模、设计模式', 3),
(25, 7, '软件测试',             '测试方法、测试用例设计、单元测试', 4),
(26, 8, 'AI概述',               '人工智能历史、定义、应用领域', 1),
(27, 8, '机器学习基础',         '监督学习、无监督学习、KNN、决策树', 2),
(28, 8, '神经网络',             '感知机、多层神经网络、反向传播', 3),
(29, 8, '深度学习入门',         'CNN、RNN、PyTorch框架基础', 4),
(30, 9, '编译概述',             '编译器结构、编译过程', 1),
(31, 9, '词法分析',             '正则表达式、有限自动机、Lex', 2),
(32, 9, '语法分析',             '上下文无关文法、LL(1)、LR(1)', 3),
(33, 9, '语义分析与中间代码',   '属性文法、三地址码、符号表', 4);

-- ===== 新知识点 =====
INSERT INTO tb_knowledge_point(id, chapter_id, content, importance, sort_order) VALUES
(53, 17, '计算机网络的定义与分类', 'medium', 1),
(54, 17, '网络性能指标：带宽、时延、吞吐量', 'high', 2),
(55, 18, '数据通信基础：信号、码元、波特率', 'medium', 1),
(56, 18, '传输介质：双绞线、光纤、无线', 'medium', 2),
(57, 19, '帧结构与封装', 'high', 1),
(58, 19, '以太网MAC协议CSMA/CD', 'high', 2),
(59, 20, 'IP地址与子网划分', 'high', 1),
(60, 20, '路由算法：RIP、OSPF', 'high', 2),
(61, 21, 'TCP三次握手与四次挥手', 'high', 1),
(62, 21, '流量控制与拥塞控制机制', 'high', 2),
(63, 22, '软件危机的表现与原因', 'medium', 1),
(64, 22, '瀑布模型与敏捷开发', 'high', 2),
(65, 23, '需求获取技术：访谈、问卷、原型', 'high', 1),
(66, 23, '用例图与活动图', 'medium', 2),
(67, 24, '模块化设计原则：高内聚低耦合', 'high', 1),
(68, 24, '常用设计模式：单例、工厂、观察者', 'medium', 2),
(69, 25, '黑盒测试与白盒测试', 'high', 1),
(70, 25, 'JUnit单元测试框架', 'medium', 2),
(71, 26, '人工智能发展简史', 'low', 1),
(72, 26, 'AI主要应用领域：CV、NLP、机器人', 'medium', 2),
(73, 27, '监督学习：分类与回归', 'high', 1),
(74, 27, 'KNN算法原理与实现', 'high', 2),
(75, 27, '决策树与随机森林', 'high', 3),
(76, 28, '感知机模型与线性可分', 'high', 1),
(77, 28, '反向传播算法推导', 'high', 2),
(78, 28, '激活函数：Sigmoid、ReLU、Tanh', 'medium', 3),
(79, 29, '卷积神经网络CNN基本原理', 'high', 1),
(80, 29, 'RNN与LSTM在序列建模中的应用', 'medium', 2),
(81, 30, '编译器的结构与工作流程', 'medium', 1),
(82, 30, '编译与解释的区别', 'low', 2),
(83, 31, '正则表达式与NFA/DFA转换', 'high', 1),
(84, 31, 'Lex工具的基本使用', 'medium', 2),
(85, 32, '上下文无关文法定义', 'high', 1),
(86, 32, 'LL(1)与LR(1)分析表的构造', 'high', 2),
(87, 33, '属性文法与语法制导翻译', 'medium', 1),
(88, 33, '三地址码的生成', 'medium', 2);

-- ===== 新课选课记录 =====
INSERT INTO tb_course_selection(id, student_id, course_id, status, select_time) VALUES
(13, 1,  3, '1', '2026-06-25 08:30:00'),
(14, 4,  3, '1', '2026-06-25 09:00:00'),
(15, 5,  3, '1', '2026-06-26 10:00:00'),
(16, 8,  4, '1', '2026-06-27 11:00:00'),
(17, 10, 1, '0', '2026-06-28 08:00:00'),
(18, 10, 8, '1', '2026-06-28 09:00:00'),
(19, 10, 6, '0', '2026-06-28 10:00:00'),
(20, 11, 4, '1', '2026-06-29 08:30:00'),
(21, 11, 8, '1', '2026-06-29 09:00:00'),
(22, 12, 8, '1', '2026-06-30 08:00:00'),
(23, 12, 5, '1', '2026-06-30 09:00:00'),
(24, 1,  8, '0', '2026-07-01 08:00:00'),
(25, 10, 2, '0', '2026-07-01 09:00:00');

-- ===== 新作业 =====
INSERT INTO tb_assignment(id, course_id, teacher_id, title, content, full_score, deadline, allow_submit_count) VALUES
(9,  6, 13, '网络协议分析',     '分析TCP/IP协议栈各层协议的特点与功能，以表格形式对比各层主要协议', 100, '2026-08-15 23:59:59', 1),
(10, 7, 13, '需求分析文档',     '选择一个在线系统，编写需求规格说明书（SRS），包括功能需求和非功能需求', 100, '2026-08-20 23:59:59', 2),
(11, 8, 14, 'KNN分类器实现',    '使用Python实现K近邻分类算法，在Iris数据集上测试并计算准确率',        100, '2026-07-30 23:59:59', 2),
(12, 8, 14, '神经网络入门',     '使用PyTorch搭建一个全连接神经网络，在MNIST数据集上训练手写数字识别模型', 100, '2026-08-15 23:59:59', 1),
(13, 9, 14, '词法分析器实现',   '用C或Java实现一个简单编程语言的词法分析器，支持关键字识别、标识符、数字', 100, '2026-08-25 23:59:59', 2),
(14, 2, 2,  '图算法实践',       '编程实现Dijkstra最短路径算法和Floyd算法，并比较时间复杂度',              100, '2026-08-10 23:59:59', 1),
(15, 1, 2,  'Java集合框架',     '使用ArrayList、HashMap、HashSet完成一个学生信息管理系统',                   100, '2026-08-20 23:59:59', 1);

-- ===== 新提交记录 =====
INSERT INTO tb_submission(id, assignment_id, student_id, content, status, score, comment, submit_time, submit_count) VALUES
(10, 11, 10, 'import numpy as np\nfrom sklearn.datasets import load_iris\nfrom sklearn.model_selection import train_test_split\nfrom collections import Counter\n\ndef knn(X_train, y_train, X_test, k=3):\n    predictions = []\n    for x in X_test:\n        distances = [np.linalg.norm(x - x_train) for x_train in X_train]\n        k_indices = np.argsort(distances)[:k]\n        k_labels = [y_train[i] for i in k_indices]\n        predictions.append(Counter(k_labels).most_common(1)[0][0])\n    return np.array(predictions)\n\niris = load_iris()\nX_train, X_test, y_train, y_test = train_test_split(iris.data, iris.target, test_size=0.2)\npred = knn(X_train, y_train, X_test)\nprint(f"准确率: {np.mean(pred == y_test):.2%}")', 2, 85, '实现完整，KNN核心逻辑正确，建议增加交叉验证评估', '2026-07-20 14:30:00', 1),
(11, 11, 11, 'class KNN:\n    def __init__(self, k=3):\n        self.k = k\n    def fit(self, X, y):\n        self.X_train = X\n        self.y_train = y\n    def predict(self, X):\n        return [self._predict(x) for x in X]\n    def _predict(self, x):\n        distances = [np.linalg.norm(x - x_train) for x_train in self.X_train]\n        k_indices = np.argsort(distances)[:self.k]\n        k_labels = [self.y_train[i] for i in k_indices]\n        return Counter(k_labels).most_common(1)[0][0]', 1, NULL, NULL, '2026-07-19 16:00:00', 1),
(12, 11, 12, 'from sklearn.neighbors import KNeighborsClassifier\n# 直接用sklearn实现\nknn = KNeighborsClassifier(n_neighbors=3)\nknn.fit(X_train, y_train)\nprint(f"准确率: {knn.score(X_test, y_test):.2%}")', 1, NULL, NULL, '2026-07-21 10:00:00', 1),
(13, 4,  10, 'def calc_score():\n    scores = list(map(int, input("请输入成绩: ").split()))\n    print(f"最高分: {max(scores)}, 最低分: {min(scores)}, 平均分: {sum(scores)/len(scores):.2f}")', 1, NULL, NULL, '2026-07-15 09:30:00', 1),
(14, 6,  10, 'class Scheduler:\n    def fcfs(self, processes):\n        processes.sort(key=lambda p: p["arrival"])\n        time = 0\n        for p in processes:\n            time = max(time, p["arrival"]) + p["burst"]\n            p["completion"] = time\n        return processes\n    def sjf(self, processes):\n        # 短作业优先实现\n        ...', 1, NULL, NULL, '2026-07-21 15:00:00', 1);

-- ===== 新课程资料 =====
INSERT INTO tb_material(id, course_id, teacher_id, title, content, required_seconds) VALUES
(4, 4, 7, 'Python数据分析实战', '数据分析流程：数据采集、数据清洗、数据探索、建模与可视化。\n\n常用库：\n1. NumPy：数值计算基础\n2. Pandas：结构化数据处理\n3. Matplotlib/Seaborn：数据可视化\n4. Scikit-learn：机器学习', 180),
(5, 5, 7, '进程管理详解', '进程是程序的一次执行过程，是系统资源分配的基本单位。\n\n进程状态：\n- 就绪（Ready）\n- 运行（Running）\n- 阻塞（Blocked）\n\n进程调度算法：FCFS、SJF、RR、优先级调度、多级反馈队列。', 150),
(6, 8, 14, '机器学习概述讲义', '机器学习是AI的核心分支，通过数据驱动的方式让计算机自动学习规律。\n\n主要类别：\n1. 监督学习：有标签数据，分类与回归\n2. 无监督学习：无标签数据，聚类与降维\n3. 强化学习：通过与环境交互学习\n\n评估指标：准确率、精确率、召回率、F1-score', 200),
(7, 8, 14, '深度学习入门讲义', '深度学习基于多层神经网络，是当前AI领域最热门的技术方向。\n\n主要网络结构：\n1. CNN：卷积神经网络，擅长图像处理\n2. RNN：循环神经网络，擅长序列数据\n3. Transformer：注意力机制，NLP领域主流\n\n框架：PyTorch、TensorFlow、PaddlePaddle', 180),
(8, 9, 14, '词法分析讲义', '词法分析是编译的第一阶段，将源代码转换为Token序列。\n\n核心概念：\n1. 正则表达式描述词法规则\n2. NFA与DFA：有限自动机理论\n3. 从NFA到DFA的子集构造法\n4. DFA的最小化\n\n工具：Lex / Flex 自动生成词法分析器', 160),
(9, 6, 13, 'TCP/IP协议栈讲义', 'TCP/IP协议栈是互联网的核心协议体系。\n\n四层结构：\n1. 应用层：HTTP、FTP、SMTP、DNS\n2. 传输层：TCP（可靠）、UDP（不可靠）\n3. 网络层：IP、ICMP、ARP\n4. 网络接口层：以太网、WiFi\n\n重点：TCP三次握手、四次挥手、滑动窗口', 200);

-- ===== 新资料已读记录 =====
INSERT INTO tb_material_read(id, material_id, student_id, read_time, completed, completed_time) VALUES
(8, 6, 10, '2026-06-28 10:00:00', 1, '2026-06-28 10:04:00'),
(9, 7, 10, '2026-06-29 14:00:00', 0, NULL),
(10, 6, 11, '2026-06-30 09:00:00', 1, '2026-06-30 09:03:30'),
(11, 7, 11, '2026-07-01 10:00:00', 0, NULL),
(12, 6, 12, '2026-07-02 08:30:00', 1, '2026-07-02 08:33:00'),
(13, 7, 12, '2026-07-03 15:00:00', 0, NULL);

-- ===== 新通知 =====
INSERT INTO tb_notification(id, course_id, title, content, create_time) VALUES
(9,  4, 'Python课程第二次作业发布', '数据分析作业已发布，截止日期：2026-08-01 23:59:59', '2026-07-15 10:00:00'),
(10, 4, 'Python课程考试通知', 'Python程序设计课程考试时间已确定为 2026-08-20 14:00-16:00', '2026-07-20 09:00:00'),
(11, 5, '操作系统第二次作业发布', '内存管理作业已发布，截止日期：2026-08-05 23:59:59', '2026-07-18 10:00:00'),
(12, 6, '新课程开课通知', '课程【计算机网络】已创建，即将开放选课', '2026-07-10 08:00:00'),
(13, 7, '新课程开课通知', '课程【软件工程】已创建，即将开放选课', '2026-07-10 08:00:00'),
(14, 8, 'AI课程开课通知', '课程【人工智能基础】已开课，请同学们按时参加学习', '2026-07-10 08:00:00'),
(15, 8, 'AI课程第一次作业发布', 'KNN分类器实现作业已发布，截止日期：2026-07-30 23:59:59', '2026-07-12 10:00:00');

-- ===== 新通知已读记录 =====
INSERT INTO tb_notification_read(id, notification_id, student_id, read_time) VALUES
(7,  9,  5, '2026-07-15 10:30:00'),
(8,  10, 5, '2026-07-20 09:30:00'),
(9,  11, 5, '2026-07-18 11:00:00'),
(10, 9,  6, '2026-07-15 11:00:00'),
(11, 10, 6, '2026-07-20 09:30:00'),
(12, 11, 6, '2026-07-18 11:00:00'),
(13, 14, 10, '2026-07-10 08:30:00'),
(14, 15, 10, '2026-07-12 10:30:00'),
(15, 14, 11, '2026-07-10 08:30:00'),
(16, 15, 11, '2026-07-12 11:00:00'),
(17, 14, 12, '2026-07-10 09:00:00'),
(18, 15, 12, '2026-07-12 10:30:00');

-- ===== 新论坛帖子 =====
INSERT INTO tb_forum_post(id, course_id, user_id, title, content, status, create_time) VALUES
(7,  1,  1, '为什么Java8要引入Lambda表达式？', '感觉匿名内部类也能用，Lambda的优势在哪里？', 1, '2026-07-05 10:00:00'),
(8,  4,  5, 'Pandas读取大文件速度慢怎么办？', '一个200MB的CSV文件，read_csv要加载很久，有什么优化的方法？', 1, '2026-07-10 14:00:00'),
(9,  4,  6, 'Python装饰器到底怎么用？', '看到很多代码用@staticmethod、@property，不太理解装饰器的原理', 1, '2026-07-12 16:00:00'),
(10, 5,  5, 'LRU页面置换算法实现思路', '老师课堂上讲的LRU算法，用LinkedHashMap是不是最简单？', 1, '2026-07-18 09:00:00'),
(11, 8,  10, 'KNN的K值怎么选？', '在做KNN实验时，发现不同的K值准确率差别很大，如何确定最优K值？', 1, '2026-07-20 15:00:00'),
(12, 2,  1, '图的邻接矩阵和邻接表哪个好？', '两种存储方式各有什么优缺点？在实际应用中怎么选？', 1, '2026-07-22 10:00:00');

-- ===== 新论坛回复 =====
INSERT INTO tb_forum_reply(id, post_id, user_id, content, create_time) VALUES
(7,  7,  2, 'Lambda表达式让代码更简洁，尤其是配合Stream API进行集合操作时，可读性大大提升。而且Lambda表达式是惰性求值的，在某些场景下性能更好。', '2026-07-05 14:00:00'),
(8,  7,  1, '明白了，谢谢老师！那我开始学习Stream API。', '2026-07-05 15:00:00'),
(9,  8,  7, '可以使用chunksize参数分批读取，或者使用dtype参数指定列类型减少内存占用。如果数据量实在太大，建议使用Dask或Polars库。', '2026-07-10 16:00:00'),
(10, 9,  7, '装饰器本质上是一个返回函数的高阶函数，@decorator相当于 func = decorator(func)。理解了这个原理，你就能自己写装饰器了。', '2026-07-12 17:00:00'),
(11, 11, 14, '常用方法：1. 交叉验证选择使准确率最高的K值；2. 从K=1开始尝试，一般建议K不超过样本数的平方根；3. 可以绘制K值与误差关系图来辅助选择。', '2026-07-21 09:00:00'),
(12, 12, 2, '邻接矩阵适合稠密图，判断两点间是否有边为O(1)；邻接表适合稀疏图，节省空间。对于大多数实际应用（稀疏图），邻接表更常用。', '2026-07-22 14:00:00');

-- ===== 补交机会 =====
INSERT INTO tb_resubmit_opportunity(id, assignment_id, student_id, deadline, created_at) VALUES
(1, 11, 12, '2026-08-05 23:59:59', '2026-07-30 10:00:00'),
(2, 4,  10, '2026-07-28 23:59:59', '2026-07-20 10:00:00');

-- ===== 同步已选人数 =====
UPDATE tb_course SET enrolled_count = (SELECT COUNT(*) FROM tb_course_selection WHERE course_id = tb_course.id AND status = '1');
