package io.github.uchkun07.travelsystem.interceptor;

import io.github.uchkun07.travelsystem.annotation.RequireAdminPermission;
import io.github.uchkun07.travelsystem.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;

/**
 * 管理员权限拦截器
 * 拦截所有需要管理员权限的接口，验证JWT令牌和权限
 */
@Slf4j
@Component
public class AdminPermissionInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        // 如果不是方法处理器，直接放行
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        
        // 检查方法或类上是否有@RequireAdminPermission注解
        RequireAdminPermission methodAnnotation = handlerMethod.getMethodAnnotation(RequireAdminPermission.class);
        RequireAdminPermission classAnnotation = handlerMethod.getBeanType().getAnnotation(RequireAdminPermission.class);
        
        // 如果没有权限注解，直接放行
        if (methodAnnotation == null && classAnnotation == null) {
            return true;
        }

        // 获取token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"success\":false,\"message\":\"未登录或登录已过期\"}");
            return false;
        }

        String token = authHeader.substring(7);

        try {
            // 验证token有效性
            if (!jwtUtil.validateToken(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"success\":false,\"message\":\"登录已过期，请重新登录\"}");
                return false;
            }

            // 验证是否为管理员token
            String userType = jwtUtil.getUserTypeFromToken(token);
            if (!"admin".equals(userType)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"success\":false,\"message\":\"无权访问管理端接口\"}");
                return false;
            }

            // 获取token中的权限列表
            List<String> userPermissions = jwtUtil.getPermissionsFromToken(token);
            
            // 优先使用方法上的注解
            RequireAdminPermission annotation = methodAnnotation != null ? methodAnnotation : classAnnotation;
            String[] requiredPermissions = annotation.value();
            
            // 如果没有指定具体权限，只要是管理员就可以访问
            if (requiredPermissions.length == 0) {
                return true;
            }

            // 验证权限
            boolean hasPermission = checkPermission(userPermissions, requiredPermissions, annotation.requireAll());
            
            if (!hasPermission) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"success\":false,\"message\":\"权限不足，无法访问该接口\"}");
                log.warn("权限不足: adminId={}, requiredPermissions={}, userPermissions={}", 
                        jwtUtil.getAdminIdFromToken(token), Arrays.toString(requiredPermissions), userPermissions);
                return false;
            }

            // 权限验证通过，将管理员信息存入request属性
            request.setAttribute("adminId", jwtUtil.getAdminIdFromToken(token));
            request.setAttribute("username", jwtUtil.getUsernameFromToken(token));
            request.setAttribute("permissions", userPermissions);
            
            return true;

        } catch (Exception e) {
            log.error("权限验证失败", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"success\":false,\"message\":\"身份验证失败\"}");
            return false;
        }
    }

    /**
     * 检查用户是否拥有所需权限
     * @param userPermissions 用户拥有的权限列表
     * @param requiredPermissions 需要的权限列表
     * @param requireAll 是否需要拥有所有权限
     * @return 是否有权限
     */
    private boolean checkPermission(List<String> userPermissions, String[] requiredPermissions, boolean requireAll) {
        if (userPermissions == null || userPermissions.isEmpty()) {
            return false;
        }

        if (requireAll) {
            // 需要拥有所有权限
            return userPermissions.containsAll(Arrays.asList(requiredPermissions));
        } else {
            // 只需拥有其中一个权限
            for (String required : requiredPermissions) {
                if (userPermissions.contains(required)) {
                    return true;
                }
            }
            return false;
        }
    }
}
