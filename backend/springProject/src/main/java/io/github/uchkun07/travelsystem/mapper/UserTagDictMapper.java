package io.github.uchkun07.travelsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.uchkun07.travelsystem.entity.UserTagDict;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户标签字典Mapper
 */
@Mapper
public interface UserTagDictMapper extends BaseMapper<UserTagDict> {
}
