package io.github.uchkun07.travelsystem.service;

import io.github.uchkun07.travelsystem.dto.*;

/**
 * 用户服务接口
 */
public interface IUserService {

    /**
     * 用户注册
     *
     * @param request 注册信息
     * @return 登录响应(包含token)
     */
    UserLoginResponse register(UserRegisterRequest request);

    /**
     * 用户登录
     *
     * @param request 登录信息
     * @return 登录响应(包含token)
     */
    UserLoginResponse login(UserLoginRequest request);

    /**
     * 获取用户信息
     *
     * @param token 访问令牌
     * @return 用户信息
     */
    UserInfoResponse getUserInfo(String token);

    /**
     * 用户登出
     *
     * @param token 访问令牌
     */
    void logout(String token);

    /**
     * 检查用户名是否可用
     *
     * @param username 用户名
     * @return 是否可用
     */
    boolean checkUsernameAvailable(String username);

    /**
     * 检查邮箱是否可用
     *
     * @param email 邮箱
     * @return 是否可用
     */
    boolean checkEmailAvailable(String email);
}
