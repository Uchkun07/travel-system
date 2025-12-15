package io.github.uchkun07.travelsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.uchkun07.travelsystem.dto.AttractionTypeCreateRequest;
import io.github.uchkun07.travelsystem.dto.AttractionTypeQueryRequest;
import io.github.uchkun07.travelsystem.dto.AttractionTypeUpdateRequest;
import io.github.uchkun07.travelsystem.dto.PageResponse;
import io.github.uchkun07.travelsystem.entity.AttractionType;
import io.github.uchkun07.travelsystem.mapper.AttractionTypeMapper;
import io.github.uchkun07.travelsystem.service.IAttractionTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 景点类型服务实现类
 */
@Slf4j
@Service
public class AttractionTypeServiceImpl implements IAttractionTypeService {

    @Autowired
    private AttractionTypeMapper attractionTypeMapper;

    /**
     * 创建景点类型
     * @param request 创建请求
     * @return 创建的景点类型
     */
    @Override
    @Transactional
    public AttractionType createAttractionType(AttractionTypeCreateRequest request) {
        // 1. 检查类型名称是否已存在
        LambdaQueryWrapper<AttractionType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AttractionType::getTypeName, request.getTypeName());
        Long count = attractionTypeMapper.selectCount(queryWrapper);
        
        if (count > 0) {
            throw new IllegalArgumentException("类型名称已存在: " + request.getTypeName());
        }

        // 2. 构建实体对象
        AttractionType attractionType = AttractionType.builder()
                .typeName(request.getTypeName())
                .sortOrder(request.getSortOrder())
                .status(request.getStatus())
                .build();

        // 3. 插入数据库
        int result = attractionTypeMapper.insert(attractionType);
        
        if (result <= 0) {
            throw new RuntimeException("创建景点类型失败");
        }

        log.info("创建景点类型成功: typeId={}, typeName={}", attractionType.getTypeId(), attractionType.getTypeName());
        return attractionType;
    }

    /**
     * 删除景点类型
     * @param typeId 类型ID
     */
    @Override
    @Transactional
    public void deleteAttractionType(Integer typeId) {
        // 1. 检查类型是否存在
        AttractionType attractionType = attractionTypeMapper.selectById(typeId);
        if (attractionType == null) {
            throw new IllegalArgumentException("景点类型不存在: typeId=" + typeId);
        }

        // 2. 删除类型
        int result = attractionTypeMapper.deleteById(typeId);
        
        if (result <= 0) {
            throw new RuntimeException("删除景点类型失败");
        }

        log.info("删除景点类型成功: typeId={}, typeName={}", typeId, attractionType.getTypeName());
    }

    /**
     * 批量删除景点类型
     * @param typeIds 类型ID列表
     */
    @Override
    @Transactional
    public void batchDeleteAttractionTypes(List<Integer> typeIds) {
        if (typeIds == null || typeIds.isEmpty()) {
            throw new IllegalArgumentException("删除列表不能为空");
        }

        // 批量删除
        @SuppressWarnings("deprecation")
        int result = attractionTypeMapper.deleteBatchIds(typeIds);
        
        if (result <= 0) {
            throw new RuntimeException("批量删除景点类型失败");
        }

        log.info("批量删除景点类型成功: 删除数量={}, typeIds={}", result, typeIds);
    }

    /**
     * 修改景点类型数据
     * @param request 修改请求
     * @return 修改后的景点类型
     */
    @Override
    @Transactional
    public AttractionType updateAttractionType(AttractionTypeUpdateRequest request) {
        // 1. 检查类型是否存在
        AttractionType existingType = attractionTypeMapper.selectById(request.getTypeId());
        if (existingType == null) {
            throw new IllegalArgumentException("景点类型不存在: typeId=" + request.getTypeId());
        }

        // 2. 如果修改了名称，检查新名称是否与其他类型重复
        if (request.getTypeName() != null && !request.getTypeName().equals(existingType.getTypeName())) {
            LambdaQueryWrapper<AttractionType> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AttractionType::getTypeName, request.getTypeName())
                       .ne(AttractionType::getTypeId, request.getTypeId());
            Long count = attractionTypeMapper.selectCount(queryWrapper);
            
            if (count > 0) {
                throw new IllegalArgumentException("类型名称已存在: " + request.getTypeName());
            }
            existingType.setTypeName(request.getTypeName());
        }

        // 3. 更新其他字段
        if (request.getSortOrder() != null) {
            existingType.setSortOrder(request.getSortOrder());
        }
        if (request.getStatus() != null) {
            existingType.setStatus(request.getStatus());
        }

        // 4. 执行更新
        int result = attractionTypeMapper.updateById(existingType);
        
        if (result <= 0) {
            throw new RuntimeException("修改景点类型失败");
        }

        log.info("修改景点类型成功: typeId={}, typeName={}", existingType.getTypeId(), existingType.getTypeName());
        return existingType;
    }

    /**
     * 分页查询景点类型
     * 支持根据类型ID或类型名称查询（可选）
     * @param request 查询请求
     * @return 分页结果
     */
    @Override
    public PageResponse<AttractionType> queryAttractionTypes(AttractionTypeQueryRequest request) {
        // 1. 构建查询条件
        LambdaQueryWrapper<AttractionType> queryWrapper = new LambdaQueryWrapper<>();
        
        // 根据类型ID精确查询（可选）
        if (request.getTypeId() != null) {
            queryWrapper.eq(AttractionType::getTypeId, request.getTypeId());
        }
        
        // 根据类型名称模糊查询（可选）
        if (request.getTypeName() != null && !request.getTypeName().trim().isEmpty()) {
            queryWrapper.like(AttractionType::getTypeName, request.getTypeName().trim());
        }
        
        // 根据状态筛选（可选）
        if (request.getStatus() != null) {
            queryWrapper.eq(AttractionType::getStatus, request.getStatus());
        }
        
        // 按排序序号升序，创建时间降序
        queryWrapper.orderByAsc(AttractionType::getSortOrder)
                   .orderByDesc(AttractionType::getCreateTime);

        // 2. 分页查询
        Page<AttractionType> page = new Page<>(request.getPageNum(), request.getPageSize());
        Page<AttractionType> result = attractionTypeMapper.selectPage(page, queryWrapper);

        // 3. 转换为PageResponse
        return PageResponse.of(result);
    }

    /**
     * 根据ID查询景点类型
     * @param typeId 类型ID
     * @return 景点类型
     */
    @Override
    public AttractionType getAttractionTypeById(Integer typeId) {
        AttractionType attractionType = attractionTypeMapper.selectById(typeId);
        if (attractionType == null) {
            throw new IllegalArgumentException("景点类型不存在: typeId=" + typeId);
        }
        return attractionType;
    }

    /**
     * 获取所有景点类型
     * @return 景点类型列表
     */
    @Override
    public List<AttractionType> getAllAttractionTypes() {
        LambdaQueryWrapper<AttractionType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AttractionType::getStatus, 1) // 只查询启用状态的类型
                   .orderByAsc(AttractionType::getSortOrder)
                   .orderByDesc(AttractionType::getCreateTime);
        return attractionTypeMapper.selectList(queryWrapper);
    }
}
