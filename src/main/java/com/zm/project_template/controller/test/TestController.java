package com.zm.project_template.controller.test;

import com.alibaba.fastjson.JSONObject;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.zm.project_template.common.ResponseEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Describle This Class Is
 * @Author ZengMin
 * @Date 2021/9/8 10:47
 */
@RestController
@RequestMapping("/test")
@Api("接口测试")
public class TestController {

    @ApiImplicitParam(value = "名称", name = "name", required = true)
    @PostMapping("/t")
    @ApiOperationSupport(order = 1)
    public ResponseEntity testApi(@RequestBody JSONObject params) {
        return ResponseEntity.success(params);
    }


}
