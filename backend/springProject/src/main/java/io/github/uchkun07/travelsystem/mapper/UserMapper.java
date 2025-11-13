package io.github.uchkun07.travelsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.uchkun07.travelsystem.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户账号Mapper
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
