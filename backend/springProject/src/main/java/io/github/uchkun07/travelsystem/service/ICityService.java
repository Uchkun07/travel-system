package io.github.uchkun07.travelsystem.service;

import io.github.uchkun07.travelsystem.dto.CityQueryRequest;
import io.github.uchkun07.travelsystem.dto.CityRequest;
import io.github.uchkun07.travelsystem.dto.PageResponse;
import io.github.uchkun07.travelsystem.entity.City;
import io.github.uchkun07.travelsystem.dto.CityResponse;

import java.util.List;

/**
 * 城市服务接口
 */
public interface ICityService {

    /**
     * 创建城市
     */
    City createCity(CityRequest request);

    /**
     * 删除城市
     */
    void deleteCity(Integer cityId);

    /**
     * 批量删除城市
     */
    void batchDeleteCities(List<Integer> cityIds);

    /**
     * 修改城市
     */
    City updateCity(CityRequest request);

    /**
     * 分页查询城市
     */
    PageResponse<City> queryCities(CityQueryRequest request);

    /**
     * 根据ID查询城市
     */
    City getCityById(Integer cityId);

    /**
     * 获取所有城市
     */
    List<City> getAllCities();

    /**
     * 客户端分页获取城市数据
     *
     * @param request 查询条件
     * @return 分页结果
     */
    PageResponse<CityResponse> getCityCards(CityQueryRequest request);
}
