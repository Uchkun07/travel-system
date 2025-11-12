package io.github.uchkun07.travelsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.uchkun07.travelsystem.entity.AdminRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 管理员角色Mapper接口
 */
@Mapper
public interface AdminRoleMapper extends BaseMapper<AdminRole> {
}
