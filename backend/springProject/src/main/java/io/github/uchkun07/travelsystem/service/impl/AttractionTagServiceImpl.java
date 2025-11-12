package io.github.uchkun07.travelsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.uchkun07.travelsystem.dto.AttractionTagQueryRequest;
import io.github.uchkun07.travelsystem.dto.AttractionTagRequest;
import io.github.uchkun07.travelsystem.dto.PageResponse;
import io.github.uchkun07.travelsystem.entity.AttractionTag;
import io.github.uchkun07.travelsystem.mapper.AttractionTagMapper;
import io.github.uchkun07.travelsystem.service.IAttractionTagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class AttractionTagServiceImpl implements IAttractionTagService {

    @Autowired
    private AttractionTagMapper tagMapper;

    @Override
    @Transactional
    public AttractionTag createTag(AttractionTagRequest request) {
        // 检查标签名称是否已存在
        LambdaQueryWrapper<AttractionTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AttractionTag::getTagName, request.getTagName());
        if (tagMapper.selectCount(wrapper) > 0) {
            throw new IllegalArgumentException("标签名称已存在: " + request.getTagName());
        }

        AttractionTag tag = AttractionTag.builder()
                .tagName(request.getTagName())
                .description(request.getDescription())
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .status(request.getStatus() != null ? request.getStatus() : 1)
                .build();

        tagMapper.insert(tag);
        log.info("创建标签成功: {}", tag.getTagName());
        return tag;
    }

    @Override
    @Transactional
    public void deleteTag(Integer tagId) {
        AttractionTag tag = tagMapper.selectById(tagId);
        if (tag == null) {
            throw new IllegalArgumentException("标签不存在");
        }
        tagMapper.deleteById(tagId);
        log.info("删除标签成功: {}", tag.getTagName());
    }

    @Override
    @Transactional
    public void batchDeleteTags(List<Integer> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            throw new IllegalArgumentException("标签ID列表不能为空");
        }
        LambdaQueryWrapper<AttractionTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(AttractionTag::getTagId, tagIds);
        int count = tagMapper.delete(wrapper);
        log.info("批量删除标签成功: 删除{}条", count);
    }

    @Override
    @Transactional
    public AttractionTag updateTag(AttractionTagRequest request) {
        AttractionTag tag = tagMapper.selectById(request.getTagId());
        if (tag == null) {
            throw new IllegalArgumentException("标签不存在");
        }

        // 检查标签名称是否与其他标签重复
        if (request.getTagName() != null && !request.getTagName().equals(tag.getTagName())) {
            LambdaQueryWrapper<AttractionTag> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AttractionTag::getTagName, request.getTagName())
                   .ne(AttractionTag::getTagId, request.getTagId());
            if (tagMapper.selectCount(wrapper) > 0) {
                throw new IllegalArgumentException("标签名称已存在: " + request.getTagName());
            }
            tag.setTagName(request.getTagName());
        }

        if (request.getDescription() != null) tag.setDescription(request.getDescription());
        if (request.getSortOrder() != null) tag.setSortOrder(request.getSortOrder());
        if (request.getStatus() != null) tag.setStatus(request.getStatus());

        tagMapper.updateById(tag);
        log.info("更新标签成功: {}", tag.getTagName());
        return tag;
    }

    @Override
    public PageResponse<AttractionTag> queryTags(AttractionTagQueryRequest request) {
        LambdaQueryWrapper<AttractionTag> wrapper = new LambdaQueryWrapper<>();

        if (request.getTagName() != null && !request.getTagName().trim().isEmpty()) {
            wrapper.like(AttractionTag::getTagName, request.getTagName().trim());
        }
        if (request.getStatus() != null) {
            wrapper.eq(AttractionTag::getStatus, request.getStatus());
        }

        wrapper.orderByAsc(AttractionTag::getSortOrder)
               .orderByDesc(AttractionTag::getCreateTime);

        Page<AttractionTag> page = new Page<>(request.getPageNum(), request.getPageSize());
        Page<AttractionTag> result = tagMapper.selectPage(page, wrapper);
        return PageResponse.of(result);
    }

    @Override
    public AttractionTag getTagById(Integer tagId) {
        AttractionTag tag = tagMapper.selectById(tagId);
        if (tag == null) {
            throw new IllegalArgumentException("标签不存在");
        }
        return tag;
    }

    @Override
    public List<AttractionTag> getAllTags() {
        LambdaQueryWrapper<AttractionTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AttractionTag::getStatus, 1)
               .orderByAsc(AttractionTag::getSortOrder);
        return tagMapper.selectList(wrapper);
    }
}
