package io.github.uchkun07.travelsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.uchkun07.travelsystem.entity.UserProfile;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户基本信息Mapper
 */
@Mapper
public interface UserProfileMapper extends BaseMapper<UserProfile> {
}
