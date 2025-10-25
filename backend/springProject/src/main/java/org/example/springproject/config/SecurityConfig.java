package org.example.springproject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 配置类
 * 配置认证和授权规则
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * 配置 HTTP 安全规则
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 禁用 CSRF (因为使用 JWT,不需要 CSRF 保护)
                .csrf(AbstractHttpConfigurer::disable)
                
                // 配置会话管理为无状态 (JWT 不需要 session)
                .sessionManagement(session -> 
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                
                // 配置授权规则
                .authorizeHttpRequests(auth -> auth
                    // 允许所有请求访问 (因为已经有自定义的 JWT 拦截器)
                    .anyRequest().permitAll()
                );

        return http.build();
    }

    /**
     * 密码加密器
     * 使用 BCrypt 算法进行密码加密
     * 
     * 注意: 当前项目使用 PBKDF2WithHmacSHA256 手动实现密码加密
     * 这个 Bean 提供给未来可能需要的 Spring Security 密码验证功能
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
