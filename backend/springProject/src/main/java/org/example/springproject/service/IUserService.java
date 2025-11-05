package org.example.springproject.service;

import org.example.springproject.entity.User;
import org.example.springproject.entity.dto.LoginRequest;
import org.example.springproject.entity.dto.RegisterRequest;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

public interface IUserService extends IService<User> {
    
    /**
     * 用户注册
     *
     * @param request 注册请求
     * @return 注册结果
     */
    Map<String, Object> register(RegisterRequest request);

    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return 登录结果(包含token等信息)
     */
    Map<String, Object> login(LoginRequest request);

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    User getUserByUsername(String username);

    /**
     * 根据邮箱查询用户
     *
     * @param email 邮箱
     * @return 用户信息
     */
    User getUserByEmail(String email);

    /**
     * 修改密码
     *
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 修改结果
     */
    Map<String, Object> changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 修改用户名
     *
     * @param userId 用户ID
     * @param newUsername 新用户名
     * @return 修改结果（包含新token）
     */
    Map<String, Object> changeUsername(Long userId, String newUsername);

    /**
     * 修改邮箱
     *
     * @param userId 用户ID
     * @param newEmail 新邮箱
     * @param captcha 验证码
     * @return 修改结果
     */
    Map<String, Object> changeEmail(Long userId, String newEmail, String captcha);
}
