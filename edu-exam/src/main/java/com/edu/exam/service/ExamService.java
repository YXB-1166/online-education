package com.edu.exam.service;

import com.edu.common.entity.Exam;
import com.edu.common.entity.ExamQuestion;
import com.edu.common.entity.ExamRecord;
import com.edu.common.page.PageParam;
import com.edu.common.page.PageResult;
import com.edu.common.result.BusinessException;
import com.edu.common.service.BaseService;
import com.edu.exam.mapper.ExamMapper;
import com.edu.exam.mapper.ExamQuestionMapper;
import com.edu.exam.mapper.ExamRecordMapper;
import com.edu.exam.mapper.KnowledgePointMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class ExamService extends BaseService {

    @Autowired
    private ExamMapper examMapper;

    @Autowired
    private ExamQuestionMapper questionMapper;

    @Autowired
    private ExamRecordMapper recordMapper;

    @Autowired
    private KnowledgePointMapper knowledgePointMapper;

    private final Random random = new Random();

    public Exam findById(Long id) {
        Exam exam = examMapper.selectById(id);
        if (exam == null) throw new BusinessException("考试不存在");
        return exam;
    }

    public List<Exam> findList(Exam exam) {
        return examMapper.selectList(exam);
    }

    public PageResult<Exam> page(PageParam param, Exam exam) {
        long total = examMapper.countList(exam);
        List<Exam> list = examMapper.selectPage(exam, param.getOffset(), param.getPageSize());
        return new PageResult<>(list, total, param.getPageNum(), param.getPageSize());
    }

    @Transactional
    public void add(Exam exam) {
        if (exam.getStatus() == null) exam.setStatus(0);
        if (examMapper.insert(exam) == 0) throw new BusinessException("创建考试失败");
        log.info("创建考试: id={}, title={}", exam.getId(), exam.getTitle());
    }

    @Transactional
    public void update(Exam exam) {
        if (examMapper.update(exam) == 0) throw new BusinessException("更新考试失败");
    }

    @Transactional
    public void delete(Long id) {
        questionMapper.deleteByExamId(id);
        if (examMapper.deleteById(id) == 0) throw new BusinessException("删除考试失败");
    }

    @Transactional
    public void publish(Long id) {
        Exam exam = findById(id);
        List<ExamQuestion> questions = questionMapper.selectByExamId(id);
        if (questions.isEmpty()) throw new BusinessException("考试没有题目，无法发布");
        int total = questions.stream().mapToInt(ExamQuestion::getScore).sum();
        exam.setTotalScore(total);
        exam.setStatus(1);
        examMapper.update(exam);
        log.info("发布考试: id={}", id);
    }

    public List<ExamQuestion> getQuestions(Long examId) {
        return questionMapper.selectByExamId(examId);
    }

    @Transactional
    public void saveQuestions(Long examId, List<ExamQuestion> questions) {
        findById(examId);
        questionMapper.deleteByExamId(examId);
        for (int i = 0; i < questions.size(); i++) {
            ExamQuestion q = questions.get(i);
            q.setExamId(examId);
            q.setSortOrder(i);
            questionMapper.insert(q);
        }
    }

    @Transactional
    public List<ExamQuestion> autoGenerateQuestions(Long examId, int choiceCount, int judgeCount) {
        Exam exam = findById(examId);
        List<String> kps = knowledgePointMapper.selectByCourseId(exam.getCourseId());
        if (kps.isEmpty()) throw new BusinessException("该课程暂无知识点，请先通过课程编辑生成章节知识点");

        questionMapper.deleteByExamId(examId);
        List<ExamQuestion> generated = new ArrayList<>();

        for (int i = 0; i < choiceCount && !kps.isEmpty(); i++) {
            String kp = kps.get(i % kps.size());
            generated.add(buildChoiceQuestion(examId, kp, i, kps));
        }
        for (int i = 0; i < judgeCount && !kps.isEmpty(); i++) {
            String kp = kps.get((choiceCount + i) % kps.size());
            generated.add(buildJudgeQuestion(examId, kp, choiceCount + i));
        }
        for (ExamQuestion q : generated) {
            questionMapper.insert(q);
        }
        log.info("自动生成题目: examId={}, choice={}, judge={}", examId, choiceCount, judgeCount);
        return generated;
    }

    private ExamQuestion buildChoiceQuestion(Long examId, String kp, int idx, List<String> allKps) {
        ExamQuestion q = new ExamQuestion();
        q.setExamId(examId);
        q.setType(1);
        q.setSortOrder(idx);
        q.setScore(10);

        String[] templates = {
            "关于「%s」，以下说法正确的是？",
            "以下对「%s」的理解，正确的是？",
            "对于「%s」，下列描述准确的是？",
            "关于「%s」的说法，正确的是？"
        };
        q.setContent(String.format(templates[random.nextInt(templates.length)], kp));

        StringBuilder opts = new StringBuilder();
        List<String> distractors = new ArrayList<>(allKps);
        distractors.remove(kp);
        Collections.shuffle(distractors, random);

        String[] labels = {"A", "B", "C", "D"};
        int correctIdx = random.nextInt(4);
        String[] correctTpl = {"%s是课程的核心知识点", "%s在课程中有详细讲解", "%s是本章的重要概念", "%s需要重点掌握"};
        String[] wrongTpl = {"%s与课程内容无关", "%s不属于本课程范围", "%s在实际中很少用到", "%s的理解超出了大纲要求"};

        for (int i = 0; i < 4; i++) {
            if (i > 0) opts.append("|");
            if (i == correctIdx) {
                opts.append(labels[i]).append(":").append(String.format(correctTpl[random.nextInt(correctTpl.length)], kp));
            } else {
                String dist = i - (i > correctIdx ? 1 : 0) < distractors.size() ? distractors.get(i - (i > correctIdx ? 1 : 0)) : kp;
                opts.append(labels[i]).append(":").append(String.format(wrongTpl[random.nextInt(wrongTpl.length)], dist));
            }
        }
        q.setOptions(opts.toString());
        q.setAnswer(labels[correctIdx]);
        return q;
    }

    private ExamQuestion buildJudgeQuestion(Long examId, String kp, int idx) {
        ExamQuestion q = new ExamQuestion();
        q.setExamId(examId);
        q.setType(2);
        q.setSortOrder(idx);
        q.setScore(10);

        boolean isTrue = random.nextBoolean();
        String[] trueTpl = {"%s是课程的重要内容", "%s是本章的核心知识点之一", "理解%s对学习本章很有帮助", "%s是本课程需要掌握的知识点"};
        String[] falseTpl = {"%s不在本课程的教学范围内", "%s不是本章的重点内容", "%s与课程主题无关", "%s属于其他课程范畴"};

        if (isTrue) {
            q.setContent(String.format(trueTpl[random.nextInt(trueTpl.length)], kp));
            q.setAnswer("T");
        } else {
            q.setContent(String.format(falseTpl[random.nextInt(falseTpl.length)], kp));
            q.setAnswer("F");
        }
        return q;
    }

    @Transactional
    public ExamRecord submitExam(Long examId, Long studentId, Map<Long, String> answers) {
        Exam exam = findById(examId);
        if (exam.getStatus() != 1) throw new BusinessException("考试未发布");
        List<ExamQuestion> questions = questionMapper.selectByExamId(examId);

        int autoScore = 0;
        StringBuilder answersJson = new StringBuilder("{");
        for (ExamQuestion q : questions) {
            String userAnswer = answers.get(q.getId());
            if (userAnswer == null) userAnswer = "";
            answersJson.append("\"").append(q.getId()).append("\":\"").append(userAnswer).append("\",");

            boolean correct = userAnswer.trim().equalsIgnoreCase(q.getAnswer().trim());
            boolean shouldAutoGrade = (q.getType() == 1 && exam.getAutoGradeChoice() == 1) ||
                                      (q.getType() == 2 && exam.getAutoGradeJudge() == 1);
            if (correct && shouldAutoGrade) {
                autoScore += q.getScore();
            }
        }
        if (answersJson.length() > 1) answersJson.setLength(answersJson.length() - 1);
        answersJson.append("}");

        ExamRecord record = new ExamRecord();
        record.setExamId(examId);
        record.setStudentId(studentId);
        record.setAnswers(answersJson.toString());
        record.setAutoScore(autoScore);
        record.setScore(autoScore);
        record.setGraded(1);
        recordMapper.upsert(record);
        log.info("提交考试: examId={}, studentId={}, autoScore={}", examId, studentId, autoScore);
        return record;
    }

    public ExamRecord getRecord(Long examId, Long studentId) {
        return recordMapper.selectByExamAndStudent(examId, studentId);
    }

    public List<ExamRecord> getRecordsByExam(Long examId) {
        return recordMapper.selectByExamId(examId);
    }

    public List<ExamRecord> getRecordsByStudent(Long studentId) {
        return recordMapper.selectByStudentId(studentId);
    }
}
