package com.edu.course.mapper;

import com.edu.common.entity.Notification;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NotificationMapper {

    @Select("select * from tb_notification where course_id = #{courseId} order by create_time desc")
    List<Notification> selectByCourse(@Param("courseId") Long courseId);

    @Insert("insert into tb_notification(course_id, title, content) values(#{courseId}, #{title}, #{content})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Notification notification);

    @Insert("insert into tb_notification_read(notification_id, student_id) values(#{notificationId}, #{studentId})")
    int markRead(@Param("notificationId") Long notificationId, @Param("studentId") Long studentId);

    @Select("select count(*) from tb_notification_read where notification_id = #{notificationId} and student_id = #{studentId}")
    int countRead(@Param("notificationId") Long notificationId, @Param("studentId") Long studentId);

    @Select("<script>select n.*, " +
            "case when nr.id is not null then 1 else 0 end as is_read " +
            "from tb_notification n " +
            "left join tb_notification_read nr on n.id = nr.notification_id and nr.student_id = #{studentId} " +
            "where n.course_id in (select course_id from tb_course_selection where student_id = #{studentId}) " +
            "order by n.create_time desc</script>")
    List<Notification> selectByStudent(@Param("studentId") Long studentId);

    @Select("select count(*) from tb_notification n " +
            "left join tb_notification_read nr on n.id = nr.notification_id and nr.student_id = #{studentId} " +
            "where n.course_id in (select course_id from tb_course_selection where student_id = #{studentId}) " +
            "and nr.id is null")
    int countUnreadByStudent(@Param("studentId") Long studentId);

}
