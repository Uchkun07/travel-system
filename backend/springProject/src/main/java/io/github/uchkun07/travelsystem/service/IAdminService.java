package io.github.uchkun07.travelsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.github.uchkun07.travelsystem.dto.LoginRequest;
import io.github.uchkun07.travelsystem.dto.LoginResponse;
import io.github.uchkun07.travelsystem.entity.Admin;

public interface IAdminService extends IService<Admin> {
    /**
     * 管理员登录
     *
     * @param loginRequest 登录请求
     * @return 登录响应信息
     */
    LoginResponse login(LoginRequest loginRequest);
}
