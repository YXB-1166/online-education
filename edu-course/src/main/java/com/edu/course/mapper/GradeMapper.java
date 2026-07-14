package com.edu.course.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface GradeMapper {

    @Select("select u.id as studentId, u.real_name as studentName, cs.score as finalScore, cs.status as selectionStatus " +
            "from tb_course_selection cs join tb_user u on cs.student_id = u.id " +
            "where cs.course_id = #{courseId} and cs.status in ('1','3') " +
            "order by cs.score desc")
    List<Map<String, Object>> selectCourseGrades(@Param("courseId") Long courseId);

    @Select("select a.id as itemId, a.title as itemName, 'assignment' as itemType, " +
            "s.score, a.full_score, s.submit_time as score_time, a.deadline " +
            "from tb_assignment a " +
            "left join tb_submission s on s.assignment_id = a.id and s.student_id = #{studentId} " +
            "where a.course_id = #{courseId} and s.score is not null " +
            "union all " +
            "select e.id as itemId, e.title as itemName, 'exam' as itemType, " +
            "er.score, e.total_score as full_score, er.submit_time as score_time, e.end_time as deadline " +
            "from tb_exam e " +
            "left join tb_exam_record er on er.exam_id = e.id and er.student_id = #{studentId} " +
            "where e.course_id = #{courseId} and er.score is not null " +
            "order by score_time asc")
    List<Map<String, Object>> selectStudentGradeTrend(@Param("courseId") Long courseId, @Param("studentId") Long studentId);
}
