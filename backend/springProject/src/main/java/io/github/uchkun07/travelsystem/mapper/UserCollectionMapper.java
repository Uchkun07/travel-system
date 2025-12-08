package io.github.uchkun07.travelsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.uchkun07.travelsystem.entity.UserCollection;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户收藏Mapper
 */
@Mapper
public interface UserCollectionMapper extends BaseMapper<UserCollection> {
}
