package io.github.uchkun07.travelsystem.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

import java.util.List;

/**
 * 路线规划请求参数
 */
@Data
public class RoutePlanRequest {

    /**
     * 出发地（中文地址，如"北京市朝阳区"）
     */
    @NotBlank(message = "出发地不能为空")
    private String departure;

    /**
     * 预算（元）
     */
    @Positive(message = "预算必须为正数")
    private double budget;

    /**
     * 出发日期，格式 yyyy-MM-dd
     */
    @NotBlank(message = "出发日期不能为空")
    private String departureDate;

    /**
     * 出行方式：自驾 / 火车 / 飞机
     */
    @NotBlank(message = "出行方式不能为空")
    private String travelMode;

    /**
     * 出行人群：独自 / 亲子 / 家庭 / 朋友
     */
    @NotBlank(message = "出行人群不能为空")
    private String travelGroup;

    /**
     * 出行偏好：经济 / 适中 / 舒适
     */
    @NotBlank(message = "出行偏好不能为空")
    private String travelPreference;

    /**
     * 用户选择的景点 ID 列表（至少 1 个）
     */
    @NotEmpty(message = "请至少选择一个景点")
    private List<Long> attractionIds;
}
