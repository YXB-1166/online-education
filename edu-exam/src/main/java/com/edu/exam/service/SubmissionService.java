package com.edu.exam.service;

import com.edu.common.entity.Assignment;
import com.edu.common.entity.ResubmitOpportunity;
import com.edu.common.entity.Submission;
import com.edu.common.page.PageParam;
import com.edu.common.page.PageResult;
import com.edu.common.result.BusinessException;
import com.edu.common.service.BaseService;
import com.edu.exam.mapper.AssignmentMapper;
import com.edu.exam.mapper.ResubmitOpportunityMapper;
import com.edu.exam.mapper.SubmissionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class SubmissionService extends BaseService {

    @Autowired
    private SubmissionMapper submissionMapper;

    @Autowired
    private AssignmentMapper assignmentMapper;

    @Autowired
    private ResubmitOpportunityMapper resubmitMapper;

    public Submission findById(Long id) {
        Submission submission = submissionMapper.selectById(id);
        if (submission == null) {
            log.warn("提交记录不存在: id={}", id);
            throw new BusinessException("提交记录不存在");
        }
        return submission;
    }

    public List<Submission> findList(Submission submission) {
        return submissionMapper.selectList(submission);
    }

    public PageResult<Submission> page(PageParam param, Submission submission) {
        long total = submissionMapper.countList(submission);
        List<Submission> list = submissionMapper.selectPage(submission, param.getOffset(), param.getPageSize());
        log.info("提交分页: total={}, page={}/{}", total, param.getPageNum(), param.getPageSize());
        return new PageResult<>(list, total, param.getPageNum(), param.getPageSize());
    }

    public void add(Submission submission) {
        Assignment assignment = assignmentMapper.selectById(submission.getAssignmentId());
        if (assignment == null) {
            throw new BusinessException("作业不存在");
        }
        Submission existing = null;
        List<Submission> list = submissionMapper.selectList(submission);
        if (list != null && !list.isEmpty()) {
            existing = list.get(0);
        }
        int submitCount = existing != null ? existing.getSubmitCount() != null ? existing.getSubmitCount() : 0 : 0;
        int maxCount = assignment.getAllowSubmitCount() != null ? assignment.getAllowSubmitCount() : 1;
        LocalDateTime now = LocalDateTime.now();
        boolean deadlinePassed = assignment.getDeadline() != null && now.isAfter(assignment.getDeadline());
        boolean hasOpportunity = false;
        if (deadlinePassed) {
            ResubmitOpportunity opp = resubmitMapper.selectByAssignmentAndStudent(
                    submission.getAssignmentId(), submission.getStudentId());
            if (opp != null && opp.getDeadline() != null && now.isBefore(opp.getDeadline())) {
                hasOpportunity = true;
            }
        }
        if (deadlinePassed && !hasOpportunity) {
            throw new BusinessException("已超过截止时间，无法提交");
        }
        if (existing != null && !deadlinePassed && submitCount >= maxCount) {
            throw new BusinessException("已达到最大提交次数");
        }
        submission.setSubmitTime(now);
        submission.setStatus(1);
        if (submissionMapper.insert(submission) == 0) {
            log.warn("提交作业失败: assignmentId={}, studentId={}", submission.getAssignmentId(), submission.getStudentId());
            throw new BusinessException("提交作业失败");
        }
        log.info("提交作业: id={}, assignmentId={}", submission.getId(), submission.getAssignmentId());
    }

    public void update(Submission submission) {
        if (submissionMapper.update(submission) == 0) {
            log.warn("更新提交失败: id={}", submission.getId());
            throw new BusinessException("更新提交失败");
        }
        log.info("更新提交: id={}", submission.getId());
    }

    public void delete(Long id) {
        if (submissionMapper.deleteById(id) == 0) {
            log.warn("删除提交记录失败: id={}", id);
            throw new BusinessException("删除提交记录失败");
        }
        log.info("删除提交记录: id={}", id);
    }

    public void grade(Long submissionId, Integer score, String comment) {
        if (submissionMapper.grade(submissionId, score, comment, LocalDateTime.now()) == 0) {
            log.warn("批改失败: submissionId={}", submissionId);
            throw new BusinessException("批改失败");
        }
        log.info("批改作业: submissionId={}, score={}", submissionId, score);
    }

    public String generateAutoComment(Long submissionId, Integer score) {
        Map<String, Object> detail = submissionMapper.selectWithDetails(submissionId);
        if (detail == null) {
            throw new BusinessException("提交记录不存在");
        }
        String content = detail.get("content") != null ? detail.get("content").toString() : "";
        String assignmentContent = detail.get("assignmentContent") != null ? detail.get("assignmentContent").toString() : "";
        String courseName = detail.get("course_name") != null ? detail.get("course_name").toString() : "";
        String studentName = detail.get("studentName") != null ? detail.get("studentName").toString() : "同学";
        int fullScore = detail.get("fullScore") != null ? ((Number) detail.get("fullScore")).intValue() : 100;

        StringBuilder comment = new StringBuilder();

        comment.append(scoreComment(score, fullScore)).append("\n");

        String kw = keywordFeedback(content + " " + assignmentContent);
        if (!kw.isEmpty()) {
            comment.append(kw).append("\n");
        }

        String improve = improvementAdvice(score, fullScore, content);
        if (!improve.isEmpty()) {
            comment.append(improve);
        }

        return comment.toString().trim();
    }

    private String scoreComment(int score, int fullScore) {
        double pct = (double) score / fullScore;
        if (pct >= 0.95) return "优秀！回答完整准确，思路清晰，展现了扎实的知识掌握。继续保持！";
        if (pct >= 0.85) return "良好！回答基本正确，逻辑清晰，部分细节可以进一步优化。";
        if (pct >= 0.75) return "中等偏上，整体思路不错，展现了对知识点的理解，建议多关注细节。";
        if (pct >= 0.60) return "及格，基本达到要求，但部分知识点理解不够深入，建议加强复习。";
        return "本次作业未达到要求，建议认真回顾课程内容，注意理解核心概念，如有疑问请及时请教。";
    }

    private String keywordFeedback(String text) {
        String lower = text.toLowerCase();
        StringBuilder sb = new StringBuilder();

        if (lower.contains("递归") || lower.contains("recursion") || lower.contains("递归算法")) {
            sb.append("• 对递归概念有一定理解，建议继续练习递归算法的应用。\n");
        }
        if (lower.contains("循环") || lower.contains("loop") || lower.contains("for") || lower.contains("while")) {
            sb.append("• 循环逻辑清晰，能够正确使用循环结构解决问题。\n");
        }
        if (lower.contains("排序") || lower.contains("sort") || lower.contains("排序算法")) {
            sb.append("• 排序算法运用得当，建议了解不同排序算法的复杂度差异。\n");
        }
        if (lower.contains("数据库") || lower.contains("sql") || lower.contains("database") || lower.contains("mysql")) {
            sb.append("• SQL语句编写规范，对数据库操作有较好的掌握。\n");
        }
        if (lower.contains("面向对象") || lower.contains("oop") || lower.contains("封装") || lower.contains("继承") || lower.contains("多态")) {
            sb.append("• 面向对象编程概念理解正确，能够合理运用封装、继承、多态。\n");
        }
        if (lower.contains("异常") || lower.contains("exception") || lower.contains("try") || lower.contains("catch")) {
            sb.append("• 异常处理机制使用得当，代码健壮性较好。\n");
        }
        if (lower.contains("线程") || lower.contains("thread") || lower.contains("并发") || lower.contains("多线程")) {
            sb.append("• 多线程编程概念清晰，注意线程安全问题。\n");
        }
        if (lower.contains("spring") || lower.contains("spring boot") || lower.contains("微服务")) {
            sb.append("• 对Spring框架有较好的理解，微服务架构概念清晰。\n");
        }
        if (lower.contains("前端") || lower.contains("vue") || lower.contains("react") || lower.contains("html")) {
            sb.append("• 前端技术掌握良好，界面交互逻辑清晰。\n");
        }
        if (lower.contains("算法") || lower.contains("数据结构") || lower.contains("链表") || lower.contains("树") || lower.contains("图")) {
            sb.append("• 数据结构和算法基础扎实，能够分析问题并选择合适的数据结构。\n");
        }

        return sb.toString().trim();
    }

    private String improvementAdvice(int score, int fullScore, String content) {
        double pct = (double) score / fullScore;
        if (pct >= 0.85) return "";
        if (pct >= 0.60) {
            if (content.length() < 50) {
                return "建议：作业内容可以更加充实，尝试展开论述你的思路，增加示例说明。";
            }
            return "建议：回顾相关知识点，尝试将理论联系实际，多加练习巩固理解。";
        }
        if (content.length() < 50) {
            return "建议：请重新学习相关章节内容，注意作业要求的核心知识点，必要时可以参考答案或请教老师。";
        }
        return "建议：建议重新梳理课程知识点，重点关注作业中涉及的核心概念，完成订正后重新提交。";
    }

}
