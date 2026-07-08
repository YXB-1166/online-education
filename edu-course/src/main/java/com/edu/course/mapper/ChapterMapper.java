package com.edu.course.mapper;

import com.edu.common.entity.Chapter;
import com.edu.common.entity.KnowledgePoint;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ChapterMapper {

    @Select("select * from tb_chapter where course_id = #{courseId} order by sort_order")
    List<Chapter> selectByCourse(@Param("courseId") Long courseId);

    @Select("select * from tb_knowledge_point where chapter_id = #{chapterId} order by sort_order")
    List<KnowledgePoint> selectKnowledgePoints(@Param("chapterId") Long chapterId);

    @Select("select * from tb_chapter where id = #{id}")
    Chapter selectById(@Param("id") Long id);

    @Insert("insert into tb_chapter(course_id, title, summary, sort_order) values(#{courseId}, #{title}, #{summary}, #{sortOrder})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Chapter chapter);

    @Insert("<script>insert into tb_knowledge_point(chapter_id, content, importance, sort_order) values " +
            "<foreach collection='list' item='kp' separator=','>(#{kp.chapterId}, #{kp.content}, #{kp.importance}, #{kp.sortOrder})</foreach></script>")
    int batchInsertKnowledgePoints(@Param("list") List<KnowledgePoint> list);

    @Delete("delete from tb_knowledge_point where chapter_id in (select id from tb_chapter where course_id = #{courseId})")
    int deleteKnowledgePointsByCourse(@Param("courseId") Long courseId);

    @Delete("delete from tb_chapter where course_id = #{courseId}")
    int deleteByCourse(@Param("courseId") Long courseId);

}
