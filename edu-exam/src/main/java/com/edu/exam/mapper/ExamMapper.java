package com.edu.exam.mapper;

import com.edu.common.entity.Exam;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ExamMapper {

    @Select("select * from tb_exam where id = #{id}")
    Exam selectById(@Param("id") Long id);

    @Select("<script>select * from tb_exam where 1=1" +
            "<if test='courseId != null'> and course_id = #{courseId}</if>" +
            "<if test='teacherId != null'> and teacher_id = #{teacherId}</if>" +
            "<if test='status != null'> and status = #{status}</if>" +
            " order by id desc</script>")
    List<Exam> selectList(Exam exam);

    @Select("<script>select count(*) from tb_exam where 1=1" +
            "<if test='courseId != null'> and course_id = #{courseId}</if>" +
            "<if test='teacherId != null'> and teacher_id = #{teacherId}</if>" +
            "<if test='status != null'> and status = #{status}</if>" +
            "</script>")
    long countList(Exam exam);

    @Select("<script>select * from tb_exam where 1=1" +
            "<if test='e.courseId != null'> and course_id = #{e.courseId}</if>" +
            "<if test='e.teacherId != null'> and teacher_id = #{e.teacherId}</if>" +
            "<if test='e.status != null'> and status = #{e.status}</if>" +
            " order by id desc limit #{offset}, #{size}</script>")
    List<Exam> selectPage(@Param("e") Exam exam, @Param("offset") int offset, @Param("size") int size);

    @Insert("insert into tb_exam(course_id, teacher_id, title, duration, auto_grade_choice, auto_grade_judge, " +
            "total_score, start_time, end_time, status) " +
            "values(#{courseId}, #{teacherId}, #{title}, #{duration}, #{autoGradeChoice}, #{autoGradeJudge}, " +
            "#{totalScore}, #{startTime}, #{endTime}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Exam exam);

    @Update("<script>update tb_exam set title=#{title}, duration=#{duration}, " +
            "auto_grade_choice=#{autoGradeChoice}, auto_grade_judge=#{autoGradeJudge}, " +
            "total_score=#{totalScore}, start_time=#{startTime}, end_time=#{endTime}, status=#{status} " +
            "where id=#{id}</script>")
    int update(Exam exam);

    @Delete("delete from tb_exam where id=#{id}")
    int deleteById(@Param("id") Long id);
}
