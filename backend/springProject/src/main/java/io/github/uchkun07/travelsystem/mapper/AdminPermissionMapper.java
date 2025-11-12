package io.github.uchkun07.travelsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.uchkun07.travelsystem.entity.AdminPermission;
import org.apache.ibatis.annotations.Mapper;

/**
 * 管理员权限Mapper接口
 */
@Mapper
public interface AdminPermissionMapper extends BaseMapper<AdminPermission> {
}
