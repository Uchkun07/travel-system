package io.github.uchkun07.travelsystem.service;

import io.github.uchkun07.travelsystem.dto.AttractionTagBatchBindRequest;
import io.github.uchkun07.travelsystem.dto.AttractionTagBindRequest;
import io.github.uchkun07.travelsystem.dto.AttractionTagUnbindRequest;
import io.github.uchkun07.travelsystem.entity.AttractionTag;

import java.util.List;

/**
 * 景点-标签关联服务接口
 */
public interface IAttractionTagRelationService {

    /**
     * 景点绑定标签
     */
    void bindTag(AttractionTagBindRequest request);

    /**
     * 景点解绑标签
     */
    void unbindTag(AttractionTagUnbindRequest request);

    /**
     * 景点批量绑定标签
     */
    void batchBindTags(AttractionTagBatchBindRequest request);

    /**
     * 景点批量解绑标签
     */
    void batchUnbindTags(AttractionTagBatchBindRequest request);

    /**
     * 查询景点的所有标签
     */
    List<AttractionTag> getAttractionTags(Long attractionId);
}
