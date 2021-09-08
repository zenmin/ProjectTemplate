package com.zm.project_template.components.aop;

import com.zm.project_template.components.annotation.HandlerMethod;
import com.zm.project_template.util.CommonUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * @Describle This Class Is 全局日志切面
 * @Author ZengMin
 * @Date 2019/1/13 16:40
 */
@Component
@Aspect
public class LogAspect {

//    @Autowired
//    LogsService operateLogsService;

    /**
     * service实现
     */
    @Pointcut("execution(* com.zm.project_template.service.*Service.*(..))")
    private void pointCutService() {
    }

    /**
     * controller子包
     */
    @Pointcut("execution(* com.zm.project_template.controller.*.*Controller.*(..))")
    private void pointCutController() {
    }

    @Around("@annotation(handlerMethod) && (pointCutController() || pointCutService())")
    public Object execAspect(ProceedingJoinPoint joinPoint, HandlerMethod handlerMethod) throws Throwable {
        HttpServletRequest request = CommonUtil.getRequest();
        // 执行方法获取结果
        Object result = joinPoint.proceed();
        this.saveLogs(joinPoint, request, result);
        return result;
    }

    private void saveLogs(ProceedingJoinPoint joinPoint, HttpServletRequest request, Object result) {
        // 取当前注解标记方法
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
//        operateLogsService.saveAsync(request, method, result);
    }

}
