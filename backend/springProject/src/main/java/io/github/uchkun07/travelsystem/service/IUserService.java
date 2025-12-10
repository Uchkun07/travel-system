package io.github.uchkun07.travelsystem.service;

import io.github.uchkun07.travelsystem.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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

    /**
     * 获取用户基本信息
     *
     * @param token 访问令牌
     * @return 用户基本信息
     */
    UserProfileResponse getUserProfile(String token);

    /**
     * 更新用户基本信息
     *
     * @param token 访问令牌
     * @param request 更新信息
     * @return 更新后的用户基本信息
     */
    UserProfileResponse updateUserProfile(String token, UpdateUserProfileRequest request);

    /**
     * 上传用户头像
     *
     * @param token 访问令牌
     * @param file 头像文件
     * @return 头像上传响应（包含头像URL和新token）
     * @throws IOException 文件上传异常
     */
    AvatarUploadResponse uploadAvatar(String token, MultipartFile file) throws IOException;
}
