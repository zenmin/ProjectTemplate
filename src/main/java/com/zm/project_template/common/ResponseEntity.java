package com.zm.project_template.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.zm.project_template.common.constant.DefinedCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Describle This Class Is 全局接口返回类
 * @Author ZengMin
 * @Date 2019/1/3 19:45
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.ALWAYS)
@ApiModel(value = "接口统一返回类")
public class ResponseEntity {

    @ApiModelProperty(value = "响应码 常见响应码（100：成功，101：资源未找到，102：参数异常，700：没有权限操作，989：操作频繁，997：登录超时，999：系统异常）")
    private int code;

    @ApiModelProperty(value = "响应说明")
    private String msg;

    @ApiModelProperty(value = "响应数据")
    private Object data;

    public static ResponseEntity success() {
        return new ResponseEntity(DefinedCode.SUCCESS, "success", null);
    }

    public static ResponseEntity success(Object data) {
        return new ResponseEntity(DefinedCode.SUCCESS, "success", data);
    }

    public static ResponseEntity error(int code, String msg) {
        return new ResponseEntity(code, msg, null);
    }

    public static ResponseEntity error() {
        return new ResponseEntity(DefinedCode.ERROR, "失败", null);
    }

    public static ResponseEntity error(String msg) {
        return new ResponseEntity(DefinedCode.ERROR, msg, null);
    }


}
