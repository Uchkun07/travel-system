package io.github.uchkun07.travelsystem.annotation;

import java.lang.annotation.*;

/**
 * 需要管理员权限的接口注解
 * 用于标注需要权限校验的管理端API
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireAdminPermission {
    
    /**
     * 需要的权限编码
     * 例如: "USER:VIEW", "CONTENT:EDIT" 等
     * 支持多个权限，满足其一即可
     */
    String[] value() default {};
    
    /**
     * 是否需要拥有所有权限
     * true: 需要拥有所有指定权限
     * false: 只需拥有其中一个权限即可
     */
    boolean requireAll() default false;
}
