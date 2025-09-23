package org.example.springproject.mapper;

import org.example.springproject.entity.Attraction;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AttractionMapper extends BaseMapper<Attraction> {
    // 这里可以添加自定义的Mapper方法
}
