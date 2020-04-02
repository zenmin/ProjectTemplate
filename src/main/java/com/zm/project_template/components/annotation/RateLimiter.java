package com.zm.project_template.components.annotation;



import com.zm.project_template.common.constant.CommonConstant;

import java.lang.annotation.*;

/**
 * @Describle This Class Is 应用限流注解
 * - 标注在类上 表示本类所有方法限流
 * - 标注在方法上 表示本方法限流 方法优先级高于类
 * @Author ZengMin
 * @Date 2019/7/17 16:54
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiter {

    /**
     * 限流：用户：每10秒不能超过多少次请求  全局：接口每秒处理请求数量
     *
     * @return
     */
    String value() default "1";

    /**
     * CommonConstant.LIMIT_XXX 默认用户限流
     *
     * @return
     */
    String target() default CommonConstant.LIMIT_USER;

}
