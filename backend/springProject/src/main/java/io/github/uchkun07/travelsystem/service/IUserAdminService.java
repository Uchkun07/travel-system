package io.github.uchkun07.travelsystem.service;

import io.github.uchkun07.travelsystem.dto.*;
import io.github.uchkun07.travelsystem.entity.UserCountTable;

/**
 * 用户管理服务接口（管理员侧）
 */
public interface IUserAdminService {

    /**
     * 分页查询用户列表
     */
    PageResponse<UserDetailResponse> queryUsers(UserQueryRequest request);

    /**
     * 获取用户完整详情（包括Profile、Preference和标签）
     */
    UserDetailResponse getUserDetail(Long userId);

    /**
     * 获取用户计数表数据
     */
    UserCountTable getUserCountTable(Long userId);
}
