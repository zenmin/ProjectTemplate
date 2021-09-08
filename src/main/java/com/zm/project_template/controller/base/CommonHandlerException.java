package com.zm.project_template.controller.base;

import com.zm.project_template.common.CommonException;
import com.zm.project_template.common.ResponseEntity;
import com.zm.project_template.common.constant.DefinedCode;
import com.zm.project_template.util.DateUtil;
import com.zm.project_template.util.EmailUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;

import javax.servlet.http.HttpServletRequest;

/**
 * @Describle This Class Is 全局异常处理器
 * @Author ZengMin
 * @Date 2019/1/3 19:43
 */
@RestControllerAdvice
@Slf4j
public class CommonHandlerException extends RuntimeException {

    @Value("${spring.profiles.active}")
    private String env;

    @Value("${spring.servlet.multipart.max-request-size}")
    String size;

    @Autowired
    EmailUtil emailUtil;

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity handler(RuntimeException e, HttpServletRequest request) {
        if (e instanceof HttpMessageNotReadableException) {
            return ResponseEntity.error(DefinedCode.PARAMS_ERROR, "Json参数类型异常！");
        }

        // 统一接口异常
        if (e instanceof CommonException) {
            return ResponseEntity.error(((CommonException) e).getCode(), e.getMessage(), ((CommonException) e).getData());
        }

        // 文件上传异常
        if (e instanceof MultipartException) {
            return ResponseEntity.error(DefinedCode.FILE_UPLOAD_ERROR, "文件过大，仅允许上传" + size + "以内的文件！");
        }
        // SpringSecurity异常(登录/认证)
        if (e instanceof AuthenticationException) {
            if (e instanceof BadCredentialsException) {
                return ResponseEntity.error(DefinedCode.NOTAUTH, "用户名或密码错误！");
            }
            return ResponseEntity.error(DefinedCode.NOTAUTH, e.getMessage());
        }

        if (e instanceof AccessDeniedException) {
            return ResponseEntity.error(DefinedCode.NOTAUTH_OPTION, StringUtils.isNotBlank(e.getMessage()) ? e.getMessage() : "您没有权限操作！");
        }
        log.error("{} 出现异常信息：", DateUtil.getNowTime());
        e.printStackTrace();
        if ("dev".equals(env)) {
            return ResponseEntity.error(DefinedCode.ERROR, "系统出错，请稍后再试！");
        } else {
            // 发送错误邮件
            emailUtil.sendErrorMail(null, request, e);
            return ResponseEntity.error("系统繁忙，请稍后再试！");
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handler(MethodArgumentNotValidException e, HttpServletRequest request) {
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        return ResponseEntity.error(DefinedCode.PARAMS_ERROR, message);
    }

}
