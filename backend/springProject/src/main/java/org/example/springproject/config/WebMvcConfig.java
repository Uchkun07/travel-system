package org.example.springproject.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置类
 * 配置跨域访问、拦截器等
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

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
        if (jwtInterceptor != null) {
            registry.addInterceptor(jwtInterceptor)
                    .addPathPatterns("/**") // 拦截所有路径
                    .excludePathPatterns(
                            "/auth/register",        // 注册接口
                            "/auth/login",           // 登录接口
                            "/auth/checkUsername",   // 检查用户名接口
                            "/auth/checkEmail",      // 检查邮箱接口
                            "/slideshow/active",     // 获取启用的轮播图(前台展示) - 允许匿名访问
                            "/attractions/list",     // 获取所有景点 - 允许匿名访问
                            "/email/sendCode",       // 发送验证码接口
                            "/swagger-ui/**",        // Swagger UI
                            "/v3/api-docs/**",       // Swagger API文档
                            "/error"                 // 错误页面
                    );
        }
    }
}

