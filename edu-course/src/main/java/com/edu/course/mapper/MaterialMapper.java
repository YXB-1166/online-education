package com.edu.course.mapper;

import com.edu.common.entity.Material;
import com.edu.common.entity.MaterialReadRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MaterialMapper {

    @Select("select * from tb_material where course_id = #{courseId} order by create_time desc")
    List<Material> selectByCourse(@Param("courseId") Long courseId);

    @Select("select * from tb_material where id = #{id}")
    Material selectById(@Param("id") Long id);

    @Insert("insert into tb_material(course_id, teacher_id, title, content, required_seconds) " +
            "values(#{courseId}, #{teacherId}, #{title}, #{content}, #{requiredSeconds})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Material material);

    @Delete("delete from tb_material where id = #{id}")
    int deleteById(@Param("id") Long id);

    @Select("select count(*) from tb_material_read where material_id = #{materialId} and student_id = #{studentId}")
    int countRead(@Param("materialId") Long materialId, @Param("studentId") Long studentId);

    @Insert("insert into tb_material_read(material_id, student_id) values(#{materialId}, #{studentId})")
    int insertRead(@Param("materialId") Long materialId, @Param("studentId") Long studentId);

    @Select("select * from tb_material_read where material_id = #{materialId} and student_id = #{studentId}")
    MaterialReadRecord selectReadRecord(@Param("materialId") Long materialId, @Param("studentId") Long studentId);

    @Update("update tb_material_read set completed = 1, completed_time = now() " +
            "where material_id = #{materialId} and student_id = #{studentId}")
    int markComplete(@Param("materialId") Long materialId, @Param("studentId") Long studentId);

}
