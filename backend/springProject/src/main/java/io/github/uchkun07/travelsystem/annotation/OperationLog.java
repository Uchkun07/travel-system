package io.github.uchkun07.travelsystem.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 * 用于标注需要记录操作日志的方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {
    
    /**
     * 操作类型（如"创建""删除""修改""查询"）
     */
    String type();
    
    /**
     * 操作对象（如"景点类型""权限""角色"）
     */
    String object();
    
    /**
     * 操作内容描述（支持SpEL表达式）
     */
    String content() default "";
}
