package com.edu.course.mapper;

import com.edu.common.entity.ForumPost;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ForumPostMapper {

    @Select("select * from tb_forum_post where id = #{id}")
    ForumPost selectById(@Param("id") Long id);

    @Select("<script>select * from tb_forum_post where 1=1" +
            "<if test='courseId != null'> and course_id = #{courseId}</if>" +
            "<if test='userId != null'> and user_id = #{userId}</if>" +
            "<if test='status != null'> and status = #{status}</if>" +
            " order by status desc, create_time desc</script>")
    List<ForumPost> selectList(ForumPost forumPost);

    @Select("<script>select count(*) from tb_forum_post where 1=1" +
            "<if test='courseId != null'> and course_id = #{courseId}</if>" +
            "<if test='userId != null'> and user_id = #{userId}</if>" +
            "<if test='status != null'> and status = #{status}</if>" +
            "</script>")
    long countList(ForumPost forumPost);

    @Select("<script>select * from tb_forum_post where 1=1" +
            "<if test='p.courseId != null'> and course_id = #{p.courseId}</if>" +
            "<if test='p.userId != null'> and user_id = #{p.userId}</if>" +
            "<if test='p.status != null'> and status = #{p.status}</if>" +
            " order by status desc, create_time desc limit #{offset}, #{size}</script>")
    List<ForumPost> selectPage(@Param("p") ForumPost forumPost, @Param("offset") int offset, @Param("size") int size);

    @Insert("insert into tb_forum_post(course_id, user_id, title, content, status) " +
            "values(#{courseId}, #{userId}, #{title}, #{content}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ForumPost forumPost);

    @Update("update tb_forum_post set title=#{title}, content=#{content}, status=#{status} where id=#{id}")
    int update(ForumPost forumPost);

    @Delete("delete from tb_forum_post where id=#{id}")
    int deleteById(@Param("id") Long id);

    @Update("update tb_forum_post set reply_count = reply_count + 1 where id = #{id}")
    int incrementReply(@Param("id") Long id);

}
