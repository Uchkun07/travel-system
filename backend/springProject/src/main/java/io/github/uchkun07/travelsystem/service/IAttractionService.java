package io.github.uchkun07.travelsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.github.uchkun07.travelsystem.dto.*;
import io.github.uchkun07.travelsystem.entity.Attraction;

/**
 * 景点服务接口
 */
public interface IAttractionService extends IService<Attraction> {

    /**
     * 创建景点
     *
     * @param request 景点信息
     * @return 景点ID
     */
    Long createAttraction(AttractionRequest request);

    /**
     * 删除景点
     *
     * @param attractionId 景点ID
     */
    void deleteAttraction(Long attractionId);

    /**
     * 更新景点
     *
     * @param request 景点信息
     */
    void updateAttraction(AttractionRequest request);

    /**
     * 分页查询景点列表
     *
     * @param request 查询条件
     * @return 分页结果
     */
    PageResponse<AttractionListResponse> listAttractions(AttractionQueryRequest request);

    /**
     * 获取景点详情
     *
     * @param attractionId 景点ID
     * @return 景点详情
     */
    AttractionDetailResponse getAttractionDetail(Long attractionId);
}
