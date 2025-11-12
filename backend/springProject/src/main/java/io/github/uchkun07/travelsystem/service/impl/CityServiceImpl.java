package io.github.uchkun07.travelsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.uchkun07.travelsystem.dto.CityQueryRequest;
import io.github.uchkun07.travelsystem.dto.CityRequest;
import io.github.uchkun07.travelsystem.dto.PageResponse;
import io.github.uchkun07.travelsystem.entity.City;
import io.github.uchkun07.travelsystem.mapper.CityMapper;
import io.github.uchkun07.travelsystem.service.ICityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class CityServiceImpl implements ICityService {

    @Autowired
    private CityMapper cityMapper;

    @Override
    @Transactional
    public City createCity(CityRequest request) {
        // 检查城市名称是否已存在
        LambdaQueryWrapper<City> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(City::getCityName, request.getCityName());
        if (cityMapper.selectCount(wrapper) > 0) {
            throw new IllegalArgumentException("城市名称已存在: " + request.getCityName());
        }

        City city = City.builder()
                .cityName(request.getCityName())
                .country(request.getCountry())
                .cityUrl(request.getCityUrl())
                .averageTemperature(request.getAverageTemperature())
                .attractionCount(request.getAttractionCount() != null ? request.getAttractionCount() : 0)
                .popularity(request.getPopularity() != null ? request.getPopularity() : 0)
                .description(request.getDescription())
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .status(request.getStatus() != null ? request.getStatus() : 1)
                .build();

        cityMapper.insert(city);
        log.info("创建城市成功: {}", city.getCityName());
        return city;
    }

    @Override
    @Transactional
    public void deleteCity(Integer cityId) {
        City city = cityMapper.selectById(cityId);
        if (city == null) {
            throw new IllegalArgumentException("城市不存在");
        }
        cityMapper.deleteById(cityId);
        log.info("删除城市成功: {}", city.getCityName());
    }

    @Override
    @Transactional
    public void batchDeleteCities(List<Integer> cityIds) {
        if (cityIds == null || cityIds.isEmpty()) {
            throw new IllegalArgumentException("城市ID列表不能为空");
        }
        int count = cityMapper.deleteBatchIds(cityIds);
        log.info("批量删除城市成功: 删除{}条", count);
    }

    @Override
    @Transactional
    public City updateCity(CityRequest request) {
        City city = cityMapper.selectById(request.getCityId());
        if (city == null) {
            throw new IllegalArgumentException("城市不存在");
        }

        // 检查城市名称是否与其他城市重复
        if (request.getCityName() != null && !request.getCityName().equals(city.getCityName())) {
            LambdaQueryWrapper<City> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(City::getCityName, request.getCityName())
                   .ne(City::getCityId, request.getCityId());
            if (cityMapper.selectCount(wrapper) > 0) {
                throw new IllegalArgumentException("城市名称已存在: " + request.getCityName());
            }
            city.setCityName(request.getCityName());
        }

        if (request.getCountry() != null) city.setCountry(request.getCountry());
        if (request.getCityUrl() != null) city.setCityUrl(request.getCityUrl());
        if (request.getAverageTemperature() != null) city.setAverageTemperature(request.getAverageTemperature());
        if (request.getAttractionCount() != null) city.setAttractionCount(request.getAttractionCount());
        if (request.getPopularity() != null) city.setPopularity(request.getPopularity());
        if (request.getDescription() != null) city.setDescription(request.getDescription());
        if (request.getSortOrder() != null) city.setSortOrder(request.getSortOrder());
        if (request.getStatus() != null) city.setStatus(request.getStatus());

        cityMapper.updateById(city);
        log.info("更新城市成功: {}", city.getCityName());
        return city;
    }

    @Override
    public PageResponse<City> queryCities(CityQueryRequest request) {
        LambdaQueryWrapper<City> wrapper = new LambdaQueryWrapper<>();

        if (request.getCityName() != null && !request.getCityName().trim().isEmpty()) {
            wrapper.like(City::getCityName, request.getCityName().trim());
        }
        if (request.getCountry() != null && !request.getCountry().trim().isEmpty()) {
            wrapper.like(City::getCountry, request.getCountry().trim());
        }
        if (request.getStatus() != null) {
            wrapper.eq(City::getStatus, request.getStatus());
        }
        if (request.getMinPopularity() != null) {
            wrapper.ge(City::getPopularity, request.getMinPopularity());
        }
        if (request.getMaxPopularity() != null) {
            wrapper.le(City::getPopularity, request.getMaxPopularity());
        }

        wrapper.orderByAsc(City::getSortOrder)
               .orderByDesc(City::getPopularity)
               .orderByDesc(City::getCreateTime);

        Page<City> page = new Page<>(request.getPageNum(), request.getPageSize());
        Page<City> result = cityMapper.selectPage(page, wrapper);
        return PageResponse.of(result);
    }

    @Override
    public City getCityById(Integer cityId) {
        City city = cityMapper.selectById(cityId);
        if (city == null) {
            throw new IllegalArgumentException("城市不存在");
        }
        return city;
    }

    @Override
    public List<City> getAllCities() {
        LambdaQueryWrapper<City> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(City::getStatus, 1)
               .orderByAsc(City::getSortOrder)
               .orderByDesc(City::getPopularity);
        return cityMapper.selectList(wrapper);
    }
}
