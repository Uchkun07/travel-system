package com.travel.mapper;

import com.travel.entity.User;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;

/**
 * 用户数据访问层接口
 * 使用MyBatis进行数据库操作
 * 
 * @author Travel System
 */
@Mapper
public interface UserMapper {
    
    /**
     * 根据ID查询用户
     * @param id 用户ID
     * @return 用户信息，不存在返回null
     */
    @Select("SELECT * FROM user WHERE id = #{id}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "username", column = "username"),
        @Result(property = "password", column = "password"),
        @Result(property = "phone", column = "phone"),
        @Result(property = "email", column = "email"),
        @Result(property = "createdTime", column = "created_time"),
        @Result(property = "lastLoginTime", column = "last_login_time"),
        @Result(property = "status", column = "status"),
        @Result(property = "role", column = "role"),
        @Result(property = "updatedTime", column = "updated_time")
    })
    User selectById(Long id);
    
    /**
     * 根据用户名查询用户（用于登录验证）
     * @param username 用户名
     * @return 用户信息，不存在返回null
     */
    @Select("SELECT * FROM user WHERE username = #{username}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "username", column = "username"),
        @Result(property = "password", column = "password"),
        @Result(property = "phone", column = "phone"),
        @Result(property = "email", column = "email"),
        @Result(property = "createdTime", column = "created_time"),
        @Result(property = "lastLoginTime", column = "last_login_time"),
        @Result(property = "status", column = "status"),
        @Result(property = "role", column = "role"),
        @Result(property = "updatedTime", column = "updated_time")
    })
    User selectByUsername(String username);
    
    /**
     * 根据手机号查询用户
     * @param phone 手机号
     * @return 用户信息，不存在返回null
     */
    @Select("SELECT * FROM user WHERE phone = #{phone}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "username", column = "username"),
        @Result(property = "password", column = "password"),
        @Result(property = "phone", column = "phone"),
        @Result(property = "email", column = "email"),
        @Result(property = "createdTime", column = "created_time"),
        @Result(property = "lastLoginTime", column = "last_login_time"),
        @Result(property = "status", column = "status"),
        @Result(property = "role", column = "role"),
        @Result(property = "updatedTime", column = "updated_time")
    })
    User selectByPhone(String phone);
    
    /**
     * 插入新用户
     * @param user 用户信息
     * @return 插入的记录数
     */
    @Insert("INSERT INTO user (username, password, phone, email, created_time, status, role) " +
            "VALUES (#{username}, #{password}, #{phone}, #{email}, #{createdTime}, #{status}, #{role})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);
    
    /**
     * 更新用户信息
     * @param user 用户信息
     * @return 更新的记录数
     */
    @Update("UPDATE user SET phone = #{phone}, email = #{email}, updated_time = #{updatedTime} " +
            "WHERE id = #{id}")
    int update(User user);
    
    /**
     * 逻辑删除用户（更新状态为禁用）
     * @param id 用户ID
     * @param updatedTime 更新时间
     * @return 更新的记录数
     */
    @Update("UPDATE user SET status = 0, updated_time = #{updatedTime} WHERE id = #{id}")
    int deleteById(@Param("id") Long id, @Param("updatedTime") LocalDateTime updatedTime);
    
    /**
     * 更新用户最后登录时间
     * @param id 用户ID
     * @param lastLoginTime 最后登录时间
     * @return 更新的记录数
     */
    @Update("UPDATE user SET last_login_time = #{lastLoginTime}, updated_time = #{updatedTime} " +
            "WHERE id = #{id}")
    int updateLastLoginTime(@Param("id") Long id, 
                           @Param("lastLoginTime") LocalDateTime lastLoginTime,
                           @Param("updatedTime") LocalDateTime updatedTime);
}