package io.github.uchkun07.travelsystem.enums;

/**
 * 文件分类枚举
 */
public enum FileCategory {
    
    /**
     * 头像
     */
    AVATAR("avatars", "头像"),
    
    /**
     * 城市图片
     */
    CITY("cities", "城市图片"),
    
    /**
     * 景点图片
     */
    ATTRACTION("attractions", "景点图片"),
    
    /**
     * 轮播图
     */
    SLIDESHOW("slideshows", "轮播图");
    
    private final String path;
    private final String description;
    
    FileCategory(String path, String description) {
        this.path = path;
        this.description = description;
    }
    
    public String getPath() {
        return path;
    }
    
    public String getDescription() {
        return description;
    }
}
