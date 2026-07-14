package com.edu.course.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface CalendarMapper {

    @Select("select a.id as eventId, a.title, a.deadline as startTime, null as endTime, " +
            "'assignment' as eventType, c.course_name as courseName, c.id as courseId " +
            "from tb_assignment a join tb_course c on a.course_id = c.id " +
            "where a.course_id in (select course_id from tb_course_selection where student_id = #{userId}) " +
            "and c.status in ('2','3') " +
            "union all " +
            "select e.id as eventId, e.title, e.start_time as startTime, e.end_time as endTime, " +
            "'exam' as eventType, c.course_name as courseName, c.id as courseId " +
            "from tb_exam e join tb_course c on e.course_id = c.id " +
            "where e.course_id in (select course_id from tb_course_selection where student_id = #{userId}) " +
            "and e.status = 1 " +
            "union all " +
            "select c.id as eventId, concat('结课考试: ', c.course_name) as title, c.exam_time as startTime, null as endTime, " +
            "'courseExam' as eventType, c.course_name as courseName, c.id as courseId " +
            "from tb_course c " +
            "where c.id in (select course_id from tb_course_selection where student_id = #{userId}) " +
            "and c.exam_time is not null " +
            "order by startTime asc")
    List<Map<String, Object>> selectEventsByStudent(@Param("userId") Long userId);

    @Select("select a.id as eventId, a.title, a.deadline as startTime, null as endTime, " +
            "'assignment' as eventType, c.course_name as courseName, c.id as courseId " +
            "from tb_assignment a join tb_course c on a.course_id = c.id " +
            "where c.teacher_id = #{userId} and c.status in ('2','3') " +
            "union all " +
            "select e.id as eventId, e.title, e.start_time as startTime, e.end_time as endTime, " +
            "'exam' as eventType, c.course_name as courseName, c.id as courseId " +
            "from tb_exam e join tb_course c on e.course_id = c.id " +
            "where c.teacher_id = #{userId} and e.status = 1 " +
            "union all " +
            "select c.id as eventId, concat('结课考试: ', c.course_name) as title, c.exam_time as startTime, null as endTime, " +
            "'courseExam' as eventType, c.course_name as courseName, c.id as courseId " +
            "from tb_course c " +
            "where c.teacher_id = #{userId} and c.exam_time is not null " +
            "order by startTime asc")
    List<Map<String, Object>> selectEventsByTeacher(@Param("userId") Long userId);
}
