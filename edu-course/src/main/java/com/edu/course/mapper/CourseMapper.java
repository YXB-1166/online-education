package com.edu.course.mapper;

import com.edu.common.entity.Course;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface CourseMapper {

    @Select("select * from tb_course where id = #{id}")
    Course selectById(@Param("id") Long id);

    @Select("<script>select c.*, u.real_name as teacher_name from tb_course c left join tb_user u on c.teacher_id = u.id" +
            " where c.status != '0' and c.status != '4'" +
            "<if test='courseName != null'> and (c.course_name like concat('%',#{courseName},'%')" +
            " or c.description like concat('%',#{courseName},'%')" +
            " or u.real_name like concat('%',#{courseName},'%'))</if>" +
            "<if test='teacherId != null'> and c.teacher_id = #{teacherId}</if>" +
            "<if test='status != null'> and c.status = #{status}</if>" +
            " order by c.id</script>")
    List<Course> selectList(Course course);

    @Select("<script>select count(*) from tb_course c left join tb_user u on c.teacher_id = u.id" +
            " where c.status != '0' and c.status != '4'" +
            "<if test='courseName != null'> and (c.course_name like concat('%',#{courseName},'%')" +
            " or c.description like concat('%',#{courseName},'%')" +
            " or u.real_name like concat('%',#{courseName},'%'))</if>" +
            "<if test='teacherId != null'> and c.teacher_id = #{teacherId}</if>" +
            "<if test='status != null'> and c.status = #{status}</if>" +
            "</script>")
    long countList(Course course);

    @Select("<script>select c.*, u.real_name as teacher_name from tb_course c left join tb_user u on c.teacher_id = u.id" +
            " where c.status != '0' and c.status != '4'" +
            "<if test='c.courseName != null'> and (c.course_name like concat('%',#{c.courseName},'%')" +
            " or c.description like concat('%',#{c.courseName},'%')" +
            " or u.real_name like concat('%',#{c.courseName},'%'))</if>" +
            "<if test='c.teacherId != null'> and c.teacher_id = #{c.teacherId}</if>" +
            "<if test='c.status != null'> and c.status = #{c.status}</if>" +
            " order by c.id limit #{offset}, #{size}</script>")
    List<Course> selectPage(@Param("c") Course course, @Param("offset") int offset, @Param("size") int size);

    @Select("<script>select c.*, u.real_name as teacher_name from tb_course c left join tb_user u on c.teacher_id = u.id" +
            " where c.status != '0' and c.status != '4'" +
            "<if test='c.courseName != null'> and (c.course_name like concat('%',#{c.courseName},'%')" +
            " or c.description like concat('%',#{c.courseName},'%')" +
            " or u.real_name like concat('%',#{c.courseName},'%'))</if>" +
            "<if test='c.teacherId != null'> and c.teacher_id = #{c.teacherId}</if>" +
            "<if test='c.status != null'> and c.status = #{c.status}</if>" +
            " and c.id not in (select course_id from tb_course_selection where student_id = #{studentId})" +
            " order by c.id</script>")
    List<Course> selectListExcludingStudent(@Param("c") Course course, @Param("studentId") Long studentId);

    @Select("<script>select count(*) from tb_course c left join tb_user u on c.teacher_id = u.id" +
            " where c.status != '0' and c.status != '4'" +
            "<if test='c.courseName != null'> and (c.course_name like concat('%',#{c.courseName},'%')" +
            " or c.description like concat('%',#{c.courseName},'%')" +
            " or u.real_name like concat('%',#{c.courseName},'%'))</if>" +
            "<if test='c.teacherId != null'> and c.teacher_id = #{c.teacherId}</if>" +
            "<if test='c.status != null'> and c.status = #{c.status}</if>" +
            " and c.id not in (select course_id from tb_course_selection where student_id = #{studentId})" +
            "</script>")
    long countListExcludingStudent(@Param("c") Course course, @Param("studentId") Long studentId);

    @Select("<script>select c.*, u.real_name as teacher_name from tb_course c left join tb_user u on c.teacher_id = u.id" +
            " where c.status != '0' and c.status != '4'" +
            "<if test='c.courseName != null'> and (c.course_name like concat('%',#{c.courseName},'%')" +
            " or c.description like concat('%',#{c.courseName},'%')" +
            " or u.real_name like concat('%',#{c.courseName},'%'))</if>" +
            "<if test='c.teacherId != null'> and c.teacher_id = #{c.teacherId}</if>" +
            "<if test='c.status != null'> and c.status = #{c.status}</if>" +
            " and c.id not in (select course_id from tb_course_selection where student_id = #{studentId})" +
            " order by c.id limit #{offset}, #{size}</script>")
    List<Course> selectPageExcludingStudent(@Param("c") Course course, @Param("studentId") Long studentId,
                                            @Param("offset") int offset, @Param("size") int size);

    @Insert("insert into tb_course(course_name, description, teacher_id, credit, max_students, status, homework_ratio, exam_ratio, exam_time, start_time, end_time) " +
            "values(#{courseName}, #{description}, #{teacherId}, #{credit}, #{maxStudents}, #{status}, #{homeworkRatio}, #{examRatio}, #{examTime}, #{startTime}, #{endTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Course course);

    @Update("update tb_course set course_name=#{courseName}, description=#{description}, teacher_id=#{teacherId}, " +
            "credit=#{credit}, max_students=#{maxStudents}, status=#{status}, homework_ratio=#{homeworkRatio}, " +
            "exam_ratio=#{examRatio}, exam_time=#{examTime}, start_time=#{startTime}, end_time=#{endTime} where id=#{id}")
    int update(Course course);

    @Update("update tb_course set status='2', homework_ratio=#{homeworkRatio}, exam_ratio=#{examRatio}, " +
            "exam_time=#{examTime} where id=#{id} and status='1'")
    int startCourse(@Param("id") Long id, @Param("homeworkRatio") Integer homeworkRatio,
                    @Param("examRatio") Integer examRatio, @Param("examTime") LocalDateTime examTime);

    @Update("update tb_course set status='3' where id=#{id} and status='2'")
    int endCourse(@Param("id") Long id);

    @Update("update tb_course set exam_time=#{examTime} where id=#{id}")
    int updateExamTime(@Param("id") Long id, @Param("examTime") LocalDateTime examTime);

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
