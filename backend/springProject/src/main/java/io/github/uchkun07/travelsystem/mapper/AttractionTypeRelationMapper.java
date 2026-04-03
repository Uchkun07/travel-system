package io.github.uchkun07.travelsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.uchkun07.travelsystem.entity.AttractionTypeRelation;
import org.apache.ibatis.annotations.Mapper;

/**
 * 景点-类型关联Mapper接口
 */
@Mapper
public interface AttractionTypeRelationMapper extends BaseMapper<AttractionTypeRelation> {
}
