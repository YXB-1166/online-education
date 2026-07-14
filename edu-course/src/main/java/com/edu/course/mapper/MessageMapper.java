package com.edu.course.mapper;

import com.edu.common.entity.Message;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MessageMapper {

    @Insert("insert into tb_message(sender_id, receiver_id, content) values(#{senderId}, #{receiverId}, #{content})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Message message);

    @Select("select m.*, u1.real_name as senderName, u2.real_name as receiverName " +
            "from tb_message m " +
            "join tb_user u1 on m.sender_id = u1.id " +
            "join tb_user u2 on m.receiver_id = u2.id " +
            "where (m.sender_id = #{userId} or m.receiver_id = #{userId}) " +
            "order by m.create_time desc")
    List<Message> selectByUser(@Param("userId") Long userId);

    @Select("select m.*, u1.real_name as senderName, u2.real_name as receiverName " +
            "from tb_message m " +
            "join tb_user u1 on m.sender_id = u1.id " +
            "join tb_user u2 on m.receiver_id = u2.id " +
            "where ((m.sender_id = #{userId} and m.receiver_id = #{otherId}) " +
            "   or (m.sender_id = #{otherId} and m.receiver_id = #{userId})) " +
            "order by m.create_time asc")
    List<Message> selectConversation(@Param("userId") Long userId, @Param("otherId") Long otherId);

    @Update("update tb_message set is_read = 1 where receiver_id = #{userId} and sender_id = #{otherId} and is_read = 0")
    int markRead(@Param("userId") Long userId, @Param("otherId") Long otherId);

    @Select("select count(*) from tb_message where receiver_id = #{userId} and is_read = 0")
    long countUnread(@Param("userId") Long userId);

    @Select("select distinct case when sender_id = #{userId} then receiver_id else sender_id end as contactId " +
            "from tb_message where sender_id = #{userId} or receiver_id = #{userId}")
    List<Long> selectContactIds(@Param("userId") Long userId);
}
