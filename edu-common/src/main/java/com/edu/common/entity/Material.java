package com.edu.common.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Material implements Serializable {

    private Long          id;
    private Long          courseId;
    private Long          teacherId;
    private String        title;
    private String        content;
    private Integer       requiredSeconds;
    private LocalDateTime createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Integer getRequiredSeconds() { return requiredSeconds; }
    public void setRequiredSeconds(Integer requiredSeconds) { this.requiredSeconds = requiredSeconds; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

}
