package io.github.uchkun07.travelsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.uchkun07.travelsystem.entity.Attraction;
import org.apache.ibatis.annotations.Mapper;

/**
 * 景点表Mapper接口
 */
@Mapper
public interface AttractionMapper extends BaseMapper<Attraction> {
}
