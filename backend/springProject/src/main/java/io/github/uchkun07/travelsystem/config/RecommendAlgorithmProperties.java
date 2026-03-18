package io.github.uchkun07.travelsystem.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 个性化推荐算法配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "recommend.algorithm")
public class RecommendAlgorithmProperties {

    /** Python 推荐脚本路径，留空则使用默认相对路径 */
    private String scriptPath = "";

    /** 行为窗口天数 */
    private int behaviorWindowDays = 30;

    /** 切换到行为画像模式的事件阈值 */
    private long behaviorSwitchThreshold = 20;

    /** 冷启动：显式偏好权重 */
    private double coldPreferenceWeight = 0.6;

    /** 冷启动：热门权重 */
    private double coldHotWeight = 0.4;

    /** 稳定期：画像权重 */
    private double matureProfileWeight = 0.8;

    /** 稳定期：热门权重 */
    private double matureHotWeight = 0.2;

    /** 画像中：显式偏好权重 */
    private double profileExplicitWeight = 0.4;

    /** 画像中：隐式行为权重 */
    private double profileImplicitWeight = 0.6;

    /** 隐式行为中：点击权重 */
    private double behaviorClickWeight = 0.4;

    /** 隐式行为中：停留权重 */
    private double behaviorStayWeight = 0.6;

    /** 推荐版本号 */
    private String recVersion = "content-py-v1";
}
