package com.edu.course.mapper;

import com.edu.common.entity.ForumLike;
import org.apache.ibatis.annotations.*;

@Mapper
public interface ForumLikeMapper {

    @Select("select count(*) from tb_forum_like where post_id = #{postId} and user_id = #{userId}")
    int exists(@Param("postId") Long postId, @Param("userId") Long userId);

    @Insert("insert into tb_forum_like(post_id, user_id) values(#{postId}, #{userId})")
    int insert(ForumLike forumLike);

    @Delete("delete from tb_forum_like where post_id = #{postId} and user_id = #{userId}")
    int deleteByPostIdAndUserId(@Param("postId") Long postId, @Param("userId") Long userId);

}
