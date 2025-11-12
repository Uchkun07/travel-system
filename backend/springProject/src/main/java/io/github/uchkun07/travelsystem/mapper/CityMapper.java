package io.github.uchkun07.travelsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.uchkun07.travelsystem.entity.City;
import org.apache.ibatis.annotations.Mapper;

/**
 * 城市Mapper接口
 */
@Mapper
public interface CityMapper extends BaseMapper<City> {
}
