package com.edu.common.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class Chapter implements Serializable {

    private Long          id;
    private Long          courseId;
    private String        title;
    private String        summary;
    private Integer       sortOrder;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private List<KnowledgePoint> knowledgePoints;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
    public List<KnowledgePoint> getKnowledgePoints() { return knowledgePoints; }
    public void setKnowledgePoints(List<KnowledgePoint> knowledgePoints) { this.knowledgePoints = knowledgePoints; }

}
