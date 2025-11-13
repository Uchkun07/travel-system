package io.github.uchkun07.travelsystem.service;

import io.github.uchkun07.travelsystem.dto.AttractionTypeCreateRequest;
import io.github.uchkun07.travelsystem.dto.AttractionTypeQueryRequest;
import io.github.uchkun07.travelsystem.dto.AttractionTypeUpdateRequest;
import io.github.uchkun07.travelsystem.dto.PageResponse;
import io.github.uchkun07.travelsystem.entity.AttractionType;

import java.util.List;

/**
 * 景点类型服务接口
 */
public interface IAttractionTypeService {

    /**
     * 创建景点类型
     * @param request 创建请求
     * @return 创建的景点类型
     */
    AttractionType createAttractionType(AttractionTypeCreateRequest request);

    /**
     * 删除景点类型
     * @param typeId 类型ID
     */
    void deleteAttractionType(Integer typeId);

    /**
     * 批量删除景点类型
     * @param typeIds 类型ID列表
     */
    void batchDeleteAttractionTypes(List<Integer> typeIds);

    /**
     * 修改景点类型数据
     * @param request 修改请求
     * @return 修改后的景点类型
     */
    AttractionType updateAttractionType(AttractionTypeUpdateRequest request);

    /**
     * 分页查询景点类型
     * 支持根据类型ID或类型名称查询（可选）
     * @param request 查询请求
     * @return 分页结果
     */
    PageResponse<AttractionType> queryAttractionTypes(AttractionTypeQueryRequest request);

    /**
     * 根据ID查询景点类型
     * @param typeId 类型ID
     * @return 景点类型
     */
    AttractionType getAttractionTypeById(Integer typeId);
}
