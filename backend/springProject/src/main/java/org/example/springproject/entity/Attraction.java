package org.example.springproject.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("attraction")
public class Attraction {
    @TableId(value = "attraction_id", type = IdType.AUTO)
    private Integer attractionId;

    @TableField("name")
    private String name;

    @TableField("description")
    private String description;

    @TableField("type")
    private String type;

    @TableField("location")
    private String location;

    @TableField("latitude")
    private BigDecimal latitude;

    @TableField("longitude")
    private BigDecimal longitude;

    @TableField("image_url")
    private String imageUrl;

    @TableField("average_rating")
    private BigDecimal averageRating;

    @TableField("view_count")
    private Integer viewCount;

    @TableField("popularity")
    private Integer popularity;

    @TableField("estimated_time")
    private Integer estimatedTime;

    @TableField("ticket_price")
    private BigDecimal ticketPrice;

    @TableField("opening_hours")
    private String openingHours;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
