package io.github.uchkun07.travelsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.uchkun07.travelsystem.entity.UserTag;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户标签关联Mapper
 */
@Mapper
public interface UserTagMapper extends BaseMapper<UserTag> {
}
