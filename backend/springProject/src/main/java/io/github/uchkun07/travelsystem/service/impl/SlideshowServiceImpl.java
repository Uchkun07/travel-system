package io.github.uchkun07.travelsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.uchkun07.travelsystem.dto.PageResponse;
import io.github.uchkun07.travelsystem.dto.SlideshowQueryRequest;
import io.github.uchkun07.travelsystem.dto.SlideshowRequest;
import io.github.uchkun07.travelsystem.entity.Slideshow;
import io.github.uchkun07.travelsystem.mapper.SlideshowMapper;
import io.github.uchkun07.travelsystem.service.ISlideshowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class SlideshowServiceImpl implements ISlideshowService {

    @Autowired
    private SlideshowMapper slideshowMapper;

    @Override
    @Transactional
    public Slideshow createSlideshow(SlideshowRequest request) {
        Slideshow slideshow = Slideshow.builder()
                .title(request.getTitle())
                .subtitle(request.getSubtitle())
                .imageUrl(request.getImageUrl())
                .attractionId(request.getAttractionId())
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .status(request.getStatus() != null ? request.getStatus() : 1)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .clickCount(0)
                .build();

        slideshowMapper.insert(slideshow);
        log.info("创建轮播图成功: {}", slideshow.getTitle());
        return slideshow;
    }

    @Override
    @Transactional
    public void deleteSlideshow(Integer slideshowId) {
        Slideshow slideshow = slideshowMapper.selectById(slideshowId);
        if (slideshow == null) {
            throw new IllegalArgumentException("轮播图不存在");
        }
        slideshowMapper.deleteById(slideshowId);
        log.info("删除轮播图成功: {}", slideshow.getTitle());
    }

    @Override
    @Transactional
    public void batchDeleteSlideshows(List<Integer> slideshowIds) {
        if (slideshowIds == null || slideshowIds.isEmpty()) {
            throw new IllegalArgumentException("轮播图ID列表不能为空");
        }
        int count = slideshowMapper.deleteBatchIds(slideshowIds);
        log.info("批量删除轮播图成功: 删除{}条", count);
    }

    @Override
    @Transactional
    public Slideshow updateSlideshow(SlideshowRequest request) {
        Slideshow slideshow = slideshowMapper.selectById(request.getSlideshowId());
        if (slideshow == null) {
            throw new IllegalArgumentException("轮播图不存在");
        }

        if (request.getTitle() != null) slideshow.setTitle(request.getTitle());
        if (request.getSubtitle() != null) slideshow.setSubtitle(request.getSubtitle());
        if (request.getImageUrl() != null) slideshow.setImageUrl(request.getImageUrl());
        if (request.getAttractionId() != null) slideshow.setAttractionId(request.getAttractionId());
        if (request.getDisplayOrder() != null) slideshow.setDisplayOrder(request.getDisplayOrder());
        if (request.getStatus() != null) slideshow.setStatus(request.getStatus());
        if (request.getStartTime() != null) slideshow.setStartTime(request.getStartTime());
        if (request.getEndTime() != null) slideshow.setEndTime(request.getEndTime());

        slideshowMapper.updateById(slideshow);
        log.info("更新轮播图成功: {}", slideshow.getTitle());
        return slideshow;
    }

    @Override
    public PageResponse<Slideshow> querySlideshows(SlideshowQueryRequest request) {
        LambdaQueryWrapper<Slideshow> wrapper = new LambdaQueryWrapper<>();

        if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
            wrapper.like(Slideshow::getTitle, request.getTitle().trim());
        }
        if (request.getStatus() != null) {
            wrapper.eq(Slideshow::getStatus, request.getStatus());
        }
        if (request.getAttractionId() != null) {
            wrapper.eq(Slideshow::getAttractionId, request.getAttractionId());
        }

        wrapper.orderByAsc(Slideshow::getDisplayOrder)
               .orderByDesc(Slideshow::getCreateTime);

        Page<Slideshow> page = new Page<>(request.getPageNum(), request.getPageSize());
        Page<Slideshow> result = slideshowMapper.selectPage(page, wrapper);
        return PageResponse.of(result);
    }

    @Override
    public Slideshow getSlideshowById(Integer slideshowId) {
        Slideshow slideshow = slideshowMapper.selectById(slideshowId);
        if (slideshow == null) {
            throw new IllegalArgumentException("轮播图不存在");
        }
        return slideshow;
    }

    @Override
    public List<Slideshow> getActiveSlideshows() {
        LocalDateTime now = LocalDateTime.now();
        LambdaQueryWrapper<Slideshow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Slideshow::getStatus, 1)
               .and(w -> w.isNull(Slideshow::getStartTime)
                          .or()
                          .le(Slideshow::getStartTime, now))
               .and(w -> w.isNull(Slideshow::getEndTime)
                          .or()
                          .ge(Slideshow::getEndTime, now))
               .orderByAsc(Slideshow::getDisplayOrder);
        return slideshowMapper.selectList(wrapper);
    }
}
