package org.example.springproject.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("slideshow")
public class Slideshow {
    @TableId(value = "slideshow_id", type = IdType.AUTO)
    private Integer slideshowId;

    @TableField("title")
    private String title;

    @TableField("subtitle")
    private String subtitle;

    @TableField("image_url")
    private String imageUrl;

    @TableField("attraction_id")
    private Integer attractionId;

    @TableField("display_order")
    private Integer displayOrder;

    @TableField("status")
    private Integer status;

    @TableField("start_time")
    private Date startTime;

    @TableField("end_time")
    private Date endTime;

    @TableField("click_count")
    private Integer clickCount;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

}
