package io.github.uchkun07.travelsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.uchkun07.travelsystem.dto.*;
import io.github.uchkun07.travelsystem.entity.*;
import io.github.uchkun07.travelsystem.mapper.AttractionMapper;
import io.github.uchkun07.travelsystem.mapper.AttractionTagMapper;
import io.github.uchkun07.travelsystem.mapper.AttractionTagRelationMapper;
import io.github.uchkun07.travelsystem.mapper.AttractionTypeMapper;
import io.github.uchkun07.travelsystem.mapper.CityMapper;
import io.github.uchkun07.travelsystem.service.IAttractionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 景点服务实现类
 */
@Service
@RequiredArgsConstructor
public class AttractionServiceImpl extends ServiceImpl<AttractionMapper, Attraction> implements IAttractionService {

    private final AttractionMapper attractionMapper;
    private final CityMapper cityMapper;
    private final AttractionTypeMapper attractionTypeMapper;
    private final AttractionTagRelationMapper attractionTagRelationMapper;
    private final AttractionTagMapper attractionTagMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createAttraction(AttractionRequest request) {
        // 验证城市是否存在
        City city = cityMapper.selectById(request.getCityId());
        if (city == null) {
            throw new RuntimeException("城市不存在");
        }

        // 验证景点类型是否存在
        AttractionType type = attractionTypeMapper.selectById(request.getTypeId());
        if (type == null) {
            throw new RuntimeException("景点类型不存在");
        }

        // 检查景点名称是否重复
        LambdaQueryWrapper<Attraction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Attraction::getName, request.getName());
        if (attractionMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("景点名称已存在");
        }

        // 创建景点
        Attraction attraction = new Attraction();
        BeanUtils.copyProperties(request, attraction);
        
        // 设置默认值
        attraction.setAverageRating(null);
        attraction.setRatingCount(0);
        attraction.setBrowseCount(0);
        attraction.setFavoriteCount(0);
        attraction.setPopularity(0);
        
        // 设置创建人ID(从Request中获取)
        Long creatorId = getCurrentAdminId();
        attraction.setCreatorId(creatorId);

        // 设置默认状态
        if (attraction.getStatus() == null) {
            attraction.setStatus(1);
        }
        if (attraction.getAuditStatus() == null) {
            attraction.setAuditStatus(1); // 默认待审核
        }

        attractionMapper.insert(attraction);
        return attraction.getAttractionId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAttraction(Long attractionId) {
        // 验证景点是否存在
        Attraction attraction = attractionMapper.selectById(attractionId);
        if (attraction == null) {
            throw new RuntimeException("景点不存在");
        }

        // 删除景点关联的标签
        LambdaQueryWrapper<AttractionTagRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AttractionTagRelation::getAttractionId, attractionId);
        attractionTagRelationMapper.delete(wrapper);

        // 删除景点
        attractionMapper.deleteById(attractionId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAttraction(AttractionRequest request) {
        if (request.getAttractionId() == null) {
            throw new RuntimeException("景点ID不能为空");
        }

        // 验证景点是否存在
        Attraction attraction = attractionMapper.selectById(request.getAttractionId());
        if (attraction == null) {
            throw new RuntimeException("景点不存在");
        }

        // 验证城市是否存在
        if (request.getCityId() != null) {
            City city = cityMapper.selectById(request.getCityId());
            if (city == null) {
                throw new RuntimeException("城市不存在");
            }
        }

        // 验证景点类型是否存在
        if (request.getTypeId() != null) {
            AttractionType type = attractionTypeMapper.selectById(request.getTypeId());
            if (type == null) {
                throw new RuntimeException("景点类型不存在");
            }
        }

        // 检查景点名称是否重复
        if (StringUtils.hasText(request.getName()) && !request.getName().equals(attraction.getName())) {
            LambdaQueryWrapper<Attraction> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Attraction::getName, request.getName());
            if (attractionMapper.selectCount(wrapper) > 0) {
                throw new RuntimeException("景点名称已存在");
            }
        }

        // 更新景点信息
        BeanUtils.copyProperties(request, attraction);
        attractionMapper.updateById(attraction);
    }

