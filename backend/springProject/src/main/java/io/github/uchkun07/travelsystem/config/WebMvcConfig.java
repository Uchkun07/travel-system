package io.github.uchkun07.travelsystem.config;

import io.github.uchkun07.travelsystem.interceptor.AdminPermissionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置类
 * 配置拦截器等
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Autowired
    private AdminPermissionInterceptor adminPermissionInterceptor;

    @Value("${file.upload.avatar-dir:C:/avatars}")
    private String avatarUploadDir;

    @Value("${file.upload.city-dir:C:/cities}")
    private String cityUploadDir;

    @Value("${file.upload.slideshow-dir:C:/slideshows}")
    private String slideshowUploadDir;

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        // 1. 注册JWT拦截器 - 验证token有效性
        if (jwtInterceptor != null) {
            registry.addInterceptor(jwtInterceptor)
                    .addPathPatterns("/api/admin/**") // 拦截所有管理端接口
                    .excludePathPatterns("/api/admin/login", "/api/admin/register") // 排除登录和注册接口
                    .order(1); // 优先级最高
        }

        // 2. 注册权限拦截器 - 验证用户权限
        if (adminPermissionInterceptor != null) {
            registry.addInterceptor(adminPermissionInterceptor)
                    .addPathPatterns("/api/admin/**", "/api/slideshow/**", "/api/upload/**") // 拦截所有管理端接口
                    .excludePathPatterns("/api/admin/login", "/api/slideshow/active") // 排除登录和公开接口
                    .order(2); // JWT验证之后执行
        }
    }
    
    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // 配置静态资源访问路径 - 使用 file:// 协议访问 C 盘文件
        registry.addResourceHandler("/avatars/**")
                .addResourceLocations("file:" + avatarUploadDir + "/");
        
        registry.addResourceHandler("/cities/**")
                .addResourceLocations("file:" + cityUploadDir + "/");
        
        registry.addResourceHandler("/slideshows/**")
                .addResourceLocations("file:" + slideshowUploadDir + "/");
        
        registry.addResourceHandler("/attractions/**")
                .addResourceLocations("classpath:/static/attractions/");
        
        registry.addResourceHandler("/slideshows/**")
                .addResourceLocations("classpath:/static/slideshows/");
    }
}
