package io.github.uchkun07.travelsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.uchkun07.travelsystem.entity.UserPreference;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户偏好Mapper
 */
@Mapper
public interface UserPreferenceMapper extends BaseMapper<UserPreference> {
}
