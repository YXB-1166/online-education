package com.edu.course.mapper;

import com.edu.common.entity.Course;
import com.edu.common.entity.CourseSelection;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface AssistantMapper {

    @Select("select c.*, u.real_name as teacherName from tb_course c " +
            "join tb_user u on c.teacher_id = u.id " +
            "where c.id not in " +
            "(select course_id from tb_course_selection where student_id = #{studentId} and status in ('1','0')) " +
            "order by c.enrolled_count desc limit #{limit}")
    List<Map<String, Object>> recommendCourses(@Param("studentId") Long studentId, @Param("limit") int limit);

    @Select("select c.*, u.real_name as teacherName from tb_course c " +
            "join tb_user u on c.teacher_id = u.id where c.status = '2'")
    List<Map<String, Object>> listActiveCourses();

    @Select("select c.*, u.real_name as teacherName from tb_course c " +
            "join tb_user u on c.teacher_id = u.id " +
            "where c.course_name like concat('%', #{keyword}, '%') " +
            "or c.description like concat('%', #{keyword}, '%') " +
            "or u.real_name like concat('%', #{keyword}, '%')")
    List<Map<String, Object>> searchCourses(@Param("keyword") String keyword);

    @Select("select cs.id, cs.status as selectionStatus, cs.select_time, c.course_name, c.credit, " +
            "c.max_students, c.enrolled_count, c.status as courseStatus " +
            "from tb_course_selection cs join tb_course c on cs.course_id = c.id " +
            "where cs.student_id = #{studentId} order by cs.select_time desc")
    List<Map<String, Object>> mySelections(@Param("studentId") Long studentId);

    @Select("select count(*) from tb_course_selection cs " +
            "join tb_course c on cs.course_id = c.id " +
            "where c.teacher_id = #{teacherId} and cs.status = '0'")
    long countPendingByTeacher(@Param("teacherId") Long teacherId);

    @Select("select c.id, c.course_name, c.enrolled_count, c.max_students, c.status, " +
            "(select count(*) from tb_course_selection where course_id = c.id and status = '0') as pendingCount " +
            "from tb_course c where c.teacher_id = #{teacherId}")
    List<Map<String, Object>> teacherCourseStats(@Param("teacherId") Long teacherId);

    @Select("select u.id, u.username, u.real_name, u.email, cs.status, cs.select_time, cs.score " +
            "from tb_course_selection cs join tb_user u on cs.student_id = u.id " +
            "where cs.course_id = #{courseId} order by cs.select_time desc")
    List<Map<String, Object>> courseStudents(@Param("courseId") Long courseId);

    @Select("select id, course_name from tb_course where teacher_id = #{teacherId} order by course_name")
    List<Map<String, Object>> teacherCourses(@Param("teacherId") Long teacherId);

    @Select("select id, real_name from tb_user where role = 2")
    List<Map<String, Object>> listTeachers();

    @Select("select count(*) from tb_course")
    long totalCourses();

    @Select("select count(*) from tb_course_selection where status = '1'")
    long totalEnrollments();

    @Select("select count(*) from tb_user where role = 1")
    long totalStudents();

    @Select("select count(*) from tb_user where role = 2")
    long totalTeachers();

    @Select("select a.id as assignmentId, a.title as assignmentTitle, " +
            "c.course_name, u.real_name as studentName, s.id as submissionId, " +
            "s.submit_time " +
            "from tb_submission s " +
            "join tb_assignment a on s.assignment_id = a.id " +
            "join tb_course c on a.course_id = c.id " +
            "join tb_user u on s.student_id = u.id " +
            "where c.teacher_id = #{teacherId} and s.status = 1 " +
            "order by s.submit_time desc")
    List<Map<String, Object>> ungradedSubmissions(@Param("teacherId") Long teacherId);

    @Select("select c.id as courseId, c.course_name, c.credit, " +
            "(select count(*) from tb_assignment where course_id = c.id) as totalAssignments, " +
            "(select count(*) from tb_submission s join tb_assignment a on s.assignment_id = a.id " +
            " where a.course_id = c.id and s.student_id = #{studentId}) as submittedCount, " +
            "(select count(*) from tb_submission s join tb_assignment a on s.assignment_id = a.id " +
            " where a.course_id = c.id and s.student_id = #{studentId} and s.score is not null) as gradedCount, " +
            "(select coalesce(avg(s.score),0) from tb_submission s join tb_assignment a on s.assignment_id = a.id " +
            " where a.course_id = c.id and s.student_id = #{studentId} and s.score is not null) as avgScore, " +
            "(select count(*) from tb_submission s join tb_assignment a on s.assignment_id = a.id " +
            " where a.course_id = c.id and s.student_id = #{studentId} and s.score is not null and s.score < 60) as below60Count " +
            "from tb_course_selection cs " +
            "join tb_course c on cs.course_id = c.id " +
            "where cs.student_id = #{studentId} and cs.status = '1'")
    List<Map<String, Object>> learningProgress(@Param("studentId") Long studentId);

    @Select("select count(*) from tb_assignment where course_id = #{courseId}")
    long countAssignmentsByCourse(@Param("courseId") Long courseId);

    @Select("select count(*) from tb_submission s " +
            "join tb_assignment a on s.assignment_id = a.id " +
            "where a.course_id = #{courseId} and s.student_id = #{studentId}")
    long countSubmissionsByCourse(@Param("courseId") Long courseId, @Param("studentId") Long studentId);
}
