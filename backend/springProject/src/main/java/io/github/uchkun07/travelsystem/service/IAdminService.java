package io.github.uchkun07.travelsystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.uchkun07.travelsystem.dto.*;
import io.github.uchkun07.travelsystem.entity.Admin;

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
     * 管理员登出
     * @param token JWT令牌
     */
    void logout(String token);

    /**
     * 根据管理员ID获取权限列表
     * @param adminId 管理员ID
     * @return 权限编码列表
     */
    List<String> getAdminPermissions(Long adminId);

    /**
     * 创建管理员
     * @param request 创建请求
     * @return 创建的管理员
     */
    Admin createAdmin(AdminCreateRequest request);

    /**
     * 删除管理员
     * @param adminId 管理员ID
     */
    void deleteAdmin(Long adminId);

    /**
     * 批量删除管理员
     * @param adminIds 管理员ID列表
     */
    void batchDeleteAdmins(List<Long> adminIds);

    /**
     * 更新管理员信息
     * @param request 更新请求
     * @return 更新后的管理员
     */
    Admin updateAdmin(AdminUpdateRequest request);

    /**
     * 修改管理员密码
     * @param request 密码修改请求
     */
    void updatePassword(AdminPasswordUpdateRequest request);

    /**
     * 分页查询管理员
     * @param request 查询请求
     * @return 分页结果
     */
    Page<Admin> queryAdmins(AdminQueryRequest request);

    /**
     * 根据ID查询管理员详情
     * @param adminId 管理员ID
     * @return 管理员详情
     */
    Admin getAdminById(Long adminId);
}
