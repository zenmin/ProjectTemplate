package com.zm.project_template.components.aop;

import com.zm.project_template.common.CommonException;
import com.zm.project_template.common.constant.CommonConstant;
import com.zm.project_template.common.constant.DefinedCode;
import com.zm.project_template.components.annotation.RequireRole;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

/**
 * @Describle This Class Is 全局权限验证Aspect
 * @Author ZengMin
 * @Date 2019/6/29 16:40
 */
@Component
@Aspect
@Slf4j
public class RoleAspect {

//    @Autowired
//    LogsService logsService;

//    @Autowired
//    UserService userService;

    /**
     * controller子包 下的方法 加注解 同时满足
     * 优先级 高
     */
    @Pointcut("@annotation(com.zm.project_template.components.annotation.RequireRole) && execution(* com.zm.project_template.controller.*.*Controller.*(..))")
    @Order(1)
    private void pointCutMethod() {
    }

    /**
     * controller的class加注解
     * 优先级 低
     */
    @Pointcut("@within(com.zm.project_template.components.annotation.RequireRole)")
    @Order(2)
    private void pointCutClass() {
    }

    /**
     * 两种情况满足一个即可
     */
    @Before("(pointCutMethod() || pointCutClass())")
    public void execAspect(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        // 先从方法上取
        RequireRole requireRole = method.getAnnotation(RequireRole.class);
        // 方法上没有注解从类上取
        if (Objects.isNull(requireRole)) {
            requireRole = method.getDeclaringClass().getAnnotation(RequireRole.class);
        }
        boolean b = this.validRole(requireRole);
        if (!b) {
            throw new CommonException(DefinedCode.NOTAUTH_OPTION, "您没有权限操作！");
        }
    }

    private boolean validRole(RequireRole requireRole) {
//        User user = userService.getLoginUser();
        String userRoleCode = "";//user.getRoleCode();
        if (StringUtils.isBlank(userRoleCode)) {
            return false;
        }
        String[] roleCode = requireRole.value();
        // admin权限不受影响
        if (userRoleCode.equals(CommonConstant.ROLE_ADMIN) || Arrays.asList(roleCode).contains(userRoleCode)) {
            return true;
        } else {
            // 无操作权限
            return false;
        }
    }


}
