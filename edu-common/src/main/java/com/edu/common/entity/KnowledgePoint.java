package com.edu.common.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

public class KnowledgePoint implements Serializable {

    private Long          id;
    private Long          chapterId;
    private String        content;
    private String        importance;
    private Integer       sortOrder;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getChapterId() { return chapterId; }
    public void setChapterId(Long chapterId) { this.chapterId = chapterId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getImportance() { return importance; }
    public void setImportance(String importance) { this.importance = importance; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }

}
