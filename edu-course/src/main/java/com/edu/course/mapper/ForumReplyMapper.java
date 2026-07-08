package com.edu.course.mapper;

import com.edu.common.entity.ForumReply;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ForumReplyMapper {

    @Select("select * from tb_forum_reply where post_id = #{postId} order by create_time")
    List<ForumReply> selectByPostId(@Param("postId") Long postId);

    @Insert("insert into tb_forum_reply(post_id, user_id, content) values(#{postId}, #{userId}, #{content})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ForumReply reply);

    @Delete("delete from tb_forum_reply where id = #{id}")
    int deleteById(@Param("id") Long id);
}
