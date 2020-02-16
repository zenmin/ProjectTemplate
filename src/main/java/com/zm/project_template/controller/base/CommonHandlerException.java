package com.zm.project_template.controller.base;

import com.zm.project_template.common.CommonException;
import com.zm.project_template.common.ResponseEntity;
import com.zm.project_template.common.constant.DefinedCode;
import com.zm.project_template.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${spring.servlet.multipart.max-file-size}")
    String size;

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity handler(RuntimeException e, HttpServletRequest request) {
        // 统一接口异常
        if (e instanceof CommonException) {
            return ResponseEntity.error(((CommonException) e).getCode(), e.getMessage());
        }

        // 文件上传异常
        if (e instanceof MultipartException) {
            e.printStackTrace();
            return ResponseEntity.error(DefinedCode.FILE_UPLOAD_ERROR, "文件过大，仅允许上传" + size + "以内的文件！");
        }

        log.error("{} 出现异常信息：", DateUtil.getNowTime());
        e.printStackTrace();
        if ("dev".equals(env)) {
            return ResponseEntity.error(DefinedCode.ERROR, "系统出错，请稍后再试！");
        } else {
            return ResponseEntity.error("系统繁忙，请稍后再试！");
        }
    }


}
