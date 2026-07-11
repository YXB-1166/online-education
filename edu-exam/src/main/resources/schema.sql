CREATE TABLE IF NOT EXISTS tb_exam (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id       BIGINT       NOT NULL COMMENT '所属课程ID',
    teacher_id      BIGINT       NOT NULL COMMENT '发布教师ID',
    title           VARCHAR(200) NOT NULL COMMENT '考试标题',
    duration        INT          DEFAULT 60 COMMENT '考试时长(分钟)',
    auto_grade_choice TINYINT    DEFAULT 1 COMMENT '选择题自动批改 0-否 1-是',
    auto_grade_judge  TINYINT    DEFAULT 1 COMMENT '判断题自动批改 0-否 1-是',
    total_score     INT          DEFAULT 100 COMMENT '总分',
    start_time      DATETIME     DEFAULT NULL COMMENT '开始时间',
    end_time        DATETIME     DEFAULT NULL COMMENT '结束时间',
    status          TINYINT      DEFAULT 0 COMMENT '0-草稿 1-已发布',
    create_time     DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tb_exam_question (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    exam_id         BIGINT       NOT NULL COMMENT '考试ID',
    type            TINYINT      NOT NULL COMMENT '1-选择题 2-判断题',
    content         TEXT         NOT NULL COMMENT '题目内容',
    options         VARCHAR(500) DEFAULT NULL COMMENT '选择题选项(A|B|C|D格式)',
    answer          VARCHAR(10)  NOT NULL COMMENT '正确答案',
    score           INT          DEFAULT 0 COMMENT '每题分值',
    sort_order      INT          DEFAULT 0 COMMENT '排序',
    create_time     DATETIME     DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tb_exam_record (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    exam_id         BIGINT       NOT NULL COMMENT '考试ID',
    student_id      BIGINT       NOT NULL COMMENT '学生ID',
    answers         TEXT         DEFAULT NULL COMMENT '答案JSON',
    score           INT          DEFAULT NULL COMMENT '最终得分',
    auto_score      INT          DEFAULT NULL COMMENT '自动批改得分',
    graded          TINYINT      DEFAULT 0 COMMENT '0-未批改 1-已批改',
    submit_time     DATETIME     DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_exam_stu (exam_id, student_id)
);
