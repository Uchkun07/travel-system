package io.github.uchkun07.travelsystem.service;

import io.github.uchkun07.travelsystem.dto.AdminLoginRequest;
import io.github.uchkun07.travelsystem.dto.AdminLoginResponse;

import java.util.List;

/**
 * 管理员服务接口
 */
public interface IAdminService {

    /**
     * 管理员登录
     * @param request 登录请求
     * @return 登录响应（包含token和权限）
     */
    AdminLoginResponse login(AdminLoginRequest request);

    /**
     * 根据管理员ID获取权限列表
     * @param adminId 管理员ID
     * @return 权限编码列表
     */
    List<String> getAdminPermissions(Long adminId);
}
