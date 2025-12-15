package io.github.uchkun07.travelsystem.service;

import io.github.uchkun07.travelsystem.dto.UserPreferenceRequest;
import io.github.uchkun07.travelsystem.dto.UserPreferenceResponse;

/**
 * 用户偏好服务接口
 */
public interface IUserPreferenceService {

    /**
     * 获取用户偏好
     * @param userId 用户ID
     * @return 用户偏好
     */
    UserPreferenceResponse getUserPreference(Long userId);

    /**
     * 更新用户偏好
     * @param userId 用户ID
     * @param request 偏好信息
     * @return 更新后的偏好
     */
    UserPreferenceResponse updateUserPreference(Long userId, UserPreferenceRequest request);

    /**
     * 删除用户偏好
     * @param userId 用户ID
     */
    void deleteUserPreference(Long userId);

    /**
     * 初始化用户偏好（注册时调用）
     * @param userId 用户ID
     */
    void initializeUserPreference(Long userId);
}
