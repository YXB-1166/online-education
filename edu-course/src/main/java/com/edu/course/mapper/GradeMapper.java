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
            "select e.id as itemId, e.title COLLATE utf8mb4_unicode_ci as itemName, 'exam' as itemType, " +
            "er.score, e.total_score as full_score, er.submit_time as score_time, e.end_time as deadline " +
            "from tb_exam e " +
            "left join tb_exam_record er on er.exam_id = e.id and er.student_id = #{studentId} " +
            "where e.course_id = #{courseId} and er.score is not null " +
            "order by score_time asc")
    List<Map<String, Object>> selectStudentGradeTrend(@Param("courseId") Long courseId, @Param("studentId") Long studentId);

    @Select("select coalesce(sum(s.score) * 100.0 / nullif(sum(a.full_score), 0), 0) " +
            "from tb_assignment a " +
            "join tb_submission s on s.assignment_id = a.id and s.student_id = #{studentId} " +
            "where a.course_id = #{courseId} and s.score is not null")
    Double calcHomeworkAvg(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    @Select("select score from tb_course_selection where course_id = #{courseId} and student_id = #{studentId}")
    Integer selectFinalScore(@Param("courseId") Long courseId, @Param("studentId") Long studentId);

    @Select("select er.score from tb_exam_record er " +
            "join tb_exam e on er.exam_id = e.id " +
            "where e.course_id = #{courseId} and er.student_id = #{studentId} " +
            "order by er.submit_time desc limit 1")
    Integer calcExamScore(@Param("studentId") Long studentId, @Param("courseId") Long courseId);
}
