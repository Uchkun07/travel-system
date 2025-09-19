package com.travel.service;

import com.travel.entity.User;
import com.travel.exception.UserAlreadyExistsException;

/**
 * 用户服务接口
 * 定义用户相关的业务操作
 * 
 * @author Travel System
 */
public interface UserService {
    
    /**
     * 根据ID查询用户
     * @param id 用户ID
     * @return 用户信息，不存在返回null
     */
    User getUserById(Long id);
    
    /**
     * 根据用户名查询用户（用于登录）
     * @param username 用户名
     * @return 用户信息，不存在返回null
     */
    User getUserByUsername(String username);
    
    /**
     * 创建新用户（注册）
     * @param user 用户信息
     * @return 创建成功返回true，失败返回false
     * @throws UserAlreadyExistsException 当用户名或手机号已存在时抛出
     */
    boolean createUser(User user) throws UserAlreadyExistsException;
    
    /**
     * 更新用户信息（如修改手机号、邮箱）
     * @param user 用户信息
     * @return 更新成功返回true，失败返回false
     */
    boolean updateUser(User user);
    
    /**
     * 删除用户（逻辑删除，更新状态字段）
     * @param id 用户ID
     * @return 删除成功返回true，失败返回false
     */
    boolean deleteUser(Long id);
    
    /**
     * 验证用户密码
     * @param rawPassword 原始密码
     * @param encodedPassword 加密后的密码
     * @return 密码匹配返回true，否则返回false
     */
    boolean verifyPassword(String rawPassword, String encodedPassword);
    
    /**
     * 更新用户最后登录时间
     * @param id 用户ID
     * @return 更新成功返回true，失败返回false
     */
    boolean updateLastLoginTime(Long id);
}