    @Override
    public PageResponse<AttractionListResponse> listAttractions(AttractionQueryRequest request) {
        // 构建分页对象
        Page<Attraction> page = new Page<>(request.getPageNum(), request.getPageSize());

        // 构建查询条件
        LambdaQueryWrapper<Attraction> wrapper = new LambdaQueryWrapper<>();
        
        // 景点ID精确查询
        if (request.getAttractionId() != null) {
            wrapper.eq(Attraction::getAttractionId, request.getAttractionId());
        }
        
        // 景点名称模糊查询
        if (StringUtils.hasText(request.getName())) {
            wrapper.like(Attraction::getName, request.getName());
        }
        
        // 景点类型
        if (request.getTypeId() != null) {
            wrapper.eq(Attraction::getTypeId, request.getTypeId());
        }
        
        // 城市
        if (request.getCityId() != null) {
            wrapper.eq(Attraction::getCityId, request.getCityId());
        }
        
        // 景点状态
        if (request.getStatus() != null) {
            wrapper.eq(Attraction::getStatus, request.getStatus());
        }
        
        // 审核状态
        if (request.getAuditStatus() != null) {
            wrapper.eq(Attraction::getAuditStatus, request.getAuditStatus());
        }

        // 排序
        String orderBy = request.getOrderBy();
        boolean isAsc = "asc".equalsIgnoreCase(request.getOrderType());
        
        switch (orderBy) {
            case "popularity":
                wrapper.orderBy(true, isAsc, Attraction::getPopularity);
                break;
            case "browse_count":
                wrapper.orderBy(true, isAsc, Attraction::getBrowseCount);
                break;
            case "favorite_count":
                wrapper.orderBy(true, isAsc, Attraction::getFavoriteCount);
                break;
            default:
                wrapper.orderByDesc(Attraction::getPopularity);
        }

        // 查询分页数据
        Page<Attraction> attractionPage = attractionMapper.selectPage(page, wrapper);

        // 获取所有涉及的城市ID和类型ID
        List<Integer> cityIds = attractionPage.getRecords().stream()
                .map(Attraction::getCityId)
                .distinct()
                .collect(Collectors.toList());
        
        List<Integer> typeIds = attractionPage.getRecords().stream()
                .map(Attraction::getTypeId)
                .distinct()
                .collect(Collectors.toList());

        // 批量查询城市和类型信息
        Map<Integer, String> cityMap = cityIds.isEmpty() ? Map.of() : 
                cityMapper.selectList(new LambdaQueryWrapper<City>().in(City::getCityId, cityIds))
                        .stream()
                        .collect(Collectors.toMap(City::getCityId, City::getCityName));

        Map<Integer, String> typeMap = typeIds.isEmpty() ? Map.of() :
                attractionTypeMapper.selectList(new LambdaQueryWrapper<AttractionType>().in(AttractionType::getTypeId, typeIds))
                        .stream()
                        .collect(Collectors.toMap(AttractionType::getTypeId, AttractionType::getTypeName));

        // 转换为响应DTO
        List<AttractionListResponse> responseList = attractionPage.getRecords().stream()
                .map(attraction -> AttractionListResponse.builder()
                        .attractionId(attraction.getAttractionId())
                        .name(attraction.getName())
                        .typeId(attraction.getTypeId())
                        .typeName(typeMap.get(attraction.getTypeId()))
                        .cityId(attraction.getCityId())
                        .cityName(cityMap.get(attraction.getCityId()))
                        .viewCount(attraction.getBrowseCount())
                        .favoriteCount(attraction.getFavoriteCount())
                        .popularityScore(attraction.getPopularity())
                        .mainImageUrl(attraction.getMainImageUrl())
                        .status(attraction.getStatus())
                        .auditStatus(attraction.getAuditStatus())
                        .build())
                .collect(Collectors.toList());

        return PageResponse.<AttractionListResponse>builder()
                .records(responseList)
                .total(attractionPage.getTotal())
                .pageNum(attractionPage.getCurrent())
                .pageSize(attractionPage.getSize())
                .totalPages(attractionPage.getPages())
                .hasPrevious(attractionPage.getCurrent() > 1)
                .hasNext(attractionPage.getCurrent() < attractionPage.getPages())
                .build();
    }

