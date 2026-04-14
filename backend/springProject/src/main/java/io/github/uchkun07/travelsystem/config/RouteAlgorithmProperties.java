package io.github.uchkun07.travelsystem.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 路线规划算法配置属性（模拟退火参数，可在 application.yml 中调优）
 */
@Data
@Component
@ConfigurationProperties(prefix = "route.algorithm")
public class RouteAlgorithmProperties {

    /** 模拟退火初始温度 */
    private double initialTemp = 10000.0;

    /** 冷却系数 */
    private double coolingRate = 0.995;

    /** 最小温度（终止阈值） */
    private double minTemp = 1.0;

    /** 每个温度下的迭代次数 */
    private int iterationsPerTemp = 100;

    /**
     * 经济偏好：时间权重（0=只看费用，1=只看时间）
     * 费用权重 = 1 - timeWeight
     */
    private double prefEconomyTimeWeight = 0.2;
    private double prefComfortTimeWeight = 0.8;
    private double prefModerateTimeWeight = 0.5;
}
