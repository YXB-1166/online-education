package com.edu.exam.mapper;

import com.edu.common.entity.ResubmitOpportunity;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ResubmitOpportunityMapper {

    @Select("select * from tb_resubmit_opportunity where assignment_id = #{assignmentId} and student_id = #{studentId}")
    ResubmitOpportunity selectByAssignmentAndStudent(@Param("assignmentId") Long assignmentId, @Param("studentId") Long studentId);

    @Select("select * from tb_resubmit_opportunity where assignment_id = #{assignmentId}")
    List<ResubmitOpportunity> selectByAssignment(@Param("assignmentId") Long assignmentId);

    @Select("select * from tb_resubmit_opportunity where student_id = #{studentId}")
    List<ResubmitOpportunity> selectByStudent(@Param("studentId") Long studentId);

    @Insert("insert into tb_resubmit_opportunity(assignment_id, student_id, deadline) " +
            "values(#{assignmentId}, #{studentId}, #{deadline}) " +
            "on duplicate key update deadline=#{deadline}")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int upsert(ResubmitOpportunity opportunity);

    @Delete("delete from tb_resubmit_opportunity where assignment_id = #{assignmentId} and student_id = #{studentId}")
    int deleteByAssignmentAndStudent(@Param("assignmentId") Long assignmentId, @Param("studentId") Long studentId);

}
