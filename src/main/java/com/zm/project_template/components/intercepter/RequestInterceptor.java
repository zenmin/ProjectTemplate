package com.zm.project_template.components.intercepter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zm.project_template.common.CommonException;
import com.zm.project_template.common.constant.DefinedCode;
import com.zm.project_template.util.IpHelper;
import com.zm.project_template.util.StaticUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @Describle This Class Is 验证所有请求权限
 * @Author ZengMin
 * @Date 2019/1/3 19:18
 */
@Slf4j
public class RequestInterceptor implements HandlerInterceptor {

//    @Autowired
//    CommonLogService commonLogService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws JsonProcessingException {
        String token = request.getHeader("token");
//        if(StringUtils.isBlank(token)){
//            throw new CommonException(DefinedCode.NOTAUTH,"登陆超时，请重新登录！");
//        }
//        // 程序验证
//        boolean b = userInfoUtil.checkAuth(null);
//        if(!b){
//            throw new CommonException(DefinedCode.NOTAUTH,"程序未授权，请联系卖家授权！");
//        }
        // cacheManager验证用户登录与否
//        userInfoUtil.getUserInfo(token);
        Map<String, String[]> parameterMap = request.getParameterMap();
        String params = StaticUtil.objectMapper.writeValueAsString(parameterMap);
        log.info("客户端ip:[{}]请求URL:[{}] ,请求params:[{}]",IpHelper.getRequestIpAddr(request), request.getRequestURL(), params);
//        commonLogService.saveLog(new CommonLog(IpHelper.getRequestIpAddr(request), request.getRequestURL().toString(),params));
        return true;
    }

}
