package com.edu.exam.mapper;

import com.edu.common.entity.Submission;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface SubmissionMapper {

    @Select("select * from tb_submission where id = #{id}")
    Submission selectById(@Param("id") Long id);

    @Select("<script>select * from tb_submission where 1=1" +
            "<if test='assignmentId != null'> and assignment_id = #{assignmentId}</if>" +
            "<if test='studentId != null'> and student_id = #{studentId}</if>" +
            "<if test='status != null'> and status = #{status}</if>" +
            " order by id</script>")
    List<Submission> selectList(Submission submission);

    @Select("<script>select count(*) from tb_submission where 1=1" +
            "<if test='assignmentId != null'> and assignment_id = #{assignmentId}</if>" +
            "<if test='studentId != null'> and student_id = #{studentId}</if>" +
            "<if test='status != null'> and status = #{status}</if>" +
            "</script>")
    long countList(Submission submission);

    @Select("<script>select * from tb_submission where 1=1" +
            "<if test='s.assignmentId != null'> and assignment_id = #{s.assignmentId}</if>" +
            "<if test='s.studentId != null'> and student_id = #{s.studentId}</if>" +
            "<if test='s.status != null'> and status = #{s.status}</if>" +
            " order by id limit #{offset}, #{size}</script>")
    List<Submission> selectPage(@Param("s") Submission submission, @Param("offset") int offset, @Param("size") int size);

    @Insert("insert into tb_submission(assignment_id, student_id, content, attachment_url, status, submit_time) " +
            "values(#{assignmentId}, #{studentId}, #{content}, #{attachmentUrl}, #{status}, #{submitTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Submission submission);

    @Update("update tb_submission set content=#{content}, attachment_url=#{attachmentUrl} where id=#{id}")
    int update(Submission submission);

    @Delete("delete from tb_submission where id=#{id}")
    int deleteById(@Param("id") Long id);

    @Update("update tb_submission set score=#{score}, comment=#{comment}, status=2, grade_time=#{gradeTime} where id=#{id}")
    int grade(@Param("id") Long id, @Param("score") Integer score,
              @Param("comment") String comment, @Param("gradeTime") java.time.LocalDateTime gradeTime);

    @Select("select s.*, a.title as assignmentTitle, a.full_score as fullScore, a.content as assignmentContent, " +
            "c.course_name, u.real_name as studentName " +
            "from tb_submission s " +
            "join tb_assignment a on s.assignment_id = a.id " +
            "join tb_course c on a.course_id = c.id " +
            "join tb_user u on s.student_id = u.id " +
            "where s.id = #{id}")
    Map<String, Object> selectWithDetails(@Param("id") Long id);

}
