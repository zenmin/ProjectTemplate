package com.zm.project_template.components.aop;

import com.zm.project_template.common.CommonException;
import com.zm.project_template.common.constant.CacheConstant;
import com.zm.project_template.common.constant.CommonConstant;
import com.zm.project_template.common.constant.DefinedCode;
import com.zm.project_template.components.annotation.RateLimiter;
import com.zm.project_template.components.business.GuavaCacheUtil;
import com.zm.project_template.util.CommonUtil;
import com.zm.project_template.util.IpHelper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @Describle This Class Is 限流Aspect
 * @Author ZengMin
 * @Date 2019/6/29 16:40
 */
@Component
@Aspect
@Slf4j
public class RateLimiterAspect {

//    @Autowired
//    UserService userService;

    @Autowired
    GuavaCacheUtil guavaCache;

    /**
     * 全局 ：每秒最多处理10个请求
     */
    public static com.google.common.util.concurrent.RateLimiter guavaLimiter = com.google.common.util.concurrent.RateLimiter.create(10.0);

    /**
     * 方法+注释 切入点
     * 优先级 高
     */
    @Pointcut("@annotation(com.zm.project_template.components.annotation.RateLimiter)")
    @Order(1)
    private void pointMethod() {
    }

    /**
     * 类+注释切入点
     * 优先级 低
     */
    @Pointcut("@within(com.zm.project_template.components.annotation.RateLimiter)")
    @Order(2)
    private void pointCutClass() {
    }

    /**
     * 两个 满足一个
     *
     * @param joinPoint
     */
    @Before("pointMethod() || pointCutClass()")
    public void execAspect(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        this.validRate(method);
    }

    private void validRate(Method method) {
        RateLimiter rateLimiter = method.getAnnotation(RateLimiter.class);
        if (Objects.isNull(rateLimiter)) {
            rateLimiter = method.getDeclaringClass().getAnnotation(RateLimiter.class);
        }
        this.limit(rateLimiter, method);
    }

    private boolean limit(RateLimiter rateLimiter, Method method) {
        // 限流数量 用户：每10秒的请求次数  全局：每秒接口响应次数
        String value = rateLimiter.value();
        // 限流目标
        String target = rateLimiter.target();
        // 用户操作限流
        if (CommonConstant.LIMIT_USER.equals(target)) {
            String id = "";//userService.getLoginUser().getId();
            int limit = Integer.parseInt(value);
            String key = CacheConstant.USER_LIMIT + method.getName() + ":" + id;
            // 判断此用户在系统的操作次数
            Object o = guavaCache.get(key);
            if (Objects.nonNull(o)) {
                int i = Integer.parseInt(o.toString());
                if (i >= limit) {
                    throw new CommonException(DefinedCode.USER_LIMIT_ERROR, "操作频繁，请稍后再试！");
                } else {
                    guavaCache.put(key, i + 1);
                }
            } else {
                guavaCache.put(key, 1);
            }
        }

        // 接口全局限流
        if (CommonConstant.LIMIT_ALL.equals(target)) {
            boolean b = guavaLimiter.tryAcquire();
            if (b) {
                return true;
            } else {
                throw new CommonException(DefinedCode.USER_LIMIT_ERROR, "系统繁忙，请稍后再试！");
            }
        }

        // IP全局限流
        if (CommonConstant.LIMIT_USER_IP.equals(target)) {
            int limit = Integer.parseInt(value);
            String key = CacheConstant.USER_LIMIT + method.getName() + ":" + IpHelper.getRequestIpAddr(CommonUtil.getRequest());
            // 判断此IP在系统的操作次数
            Object o = guavaCache.get(key);
            if (Objects.nonNull(o)) {
                int i = Integer.parseInt(o.toString());
                if (i >= limit) {
                    throw new CommonException(DefinedCode.USER_LIMIT_ERROR, "操作频繁，请稍后再试！");
                } else {
                    guavaCache.put(key, i + 1);
                }
            } else {
                guavaCache.put(key, 1);
            }
        }
        return true;
    }
}

