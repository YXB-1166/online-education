package com.edu.course.mapper;

import com.edu.common.entity.Material;
import com.edu.common.entity.MaterialReadRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

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

    @Update("update tb_material_read set read_time = now() " +
            "where material_id = #{materialId} and student_id = #{studentId} and completed = 0")
    int resetReadTime(@Param("materialId") Long materialId, @Param("studentId") Long studentId);

    @Update("update tb_material_read set completed = 0, completed_time = null, read_time = now() " +
            "where material_id = #{materialId} and student_id = #{studentId} and completed = 1")
    int restartRead(@Param("materialId") Long materialId, @Param("studentId") Long studentId);

    @Update("update tb_material_read set completed = 1, completed_time = now() " +
            "where material_id = #{materialId} and student_id = #{studentId}")
    int markComplete(@Param("materialId") Long materialId, @Param("studentId") Long studentId);

    @Select("select m.*, coalesce(mr.completed, 0) as completed " +
            "from tb_material m " +
            "left join tb_material_read mr on m.id = mr.material_id and mr.student_id = #{studentId} " +
            "where m.course_id = #{courseId} order by m.create_time desc")
    List<Map<String, Object>> selectByCourseWithStatus(@Param("courseId") Long courseId, @Param("studentId") Long studentId);

}
