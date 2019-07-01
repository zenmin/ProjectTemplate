package com.zm.project_template.components.aop;

import com.zm.project_template.common.CommonException;
import com.zm.project_template.common.constant.DefinedCode;
import com.zm.project_template.common.constant.RequestConstant;
import com.zm.project_template.components.annotation.RequireRole;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * @Describle This Class Is 全局权限验证Aspect
 * @Author ZengMin
 * @Date 2019/6/29 16:40
 */
@Component
@Aspect
@Slf4j
public class RoleAspect {

    @Pointcut("execution(* com.zhuoan.codeapi.controller.*.*Controller.*(..))")
    private void pointCut() {
    }

    @Around("@annotation(requireRole) && pointCut()")
    public Object execAspect(ProceedingJoinPoint joinPoint, RequireRole requireRole) throws Throwable {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
        HttpServletRequest request = servletRequestAttributes.getRequest();
        boolean b = this.validRole(joinPoint, request);
        if (b) {
            return joinPoint.proceed();
        } else {
            throw new CommonException(DefinedCode.NOTAUTH, "您没有权限操作！");
        }
    }

    private boolean validRole(ProceedingJoinPoint joinPoint, HttpServletRequest request) throws IOException {
        String token = request.getHeader(RequestConstant.TOKEN);
        Object attribute = request.getAttribute(token);
//        User user = StaticUtil.objectMapper.readValue(JSONUtil.toJSONString(attribute), User.class);
//        String userRoleCode = user.getRoleCode();
//        if (StringUtils.isBlank(userRoleCode)) {
//            return false;
//        }
        Signature signature = joinPoint.getSignature();
        if (signature instanceof MethodSignature) {
            MethodSignature methodSignature = (MethodSignature) signature;
            Method method = methodSignature.getMethod();
            RequireRole annotation = method.getAnnotation(RequireRole.class);
            String[] roleCode = annotation.value();
//            if (userRoleCode.equals(CommonConstant.ROLE_ADMIN) || Arrays.asList(roleCode).contains(userRoleCode)) {
//                // admin权限不受影响
            return true;
//            } else {
//                // 无操作权限
//                return false;
//            }
        } else {
            throw new IllegalArgumentException("该注解仅用于Controller方法上！");
        }
    }


}
