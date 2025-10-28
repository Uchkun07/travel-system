package org.example.springproject.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.springproject.entity.Slideshow;

import java.util.List;

public interface SlideshowService extends IService<Slideshow> {
    
    /**
     * 获取所有启用的轮播图(前台展示)
     */
    List<Slideshow> getActiveSlideshow();
    
    /**
     * 记录轮播图点击
     */
    boolean recordClick(Integer slideshowId);
    
    /**
     * 获取所有轮播图(管理后台,按显示顺序排序)
     */
    List<Slideshow> getAllSlideshow();
    
    /**
     * 更新轮播图状态
     */
    boolean updateStatus(Integer slideshowId, Integer status);
    
    /**
     * 批量更新显示顺序
     */
    boolean updateDisplayOrder(List<Slideshow> slideshows);
}
