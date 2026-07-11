package com.edu.exam.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface KnowledgePointMapper {

    @Select("select k.content from tb_knowledge_point k " +
            "join tb_chapter c on k.chapter_id = c.id " +
            "where c.course_id = #{courseId} order by k.id")
    List<String> selectByCourseId(@Param("courseId") Long courseId);
}
