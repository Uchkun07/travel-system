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
}
