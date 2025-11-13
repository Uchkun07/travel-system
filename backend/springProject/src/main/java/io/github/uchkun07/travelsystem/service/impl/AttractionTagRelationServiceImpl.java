package io.github.uchkun07.travelsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.uchkun07.travelsystem.dto.AttractionTagBatchBindRequest;
import io.github.uchkun07.travelsystem.dto.AttractionTagBindRequest;
import io.github.uchkun07.travelsystem.dto.AttractionTagUnbindRequest;
import io.github.uchkun07.travelsystem.entity.AttractionTag;
import io.github.uchkun07.travelsystem.entity.AttractionTagRelation;
import io.github.uchkun07.travelsystem.mapper.AttractionTagMapper;
import io.github.uchkun07.travelsystem.mapper.AttractionTagRelationMapper;
import io.github.uchkun07.travelsystem.service.IAttractionTagRelationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AttractionTagRelationServiceImpl implements IAttractionTagRelationService {

    @Autowired
    private AttractionTagRelationMapper relationMapper;

    @Autowired
    private AttractionTagMapper tagMapper;

    @Override
    @Transactional
    public void bindTag(AttractionTagBindRequest request) {
        if (request.getAttractionId() == null) {
            throw new IllegalArgumentException("景点ID不能为空");
        }
        if (request.getTagId() == null) {
            throw new IllegalArgumentException("标签ID不能为空");
        }

        // 检查标签是否存在
        AttractionTag tag = tagMapper.selectById(request.getTagId());
        if (tag == null) {
            throw new IllegalArgumentException("标签不存在");
        }

        // 检查是否已经绑定
        LambdaQueryWrapper<AttractionTagRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AttractionTagRelation::getAttractionId, request.getAttractionId())
               .eq(AttractionTagRelation::getTagId, request.getTagId());
        
        if (relationMapper.selectCount(wrapper) > 0) {
            throw new IllegalArgumentException("该标签已经绑定到此景点");
        }

        // 创建绑定关系
        AttractionTagRelation relation = AttractionTagRelation.builder()
                .attractionId(request.getAttractionId())
                .tagId(request.getTagId())
                .build();

        relationMapper.insert(relation);
        log.info("景点 {} 绑定标签 {} 成功", request.getAttractionId(), tag.getTagName());
    }

    @Override
    @Transactional
    public void unbindTag(AttractionTagUnbindRequest request) {
        if (request.getAttractionId() == null) {
            throw new IllegalArgumentException("景点ID不能为空");
        }
        if (request.getTagId() == null) {
            throw new IllegalArgumentException("标签ID不能为空");
        }

        // 查找绑定关系
        LambdaQueryWrapper<AttractionTagRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AttractionTagRelation::getAttractionId, request.getAttractionId())
               .eq(AttractionTagRelation::getTagId, request.getTagId());

        AttractionTagRelation relation = relationMapper.selectOne(wrapper);
        if (relation == null) {
            throw new IllegalArgumentException("该标签未绑定到此景点");
        }

        relationMapper.deleteById(relation.getRelationId());
        log.info("景点 {} 解绑标签 {} 成功", request.getAttractionId(), request.getTagId());
    }

    @Override
    @Transactional
    public void batchBindTags(AttractionTagBatchBindRequest request) {
        if (request.getAttractionId() == null) {
            throw new IllegalArgumentException("景点ID不能为空");
        }
        if (request.getTagIds() == null || request.getTagIds().isEmpty()) {
            throw new IllegalArgumentException("标签ID列表不能为空");
        }

        // 检查所有标签是否存在
        LambdaQueryWrapper<AttractionTag> tagWrapper = new LambdaQueryWrapper<>();
        tagWrapper.in(AttractionTag::getTagId, request.getTagIds());
        List<AttractionTag> tags = tagMapper.selectList(tagWrapper);
        if (tags.size() != request.getTagIds().size()) {
            throw new IllegalArgumentException("部分标签不存在");
        }

        // 查询已绑定的标签
        LambdaQueryWrapper<AttractionTagRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AttractionTagRelation::getAttractionId, request.getAttractionId())
               .in(AttractionTagRelation::getTagId, request.getTagIds());
        
        List<AttractionTagRelation> existingRelations = relationMapper.selectList(wrapper);
        List<Integer> existingTagIds = existingRelations.stream()
                .map(AttractionTagRelation::getTagId)
                .collect(Collectors.toList());

        // 过滤掉已绑定的标签
        List<Integer> newTagIds = request.getTagIds().stream()
                .filter(tagId -> !existingTagIds.contains(tagId))
                .collect(Collectors.toList());

        if (newTagIds.isEmpty()) {
            throw new IllegalArgumentException("所有标签都已绑定,无需重复绑定");
        }

        // 批量创建绑定关系
        for (Integer tagId : newTagIds) {
            AttractionTagRelation relation = AttractionTagRelation.builder()
                    .attractionId(request.getAttractionId())
                    .tagId(tagId)
                    .build();
            relationMapper.insert(relation);
        }

        log.info("景点 {} 批量绑定 {} 个标签成功", request.getAttractionId(), newTagIds.size());
    }

    @Override
    @Transactional
    public void batchUnbindTags(AttractionTagBatchBindRequest request) {
        if (request.getAttractionId() == null) {
            throw new IllegalArgumentException("景点ID不能为空");
        }
        if (request.getTagIds() == null || request.getTagIds().isEmpty()) {
            throw new IllegalArgumentException("标签ID列表不能为空");
        }

        // 查询要解绑的关系
        LambdaQueryWrapper<AttractionTagRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AttractionTagRelation::getAttractionId, request.getAttractionId())
               .in(AttractionTagRelation::getTagId, request.getTagIds());

        List<AttractionTagRelation> relations = relationMapper.selectList(wrapper);
        if (relations.isEmpty()) {
            throw new IllegalArgumentException("没有找到要解绑的标签关系");
        }

        // 批量删除
        List<Long> relationIds = relations.stream()
                .map(AttractionTagRelation::getRelationId)
                .collect(Collectors.toList());

        LambdaQueryWrapper<AttractionTagRelation> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.in(AttractionTagRelation::getRelationId, relationIds);
        int count = relationMapper.delete(deleteWrapper);
        log.info("景点 {} 批量解绑 {} 个标签成功", request.getAttractionId(), count);
    }

    @Override
    public List<AttractionTag> getAttractionTags(Long attractionId) {
        if (attractionId == null) {
            throw new IllegalArgumentException("景点ID不能为空");
        }

        // 查询景点的所有标签关系
        LambdaQueryWrapper<AttractionTagRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AttractionTagRelation::getAttractionId, attractionId);
        
        List<AttractionTagRelation> relations = relationMapper.selectList(wrapper);
        if (relations.isEmpty()) {
            return List.of();
        }

        // 获取标签ID列表
        List<Integer> tagIds = relations.stream()
                .map(AttractionTagRelation::getTagId)
                .collect(Collectors.toList());

        // 查询标签详情
        LambdaQueryWrapper<AttractionTag> tagWrapper = new LambdaQueryWrapper<>();
        tagWrapper.in(AttractionTag::getTagId, tagIds);
        return tagMapper.selectList(tagWrapper);
    }
}
