package org.example.springproject.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.springproject.entity.Slideshow;
import org.example.springproject.mapper.SlideshowMapper;
import org.example.springproject.service.SlideshowService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SlideshowImpl extends ServiceImpl<SlideshowMapper, Slideshow> implements SlideshowService {
    
    @Override
    public List<Slideshow> getActiveSlideshow() {
        return baseMapper.getActiveSlideshow();
    }
    
    @Override
    public boolean recordClick(Integer slideshowId) {
        return baseMapper.incrementClickCount(slideshowId) > 0;
    }
    
    @Override
    public List<Slideshow> getAllSlideshow() {
        QueryWrapper<Slideshow> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("display_order");
        return list(queryWrapper);
    }
    
    @Override
    public boolean updateStatus(Integer slideshowId, Integer status) {
        UpdateWrapper<Slideshow> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("slideshow_id", slideshowId)
                     .set("status", status);
        return update(updateWrapper);
    }
    
    @Override
    @Transactional
    public boolean updateDisplayOrder(List<Slideshow> slideshows) {
        for (Slideshow slideshow : slideshows) {
            UpdateWrapper<Slideshow> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("slideshow_id", slideshow.getSlideshowId())
                         .set("display_order", slideshow.getDisplayOrder());
            update(updateWrapper);
        }
        return true;
    }
}
