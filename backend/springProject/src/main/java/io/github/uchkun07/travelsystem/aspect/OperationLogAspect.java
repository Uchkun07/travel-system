package io.github.uchkun07.travelsystem.aspect;

import io.github.uchkun07.travelsystem.annotation.OperationLog;
import io.github.uchkun07.travelsystem.service.IOperationLogService;
import io.github.uchkun07.travelsystem.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

/**
 * 操作日志切面
 * 自动记录带有@OperationLog注解的方法的操作日志
 */
@Slf4j
@Aspect
@Component
public class OperationLogAspect {

    @Autowired
    private IOperationLogService operationLogService;

    @Autowired
    private JwtUtil jwtUtil;

    @Around("@annotation(io.github.uchkun07.travelsystem.annotation.OperationLog)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        // 获取注解信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        OperationLog operationLog = method.getAnnotation(OperationLog.class);
        
        // 获取请求信息
        ServletRequestAttributes attributes = 
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;
        
        // 执行方法
        Object result = null;
        Throwable exception = null;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            exception = e;
            throw e;
        } finally {
            // 异步记录日志
            try {
                Long adminId = null;
                String operationIp = null;
                
                if (request != null) {
                    // 获取IP地址
                    operationIp = getIpAddress(request);
                    
                    // 从token获取管理员ID
                    String token = request.getHeader("Authorization");
                    if (token != null && token.startsWith("Bearer ")) {
                        token = token.substring(7);
                        try {
                            adminId = jwtUtil.getAdminIdFromToken(token);
                        } catch (Exception e) {
                            log.debug("从token获取管理员ID失败", e);
                        }
                    }
                }
                
                // 如果从token中获取不到adminId（如登录操作），尝试从返回值中提取
                if (adminId == null) {
                    adminId = extractAdminId(result);
                }
                
                // 获取操作对象ID（从方法参数或返回值中提取）
                Long objectId = extractObjectId(joinPoint, result);
                
                // 构建操作内容
                String content = buildOperationContent(operationLog, joinPoint, result, exception);
                
                // 保存日志
                operationLogService.saveLog(
                    adminId,
                    operationLog.type(),
                    operationLog.object(),
                    objectId,
                    content,
                    operationIp
                );
                
                long endTime = System.currentTimeMillis();
                log.debug("操作日志记录完成: type={}, object={}, adminId={}, time={}ms", 
                         operationLog.type(), operationLog.object(), adminId, (endTime - startTime));
            } catch (Exception e) {
                log.error("记录操作日志失败", e);
            }
        }
        
        return result;
    }

    /**
     * 从登录响应中提取管理员ID
     */
    private Long extractAdminId(Object result) {
        if (result == null) {
            return null;
        }
        
        try {
            // 检查是否是ApiResponse类型
            Method getDataMethod = result.getClass().getMethod("getData");
            Object data = getDataMethod.invoke(result);
            
            if (data != null) {
                // 尝试从AdminLoginResponse中获取adminId
                try {
                    Method getAdminIdMethod = data.getClass().getMethod("getAdminId");
                    Object adminId = getAdminIdMethod.invoke(data);
                    if (adminId instanceof Long) {
                        return (Long) adminId;
                    } else if (adminId instanceof Integer) {
                        return ((Integer) adminId).longValue();
                    }
                } catch (NoSuchMethodException e) {
                    // 忽略，可能不是登录响应
                }
            }
        } catch (Exception e) {
            log.debug("从返回值提取管理员ID失败", e);
        }
        
        return null;
    }

    /**
     * 提取操作对象ID
     */
    private Long extractObjectId(ProceedingJoinPoint joinPoint, Object result) {
        // 1. 从方法参数中提取ID
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof Integer) {
                return ((Integer) arg).longValue();
            } else if (arg instanceof Long) {
                return (Long) arg;
            }
        }
        
        // 2. 从返回结果中提取ID
        if (result != null) {
            try {
                // 尝试获取ID字段
                Method[] methods = result.getClass().getMethods();
                for (Method method : methods) {
                    String methodName = method.getName();
                    if ((methodName.equals("getTypeId") || methodName.equals("getRoleId") || 
                         methodName.equals("getPermissionId")) && method.getParameterCount() == 0) {
                        Object id = method.invoke(result);
                        if (id instanceof Integer) {
                            return ((Integer) id).longValue();
                        } else if (id instanceof Long) {
                            return (Long) id;
                        }
                    }
                }
            } catch (Exception e) {
                log.debug("提取对象ID失败", e);
            }
        }
        
        return null;
    }

    /**
     * 构建操作内容描述
     */
    private String buildOperationContent(OperationLog operationLog, ProceedingJoinPoint joinPoint, 
                                         Object result, Throwable exception) {
        if (!operationLog.content().isEmpty()) {
            return operationLog.content();
        }
        
        // 默认内容
        StringBuilder content = new StringBuilder();
        content.append(operationLog.type()).append(operationLog.object());
        
        if (exception != null) {
            content.append(" - 失败: ").append(exception.getMessage());
        } else {
            content.append(" - 成功");
        }
        
        return content.toString();
    }

    /**
     * 获取真实IP地址
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 处理多个IP的情况
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
