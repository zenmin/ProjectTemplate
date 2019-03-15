package com.zm.project_template.components.aop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zm.project_template.common.constant.RequestConstant;
import com.zm.project_template.components.annotation.HandlerMethod;
import com.zm.project_template.util.IpHelper;
import com.zm.project_template.util.StaticUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * @Describle This Class Is 全局操作切面类
 * @Author ZengMin
 * @Date 2019/1/13 16:40
 */
@Component
@Aspect
@Slf4j
public class LogAspect {

    // 注入一个LogService

    @Pointcut("execution(* com.zm.project_template.service.*Service.*(..))")
    private void pointCut() {
    }

    @Around("@annotation(handlerMethod) && pointCut()")
    public Object execAspect(ProceedingJoinPoint joinPoint, HandlerMethod handlerMethod) throws Throwable {
        try {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
            HttpServletRequest request = servletRequestAttributes.getRequest();
            this.saveLogs(joinPoint, request, servletRequestAttributes);
        }catch (Exception e){
            return joinPoint.proceed();
        }
        return joinPoint.proceed();
    }

    private void saveLogs(ProceedingJoinPoint joinPoint, HttpServletRequest request, ServletRequestAttributes servletRequestAttributes) {
        //取操作名称和描述
        Object[] args = joinPoint.getArgs();
        //取方法入参
        String params = "";
        try {
            params = StaticUtil.objectMapper.writeValueAsString(args);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        //取请求ip
        String ip = IpHelper.getRequestIpAddr(request);
        log.info("监听到管理员全局操作 正在写入日志-->客户端IP:{}",ip);
        //取操作人
        String optUserName = "";
        String token = request.getHeader(RequestConstant.TOKEN);
        Object aThis = joinPoint.getSignature();
        if (aThis instanceof MethodSignature) {
            MethodSignature methodSignature = (MethodSignature) aThis;
            Method method = methodSignature.getMethod();
            HandlerMethod annotation = method.getAnnotation(HandlerMethod.class);
            String optDesc = annotation.optDesc();
            String optName = annotation.optName();
        } else {
            throw new IllegalArgumentException("该注解仅用于Service实现方法上，请勿乱用！");
        }
        //异步保存
    }


}
