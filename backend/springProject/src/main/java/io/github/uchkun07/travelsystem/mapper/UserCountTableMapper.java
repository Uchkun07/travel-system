package io.github.uchkun07.travelsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.uchkun07.travelsystem.entity.UserCountTable;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户计数表Mapper
 */
@Mapper
public interface UserCountTableMapper extends BaseMapper<UserCountTable> {
}
