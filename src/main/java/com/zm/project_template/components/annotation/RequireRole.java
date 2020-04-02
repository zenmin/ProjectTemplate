package com.zm.project_template.components.annotation;

import com.zm.project_template.common.constant.CommonConstant;

import java.lang.annotation.*;

/**
 * @Describle This Class Is 权限注解类
 * - 标注在方法上表示本方法受权限控制 方法优先级高于类
 * - 标注在类上 表示本类所有方法均受权限控制
 * @Author ZengMin
 * @Date 2019/6/29 16:43
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireRole {

    /**
     * RoleCode 角色编码
     *
     * @return
     */
    String[] value() default {CommonConstant.ROLE_ADMIN};

}
