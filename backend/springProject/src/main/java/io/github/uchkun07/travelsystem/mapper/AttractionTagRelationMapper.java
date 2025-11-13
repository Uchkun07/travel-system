package io.github.uchkun07.travelsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.uchkun07.travelsystem.entity.AttractionTagRelation;
import org.apache.ibatis.annotations.Mapper;

/**
 * 景点-标签关联Mapper接口
 */
@Mapper
public interface AttractionTagRelationMapper extends BaseMapper<AttractionTagRelation> {
}
