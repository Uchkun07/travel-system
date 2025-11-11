package io.github.uchkun07.travelsystem.config;

import io.github.uchkun07.travelsystem.interceptor.AdminPermissionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置类
 * 配置跨域访问、拦截器等
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Autowired
    private AdminPermissionInterceptor adminPermissionInterceptor;

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*") // 允许所有源(开发环境),生产环境应指定具体域名
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        // 管理员权限拦截器 - 拦截所有/admin开头的接口
        if (adminPermissionInterceptor != null) {
            registry.addInterceptor(adminPermissionInterceptor)
                    .addPathPatterns("/admin/**")  // 拦截所有管理端接口
                    .excludePathPatterns(
                            "/admin/login"           // 管理员登录接口不需要权限验证
                    )
                    .order(1);  // 设置拦截器优先级，越小越先执行
        }

        // 普通用户JWT拦截器
        if (jwtInterceptor != null) {
            registry.addInterceptor(jwtInterceptor)
                    .addPathPatterns("/**") // 拦截所有路径
                    .excludePathPatterns(
                            "/admin/**",             // 管理端接口由AdminPermissionInterceptor处理
                            "/email/sendCode",       // 发送验证码接口
                            "/img/**",               // 静态资源 - 图片文件
                            "/swagger-ui/**",        // Swagger UI
                            "/v3/api-docs/**",       // Swagger API文档
                            "/error"                 // 错误页面
                    )
                    .order(2);  // 设置优先级
        }
    }

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // 配置静态资源访问路径
        // 使用绝对路径，确保能正确访问到 public 目录下的文件
        registry.addResourceHandler("/img/**")
                .addResourceLocations(
                    "classpath:/static/img/",
                    "file:" + System.getProperty("user.dir") + "/public/img/"
                );
    }
}