    /**
     * 从当前请求中获取管理员ID
     */
    private Long getCurrentAdminId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            Object adminId = request.getAttribute("adminId");
            if (adminId instanceof Long) {
                return (Long) adminId;
            }
        }
        return null;
    }

    @Override
    public AttractionDetailResponse getAttractionDetail(Long attractionId) {
        // 查询景点信息
        Attraction attraction = attractionMapper.selectById(attractionId);
        if (attraction == null) {
            throw new RuntimeException("景点不存在");
        }

        // 查询城市信息
        City city = cityMapper.selectById(attraction.getCityId());
        String cityName = city != null ? city.getCityName() : null;

        // 查询景点类型信息
        AttractionType type = attractionTypeMapper.selectById(attraction.getTypeId());
        String typeName = type != null ? type.getTypeName() : null;

        // 查询景点绑定的标签
        LambdaQueryWrapper<AttractionTagRelation> relationWrapper = new LambdaQueryWrapper<>();
        relationWrapper.eq(AttractionTagRelation::getAttractionId, attractionId);
        List<AttractionTagRelation> relations = attractionTagRelationMapper.selectList(relationWrapper);

        List<AttractionDetailResponse.TagInfo> tags = new ArrayList<>();
        if (!relations.isEmpty()) {
            List<Integer> tagIds = relations.stream()
                    .map(AttractionTagRelation::getTagId)
                    .collect(Collectors.toList());

            LambdaQueryWrapper<AttractionTag> tagWrapper = new LambdaQueryWrapper<>();
            tagWrapper.in(AttractionTag::getTagId, tagIds);
            List<AttractionTag> tagList = attractionTagMapper.selectList(tagWrapper);

            tags = tagList.stream()
                    .map(tag -> AttractionDetailResponse.TagInfo.builder()
                            .tagId(tag.getTagId())
                            .tagName(tag.getTagName())
                            .build())
                    .collect(Collectors.toList());
        }

        // 构建响应DTO
        AttractionDetailResponse response = new AttractionDetailResponse();
        BeanUtils.copyProperties(attraction, response);
        response.setCityName(cityName);
        response.setTypeName(typeName);
        response.setTags(tags);

        return response;
    }

    @Override
    public PageResponse<AttractionCardResponse> getAttractionCards(AttractionQueryRequest request) {
        // 创建分页对象
        Page<Attraction> page = new Page<>(request.getPageNum(), request.getPageSize());
        
        // 查询条件：只查询已审核通过且启用的景点
        LambdaQueryWrapper<Attraction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Attraction::getAuditStatus, 2); // 已审核
        wrapper.eq(Attraction::getStatus, 1); // 启用
        wrapper.orderByDesc(Attraction::getPopularity); // 按热度排序
        
        Page<Attraction> attractionPage = attractionMapper.selectPage(page, wrapper);
        
        // 转换为响应DTO
        List<AttractionCardResponse> cardList = attractionPage.getRecords().stream()
                .map(attraction -> {
                    // 查询城市信息
                    City city = cityMapper.selectById(attraction.getCityId());
                    String cityName = city != null ? city.getCityName() : "";
                    
                    // 查询类型信息
                    AttractionType type = attractionTypeMapper.selectById(attraction.getTypeId());
                    String typeName = type != null ? type.getTypeName() : "";
                    
                    // 处理图片URL - 如果是相对路径则添加前缀
                    String imageUrl = attraction.getMainImageUrl();
                    if (imageUrl != null && !imageUrl.startsWith("http")) {
                        imageUrl = "http://localhost:8080" + imageUrl;
                    }
                    
                    // 手动构建响应对象，只包含卡片所需字段
                    return AttractionCardResponse.builder()
                            .attractionId(attraction.getAttractionId())
                            .name(attraction.getName())
                            .description(attraction.getSubtitle() != null ? attraction.getSubtitle() : "")
                            .type(typeName)
                            .location(cityName)
                            .imageUrl(imageUrl)
                            .averageRating(attraction.getAverageRating())
                            .viewCount(attraction.getBrowseCount())
                            .popularity(attraction.getPopularity())
                            .ticketPrice(attraction.getTicketPrice())
                            .build();
                })
                .collect(Collectors.toList());
        
        return PageResponse.<AttractionCardResponse>builder()
                .records(cardList)
                .total(attractionPage.getTotal())
                .pageNum(attractionPage.getCurrent())
                .pageSize(attractionPage.getSize())
                .totalPages(attractionPage.getPages())
                .hasPrevious(attractionPage.getCurrent() > 1)
                .hasNext(attractionPage.getCurrent() < attractionPage.getPages())
                .build();
    }

    @Override
    public AttractionDetailResponse getAttractionCardById(Long attractionId) {
        // 异步增加浏览量
        incrementBrowseCountAsync(attractionId);
        
        // 直接使用已有的getAttractionDetail方法，它返回完整的详情数据
        return getAttractionDetail(attractionId);
    }

    /**
     * 异步增加浏览量
     */
    @Async
    @Transactional(rollbackFor = Exception.class)
    public void incrementBrowseCountAsync(Long attractionId) {
        try {
            Attraction attraction = attractionMapper.selectById(attractionId);
            if (attraction != null) {
                attraction.setBrowseCount(attraction.getBrowseCount() + 1);
                attractionMapper.updateById(attraction);
            }
        } catch (Exception e) {
            // 浏览量更新失败不影响主流程，记录日志即可
            log.error("异步更新浏览量失败, attractionId=" + attractionId, e);
        }
    }

    @Override
    public List<AttractionCardResponse> getAttractionCardsByIds(List<Long> attractionIds) {
        if (attractionIds == null || attractionIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 批量查询景点
        List<Attraction> attractions = listByIds(attractionIds);
        
        // 转换为卡片响应
        return attractions.stream().map(attraction -> {
            AttractionCardResponse card = new AttractionCardResponse();
            card.setAttractionId(attraction.getAttractionId());
            card.setName(attraction.getName());
            card.setDescription(attraction.getSubtitle());
            card.setImageUrl(attraction.getMainImageUrl());
            card.setAverageRating(attraction.getAverageRating());
            card.setViewCount(attraction.getBrowseCount());
            card.setPopularity(attraction.getPopularity());
            card.setTicketPrice(attraction.getTicketPrice());
            
            // 获取城市信息
            if (attraction.getCityId() != null) {
                City city = cityMapper.selectById(attraction.getCityId());
                if (city != null) {
                    card.setLocation(city.getCityName());
                }
            }
            
            // 获取类型信息
            if (attraction.getTypeId() != null) {
                AttractionType type = attractionTypeMapper.selectById(attraction.getTypeId());
                if (type != null) {
                    card.setType(type.getTypeName());
                }
            }
            
            return card;
        }).collect(Collectors.toList());
    }
}

