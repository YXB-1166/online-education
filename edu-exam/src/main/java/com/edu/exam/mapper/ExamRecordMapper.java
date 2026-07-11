package com.edu.exam.mapper;

import com.edu.common.entity.ExamRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ExamRecordMapper {

    @Select("select * from tb_exam_record where id = #{id}")
    ExamRecord selectById(@Param("id") Long id);

    @Select("select * from tb_exam_record where exam_id = #{examId} and student_id = #{studentId}")
    ExamRecord selectByExamAndStudent(@Param("examId") Long examId, @Param("studentId") Long studentId);

    @Select("select * from tb_exam_record where exam_id = #{examId} order by score desc")
    List<ExamRecord> selectByExamId(@Param("examId") Long examId);

    @Select("select * from tb_exam_record where student_id = #{studentId} order by submit_time desc")
    List<ExamRecord> selectByStudentId(@Param("studentId") Long studentId);

    @Insert("insert into tb_exam_record(exam_id, student_id, answers, score, auto_score, graded) " +
            "values(#{examId}, #{studentId}, #{answers}, #{score}, #{autoScore}, #{graded}) " +
            "on duplicate key update answers=#{answers}, score=#{score}, auto_score=#{autoScore}, " +
            "graded=#{graded}, submit_time=now()")
    int upsert(ExamRecord record);

    @Update("update tb_exam_record set score=#{score}, grade=#{grade} where id=#{id}")
    int update(ExamRecord record);
}
