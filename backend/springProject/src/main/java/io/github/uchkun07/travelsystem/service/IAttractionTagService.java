package io.github.uchkun07.travelsystem.service;

import io.github.uchkun07.travelsystem.dto.AttractionTagQueryRequest;
import io.github.uchkun07.travelsystem.dto.AttractionTagRequest;
import io.github.uchkun07.travelsystem.dto.PageResponse;
import io.github.uchkun07.travelsystem.entity.AttractionTag;

import java.util.List;

/**
 * 景点标签服务接口
 */
public interface IAttractionTagService {

    /**
     * 创建标签
     */
    AttractionTag createTag(AttractionTagRequest request);

    /**
     * 删除标签
     */
    void deleteTag(Integer tagId);

    /**
     * 批量删除标签
     */
    void batchDeleteTags(List<Integer> tagIds);

    /**
     * 修改标签
     */
    AttractionTag updateTag(AttractionTagRequest request);

    /**
     * 分页查询标签
     */
    PageResponse<AttractionTag> queryTags(AttractionTagQueryRequest request);

    /**
     * 根据ID查询标签
     */
    AttractionTag getTagById(Integer tagId);

    /**
     * 获取所有标签
     */
    List<AttractionTag> getAllTags();
}
