package io.github.uchkun07.travelsystem.dto;

import lombok.Data;

/**
 * 用户在某景点类型上的行为统计
 */
@Data
public class RecommendTypeBehaviorStat {
    private Integer typeId;
    private Integer clickCount;
    private Integer staySeconds;
}
