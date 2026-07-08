package com.edu.user.mapper;

import com.edu.common.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("select * from tb_user where id = #{id}")
    User selectById(@Param("id") Long id);

    @Select("select * from tb_user where username = #{username}")
    User selectByUsername(@Param("username") String username);

    @Select("<script>select * from tb_user where 1=1" +
            "<if test='username != null'> and username like concat('%',#{username},'%')</if>" +
            "<if test='realName != null'> and real_name like concat('%',#{realName},'%')</if>" +
            "<if test='role != null'> and role = #{role}</if>" +
            " order by id</script>")
    List<User> selectList(User user);

    @Select("<script>select count(*) from tb_user where 1=1" +
            "<if test='username != null'> and username like concat('%',#{username},'%')</if>" +
            "<if test='realName != null'> and real_name like concat('%',#{realName},'%')</if>" +
            "<if test='role != null'> and role = #{role}</if>" +
            "</script>")
    long countList(User user);

    @Select("<script>select * from tb_user where 1=1" +
            "<if test='u.username != null'> and username like concat('%',#{u.username},'%')</if>" +
            "<if test='u.realName != null'> and real_name like concat('%',#{u.realName},'%')</if>" +
            "<if test='u.role != null'> and role = #{u.role}</if>" +
            " order by id limit #{offset}, #{size}</script>")
    List<User> selectPage(@Param("u") User user, @Param("offset") int offset, @Param("size") int size);

    @Insert("insert into tb_user(username, password, real_name, role, email, phone, title, status) " +
            "values(#{username}, #{password}, #{realName}, #{role}, #{email}, #{phone}, #{title}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Update("update tb_user set username=#{username}, real_name=#{realName}, role=#{role}, " +
            "email=#{email}, phone=#{phone}, title=#{title}, status=#{status} where id=#{id}")
    int update(User user);

    @Delete("delete from tb_user where id=#{id}")
    int deleteById(@Param("id") Long id);

}
