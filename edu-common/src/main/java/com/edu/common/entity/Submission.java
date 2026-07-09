package com.edu.common.entity;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

public class Submission implements Serializable {

    private Long          id;

    @NotNull(message = "作业ID不能为空")
    private Long          assignmentId;

    @NotNull(message = "学生ID不能为空")
    private Long          studentId;

    private String        content;
    private String        attachmentUrl;
    private Integer       score;
    private String        comment;
    private Integer       status;
    private Integer       submitCount;
    private LocalDateTime submitTime;
    private LocalDateTime gradeTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getAssignmentId() { return assignmentId; }
    public void setAssignmentId(Long assignmentId) { this.assignmentId = assignmentId; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getAttachmentUrl() { return attachmentUrl; }
    public void setAttachmentUrl(String attachmentUrl) { this.attachmentUrl = attachmentUrl; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Integer getSubmitCount() { return submitCount; }
    public void setSubmitCount(Integer submitCount) { this.submitCount = submitCount; }
    public LocalDateTime getSubmitTime() { return submitTime; }
    public void setSubmitTime(LocalDateTime submitTime) { this.submitTime = submitTime; }
    public LocalDateTime getGradeTime() { return gradeTime; }
    public void setGradeTime(LocalDateTime gradeTime) { this.gradeTime = gradeTime; }

}
