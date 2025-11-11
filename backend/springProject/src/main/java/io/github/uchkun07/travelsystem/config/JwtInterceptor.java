package io.github.uchkun07.travelsystem.config;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import io.github.uchkun07.travelsystem.util.JwtUtil;
import io.github.uchkun07.travelsystem.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT拦截器
 * 用于验证JWT令牌和检查黑名单
 */
@Slf4j
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        // 处理OPTIONS预检请求
        if ("OPTIONS".equals(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }

        // 从请求头中获取token
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"success\":false,\"message\":\"未提供认证令牌\"}");
            log.warn("请求被拦截: 未提供认证令牌, URI: {}", request.getRequestURI());
            return false;
        }

        String token = authHeader.substring(7); // 移除 "Bearer " 前缀

        try {
            // 1. 验证令牌格式和有效性
            if (!jwtUtil.validateToken(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"success\":false,\"message\":\"令牌无效或已过期\"}");
                log.warn("请求被拦截: 令牌无效或已过期, URI: {}", request.getRequestURI());
                return false;
            }

            // 2. 检查令牌是否在黑名单中(已登出)
            String blacklistKey = "token_blacklist:" + token;
            if (Boolean.TRUE.equals(redisUtil.hasKey(blacklistKey))) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"success\":false,\"message\":\"令牌已失效,请重新登录\"}");
                log.warn("请求被拦截: 令牌已在黑名单中, URI: {}", request.getRequestURI());
                return false;
            }

            // 3. 从令牌中提取用户信息并存储到请求属性中,供Controller使用
            Long userId = jwtUtil.getUserIdFromToken(token);
            String username = jwtUtil.getUsernameFromToken(token);
            
            request.setAttribute("userId", userId);
            request.setAttribute("username", username);
            request.setAttribute("token", token);

            log.debug("令牌验证成功, 用户ID: {}, 用户名: {}, URI: {}", userId, username, request.getRequestURI());
            return true;

        } catch (JwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"success\":false,\"message\":\"令牌解析失败\"}");
            log.warn("请求被拦截: 令牌解析失败, URI: {}, 错误: {}", request.getRequestURI(), e.getMessage());
            return false;
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"success\":false,\"message\":\"服务器内部错误\"}");
            log.error("令牌验证异常, URI: " + request.getRequestURI(), e);
            return false;
        }
    }
}
