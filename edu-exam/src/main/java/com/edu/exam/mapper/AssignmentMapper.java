package com.edu.exam.mapper;

import com.edu.common.entity.Assignment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AssignmentMapper {

    @Select("select * from tb_assignment where id = #{id}")
    Assignment selectById(@Param("id") Long id);

    @Select("<script>select * from tb_assignment where 1=1" +
            "<if test='title != null'> and title like concat('%',#{title},'%')</if>" +
            "<if test='courseId != null'> and course_id = #{courseId}</if>" +
            "<if test='teacherId != null'> and teacher_id = #{teacherId}</if>" +
            " order by id</script>")
    List<Assignment> selectList(Assignment assignment);

    @Select("<script>select count(*) from tb_assignment where 1=1" +
            "<if test='title != null'> and title like concat('%',#{title},'%')</if>" +
            "<if test='courseId != null'> and course_id = #{courseId}</if>" +
            "<if test='teacherId != null'> and teacher_id = #{teacherId}</if>" +
            "</script>")
    long countList(Assignment assignment);

    @Select("<script>select * from tb_assignment where 1=1" +
            "<if test='a.title != null'> and title like concat('%',#{a.title},'%')</if>" +
            "<if test='a.courseId != null'> and course_id = #{a.courseId}</if>" +
            "<if test='a.teacherId != null'> and teacher_id = #{a.teacherId}</if>" +
            " order by id limit #{offset}, #{size}</script>")
    List<Assignment> selectPage(@Param("a") Assignment assignment, @Param("offset") int offset, @Param("size") int size);

    @Insert("insert into tb_assignment(course_id, teacher_id, title, content, full_score, deadline, allow_submit_count) " +
            "values(#{courseId}, #{teacherId}, #{title}, #{content}, #{fullScore}, #{deadline}, #{allowSubmitCount})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Assignment assignment);

    @Update("update tb_assignment set title=#{title}, content=#{content}, full_score=#{fullScore}, " +
            "deadline=#{deadline}, allow_submit_count=#{allowSubmitCount} where id=#{id}")
    int update(Assignment assignment);

    @Delete("delete from tb_assignment where id=#{id}")
    int deleteById(@Param("id") Long id);

}
