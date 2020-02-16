package com.zm.project_template.components.aop;

import com.zm.project_template.common.CommonException;
import com.zm.project_template.common.constant.CacheConstant;
import com.zm.project_template.common.constant.CommonConstant;
import com.zm.project_template.common.constant.DefinedCode;
import com.zm.project_template.components.annotation.RateLimiter;
import com.zm.project_template.components.business.GuavaCacheUtil;
import com.zm.project_template.util.CommonUtil;
import com.zm.project_template.util.IpHelper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @Describle This Class Is 全局限流验证Aspect
 * @Author ZengMin
 * @Date 2019/6/29 16:40
 */
//@Scope
//@Component
//@Aspect
//@Slf4j
public class RateLimiterAspect {

//    @Autowired
//    UserService userService;

    @Autowired
    GuavaCacheUtil guavaCache;

    /**
     * 每秒最多处理一个请求
     */
    public static com.google.common.util.concurrent.RateLimiter guavaLimiter = com.google.common.util.concurrent.RateLimiter.create(1.0);

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping)")
    private void pointCut() {
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    private void pointCutRequestMapping() {
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping)")
    private void pointCutGetMapping() {
    }

    @Around("pointCut() || pointCutRequestMapping() || pointCutGetMapping()")
    public Object execAspect(ProceedingJoinPoint joinPoint) throws Throwable {
        this.validRate(joinPoint);
        return joinPoint.proceed();
    }

    private void validRate(ProceedingJoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        if (signature instanceof MethodSignature) {
            MethodSignature methodSignature = (MethodSignature) signature;
            Method method = methodSignature.getMethod();
            // 判断限流注解在方法上 还是在类上
            RateLimiter methodRateLimiter = method.getAnnotation(RateLimiter.class);
            // 如果在方法上  以方法的为准
            if (Objects.nonNull(methodRateLimiter)) {
                this.limit(methodRateLimiter, method);
            } else {
                RateLimiter classRateLimiter = method.getDeclaringClass().getAnnotation(RateLimiter.class);
                if (Objects.nonNull(classRateLimiter)) {
                    this.limit(classRateLimiter, method);
                }
            }
        }
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
            String key = CacheConstant.USER_LIMIT + method.getName() + id;
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
            String key = CacheConstant.USER_LIMIT + method.getName() + IpHelper.getRequestIpAddr(CommonUtil.getRequest());
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

