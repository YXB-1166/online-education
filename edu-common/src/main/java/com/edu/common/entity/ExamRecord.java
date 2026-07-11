package com.edu.common.entity;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

public class ExamRecord implements Serializable {
    private Long id;
    @NotNull private Long examId;
    @NotNull private Long studentId;
    private String answers;
    private Integer score;
    private Integer autoScore;
    private Integer graded = 0;
    private LocalDateTime submitTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getExamId() { return examId; }
    public void setExamId(Long examId) { this.examId = examId; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public String getAnswers() { return answers; }
    public void setAnswers(String answers) { this.answers = answers; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    public Integer getAutoScore() { return autoScore; }
    public void setAutoScore(Integer autoScore) { this.autoScore = autoScore; }
    public Integer getGraded() { return graded; }
    public void setGraded(Integer graded) { this.graded = graded; }
    public LocalDateTime getSubmitTime() { return submitTime; }
    public void setSubmitTime(LocalDateTime submitTime) { this.submitTime = submitTime; }
}
