package com.edu.course.mapper;

import com.edu.common.entity.Course;
import com.edu.common.entity.CourseSelection;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface CourseSelectionMapper {

    @Select("select * from tb_course_selection where student_id = #{studentId} and course_id = #{courseId} and status = '1'")
    CourseSelection selectActiveByStudentAndCourse(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    @Select("select * from tb_course_selection where student_id = #{studentId} and course_id = #{courseId} and status = '0'")
    CourseSelection selectPendingByStudentAndCourse(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    @Select("select * from tb_course_selection where student_id = #{studentId}")
    List<CourseSelection> selectByStudent(@Param("studentId") Long studentId);

    @Select("<script>" +
            "select cs.* from tb_course_selection cs join tb_course c on cs.course_id = c.id " +
            "where cs.status = '0'" +
            "<if test='teacherId != null'> and c.teacher_id = #{teacherId}</if>" +
            "</script>")
    List<CourseSelection> selectPending(@Param("teacherId") Long teacherId);

    @Insert("insert into tb_course_selection(student_id, course_id, status, select_time) " +
            "values(#{studentId}, #{courseId}, #{status}, #{selectTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CourseSelection cs);

    @Update("update tb_course_selection set status = #{status}, drop_time = #{dropTime} " +
            "where student_id = #{studentId} and course_id = #{courseId} and status = '1'")
    int updateStatus(@Param("studentId") Long studentId, @Param("courseId") Long courseId,
                     @Param("status") String status, @Param("dropTime") LocalDateTime dropTime);

    @Update("update tb_course_selection set status = '0', select_time = #{selectTime}, drop_time = null " +
            "where student_id = #{studentId} and course_id = #{courseId} and status in ('2', '4')")
    int reactivate(@Param("studentId") Long studentId, @Param("courseId") Long courseId,
                   @Param("selectTime") LocalDateTime selectTime);

    @Update("update tb_course_selection set status = '0', select_time = #{selectTime}, drop_time = null " +
            "where id = #{id} and status = '2'")
    int reactivateById(@Param("id") Long id, @Param("selectTime") LocalDateTime selectTime);

    @Update("update tb_course_selection set status = '1' where id = #{id} and status = '0'")
    int approveById(@Param("id") Long id);

    @Update("update tb_course_selection set status = '4' where id = #{id} and status = '0'")
    int rejectById(@Param("id") Long id);

    @Select("select * from tb_course_selection where id = #{id}")
    CourseSelection selectById(@Param("id") Long id);

    @Select("select c.* from tb_course_selection cs join tb_course c on cs.course_id = c.id " +
            "where cs.student_id = #{studentId} and cs.status = '1'")
    List<Course> selectMyCourses(@Param("studentId") Long studentId);

    @Select("select cs.* from tb_course_selection cs join tb_course c on cs.course_id = c.id " +
            "where c.id = #{courseId} and c.teacher_id = #{teacherId} order by cs.select_time desc")
    List<CourseSelection> selectByCourse(@Param("courseId") Long courseId, @Param("teacherId") Long teacherId);

    @Select("select cs.* from tb_course_selection cs where cs.course_id = #{courseId} and cs.status = '1'")
    List<CourseSelection> selectActiveByCourse(@Param("courseId") Long courseId);

    @Update("update tb_course_selection set score = #{score} where student_id = #{studentId} and course_id = #{courseId}")
    int updateScore(@Param("studentId") Long studentId, @Param("courseId") Long courseId, @Param("score") Integer score);

}
