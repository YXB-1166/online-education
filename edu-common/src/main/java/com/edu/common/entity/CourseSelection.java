package com.edu.common.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 选课记录实体类 — 对应 tb_course_selection 表
 */
public class CourseSelection implements Serializable {

    private Long          id;
    private Long          studentId;    // 学生ID
    private Long          courseId;     // 课程ID
    private Integer       score;        // 总评成绩
    private String        status;       // 状态：0-待审核 1-已选课 2-已退选 3-已结课 4-审核不通过
    private LocalDateTime selectTime;   // 选课时间
    private LocalDateTime dropTime;     // 退选时间

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getSelectTime() { return selectTime; }
    public void setSelectTime(LocalDateTime selectTime) { this.selectTime = selectTime; }
    public LocalDateTime getDropTime() { return dropTime; }
    public void setDropTime(LocalDateTime dropTime) { this.dropTime = dropTime; }

}
