package com.edu.exam.mapper;

import com.edu.common.entity.ExamQuestion;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ExamQuestionMapper {

    @Select("select * from tb_exam_question where id = #{id}")
    ExamQuestion selectById(@Param("id") Long id);

    @Select("select * from tb_exam_question where exam_id = #{examId} order by sort_order, id")
    List<ExamQuestion> selectByExamId(@Param("examId") Long examId);

    @Insert("insert into tb_exam_question(exam_id, type, content, options, answer, score, sort_order) " +
            "values(#{examId}, #{type}, #{content}, #{options}, #{answer}, #{score}, #{sortOrder})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ExamQuestion question);

    @Update("update tb_exam_question set type=#{type}, content=#{content}, options=#{options}, " +
            "answer=#{answer}, score=#{score}, sort_order=#{sortOrder} where id=#{id}")
    int update(ExamQuestion question);

    @Delete("delete from tb_exam_question where id=#{id}")
    int deleteById(@Param("id") Long id);

    @Delete("delete from tb_exam_question where exam_id = #{examId}")
    int deleteByExamId(@Param("examId") Long examId);
}
