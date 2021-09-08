package com.zm.project_template.controller.base;

import com.zm.project_template.common.ResponseEntity;
import com.zm.project_template.common.constant.DefinedCode;
import com.zm.project_template.util.IpHelper;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Describle This Class Is 全局服务端异常处理器
 * @Author ZengMin
 * @Date 2019/1/3 19:43
 */
@ControllerAdvice
public class CommonServerException extends ServletException {

    /**
     * 参数缺少异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public ResponseEntity handler(MissingServletRequestParameterException e) {
        return ResponseEntity.error(DefinedCode.PARAMS_ERROR, "缺少必要参数，参数名：" + e.getParameterName() + "，类型：" + e.getParameterType());
    }

    /**
     * 参数类型异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(BindException.class)
    @ResponseBody
    public ResponseEntity handler(BindException e) {
        return ResponseEntity.error(DefinedCode.PARAMS_ERROR, "参数类型异常，参数名称："
                + e.getFieldError().getField() + "，需要类型" + e.getFieldType(e.getFieldError().getField()).getSimpleName());
    }

    /**
     * 参数类型异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseBody
    public ResponseEntity handler(HttpMediaTypeNotSupportedException e) {
        e.printStackTrace();
        return ResponseEntity.error(DefinedCode.PARAMS_ERROR, "请求数据类型错误，除文件上传外，应全局使用application/json");
    }

    /**
     * json参数类型异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public ResponseEntity handler(HttpMessageNotReadableException e) {
        return ResponseEntity.error(DefinedCode.PARAMS_ERROR, "参数类型异常，请检查字段类型！");
    }

    /**
     * 全局异常
     *
     * @param e
     * @param request
     * @param model
     * @param response
     * @return
     * @throws IOException
     */
    @ExceptionHandler(ServletException.class)
    public String handler(ServletException e, HttpServletRequest request, Model model, HttpServletResponse response) throws IOException {
        if (e instanceof HttpRequestMethodNotSupportedException) {
            model.addAttribute("error", "非法访问！你的IP已记录：" + IpHelper.getRequestIpAddr(request));
        }

        e.printStackTrace();
        return "error/5xx";
    }

}
