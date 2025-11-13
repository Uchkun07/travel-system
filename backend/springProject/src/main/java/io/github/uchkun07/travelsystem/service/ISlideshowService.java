package io.github.uchkun07.travelsystem.service;

import io.github.uchkun07.travelsystem.dto.PageResponse;
import io.github.uchkun07.travelsystem.dto.SlideshowQueryRequest;
import io.github.uchkun07.travelsystem.dto.SlideshowRequest;
import io.github.uchkun07.travelsystem.entity.Slideshow;

import java.util.List;

/**
 * 轮播图服务接口
 */
public interface ISlideshowService {

    /**
     * 创建轮播图
     */
    Slideshow createSlideshow(SlideshowRequest request);

    /**
     * 删除轮播图
     */
    void deleteSlideshow(Integer slideshowId);

    /**
     * 批量删除轮播图
     */
    void batchDeleteSlideshows(List<Integer> slideshowIds);

    /**
     * 修改轮播图
     */
    Slideshow updateSlideshow(SlideshowRequest request);

    /**
     * 分页查询轮播图
     */
    PageResponse<Slideshow> querySlideshows(SlideshowQueryRequest request);

    /**
     * 根据ID查询轮播图
     */
    Slideshow getSlideshowById(Integer slideshowId);

    /**
     * 获取所有启用的轮播图
     */
    List<Slideshow> getActiveSlideshows();
}
