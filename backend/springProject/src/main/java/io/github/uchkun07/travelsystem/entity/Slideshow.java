package io.github.uchkun07.travelsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 轮播图表实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("slideshow")
public class Slideshow {

    /**
     * 轮播图唯一标识
     */
    @TableId(value = "slideshow_id", type = IdType.AUTO)
    private Integer slideshowId;

    /**
     * 轮播图标题
     */
    @TableField("title")
    private String title;

    /**
     * 副标题/描述
     */
    @TableField("subtitle")
    private String subtitle;

    /**
     * 轮播图图片URL
     */
    @TableField("image_url")
    private String imageUrl;

    /**
     * 关联的景点ID（当link_type为attraction时使用）
     */
    @TableField("attraction_id")
    private Long attractionId;

    /**
     * 显示顺序（数字越小越靠前）
     */
    @TableField("display_order")
    private Integer displayOrder;

    /**
     * 状态：0-禁用，1-启用
     */
    @TableField("status")
    private Integer status;

    /**
     * 开始展示时间
     */
    @TableField("start_time")
    private LocalDateTime startTime;

    /**
     * 结束展示时间
     */
    @TableField("end_time")
    private LocalDateTime endTime;

    /**
     * 点击次数
     */
    @TableField("click_count")
    private Integer clickCount;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
