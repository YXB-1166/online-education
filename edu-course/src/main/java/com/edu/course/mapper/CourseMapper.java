package com.edu.course.mapper;

import com.edu.common.entity.Course;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CourseMapper {

    @Select("select * from tb_course where id = #{id}")
    Course selectById(@Param("id") Long id);

    @Select("<script>select * from tb_course where status != '0' and status != '4'" +
            "<if test='courseName != null'> and (course_name like concat('%',#{courseName},'%') or description like concat('%',#{courseName},'%'))</if>" +
            "<if test='teacherId != null'> and teacher_id = #{teacherId}</if>" +
            "<if test='status != null'> and status = #{status}</if>" +
            " order by id</script>")
    List<Course> selectList(Course course);

    @Select("<script>select count(*) from tb_course where status != '0' and status != '4'" +
            "<if test='courseName != null'> and (course_name like concat('%',#{courseName},'%') or description like concat('%',#{courseName},'%'))</if>" +
            "<if test='teacherId != null'> and teacher_id = #{teacherId}</if>" +
            "<if test='status != null'> and status = #{status}</if>" +
            "</script>")
    long countList(Course course);

    @Select("<script>select * from tb_course where status != '0' and status != '4'" +
            "<if test='c.courseName != null'> and (course_name like concat('%',#{c.courseName},'%') or description like concat('%',#{c.courseName},'%'))</if>" +
            "<if test='c.teacherId != null'> and teacher_id = #{c.teacherId}</if>" +
            "<if test='c.status != null'> and status = #{c.status}</if>" +
            " order by id limit #{offset}, #{size}</script>")
    List<Course> selectPage(@Param("c") Course course, @Param("offset") int offset, @Param("size") int size);

    @Insert("insert into tb_course(course_name, description, teacher_id, credit, max_students, status, start_time, end_time) " +
            "values(#{courseName}, #{description}, #{teacherId}, #{credit}, #{maxStudents}, #{status}, #{startTime}, #{endTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Course course);

    @Update("update tb_course set course_name=#{courseName}, description=#{description}, teacher_id=#{teacherId}, " +
            "credit=#{credit}, max_students=#{maxStudents}, status=#{status}, start_time=#{startTime}, " +
            "end_time=#{endTime} where id=#{id}")
    int update(Course course);

    @Delete("delete from tb_course where id=#{id}")
    int deleteById(@Param("id") Long id);

    @Update("update tb_course set enrolled_count = enrolled_count + 1 where id = #{courseId}")
    int incrementEnrolled(@Param("courseId") Long courseId);

    @Update("update tb_course set enrolled_count = enrolled_count - 1 where id = #{courseId} and enrolled_count > 0")
    int decrementEnrolled(@Param("courseId") Long courseId);

    @Select("select * from tb_course where teacher_id = #{teacherId} order by id desc")
    List<Course> selectByTeacher(@Param("teacherId") Long teacherId);

    @Select("select * from tb_course where status = '0' order by id desc")
    List<Course> selectPending();

    @Update("update tb_course set status = '1' where id = #{id} and status = '0'")
    int approveById(@Param("id") Long id);

    @Update("update tb_course set status = '4' where id = #{id} and status = '0'")
    int rejectById(@Param("id") Long id);

}
