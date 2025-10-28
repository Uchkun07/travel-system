package org.example.springproject.mapper;

import org.example.springproject.entity.Slideshow;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SlideshowMapper extends BaseMapper<Slideshow> {
    
    /**
     * 获取所有启用的轮播图(按显示顺序排序)
     */
    @Select("SELECT * FROM slideshow WHERE status = 1 " +
            "AND (start_time IS NULL OR start_time <= NOW()) " +
            "AND (end_time IS NULL OR end_time >= NOW()) " +
            "ORDER BY display_order ASC")
    List<Slideshow> getActiveSlideshow();
    
    /**
     * 增加点击次数
     */
    @Update("UPDATE slideshow SET click_count = click_count + 1 WHERE slideshow_id = #{slideshowId}")
    int incrementClickCount(Integer slideshowId);
}
