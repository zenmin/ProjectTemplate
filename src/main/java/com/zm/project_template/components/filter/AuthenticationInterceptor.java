package com.zm.project_template.components.filter;

import cn.hutool.core.io.IoUtil;
import com.alibaba.fastjson.JSONObject;
import com.zm.project_template.common.CommonException;
import com.zm.project_template.common.ResponseEntity;
import com.zm.project_template.common.constant.DefinedCode;
import com.zm.project_template.common.constant.RequestConstant;
import com.zm.project_template.config.SecurityConfig;
import com.zm.project_template.util.CommonUtil;
import com.zm.project_template.util.JSONUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @Describle This Class Is Security EntryPoint  默认加入到Spring security的拦截器中
 * @Author ZengMin
 * @Date 2020/4/4 10:55
 */
@Component
public class AuthenticationInterceptor extends OncePerRequestFilter implements AuthenticationEntryPoint, AccessDeniedHandler, WebMvcConfigurer {

//    @Autowired
//    LoginService loginService;

    private static final AntPathMatcher antPathMatcher = new AntPathMatcher();

    /**
     * 添加开放接口拦截器
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
    }

    /**
     * 检查登陆
     *
     * @param request
     * @param response
     * @param chain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain) throws ServletException, IOException {
        String token = request.getHeader(RequestConstant.TOKEN);
        // 如果token不为空 并且以/api开头 否则交给其他interceptor和security
        if (StringUtils.isNotBlank(token) && !StringUtils.equals(token, "null") && this.matchPattern(request.getRequestURI())) {
            try {
                boolean b = true;//loginService.checkLoginAuth(token, request);
                if (!b) {
                    this.writeErrorMsg(response, JSONUtil.toJSONString(ResponseEntity.error(DefinedCode.NOTAUTH, "登录超时，请重新登录！")));
                    return;
                }
            } catch (CommonException e) {
                this.writeErrorMsg(response, JSONUtil.toJSONString(ResponseEntity.error(e.getCode(), e.getMessage())));
                return;
            }
        }
        // 只处理非上传/非开放接口的POST请求
        if (!StringUtils.startsWithIgnoreCase(request.getContentType(), "multipart/form-data") &&
                StringUtils.equalsIgnoreCase(request.getMethod(), HttpMethod.POST.name()) && !StringUtils.startsWithIgnoreCase(request.getRequestURI(), "/open")) {
            ServletRequest requestWrapper = new DecodeRequestWrapper(request);
            chain.doFilter(requestWrapper, response);
        } else {
            chain.doFilter(request, response);
        }
    }

    /**
     * 未登录
     *
     * @param httpServletRequest
     * @param httpServletResponse
     * @param e
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException {
        httpServletResponse.setStatus(HttpStatus.OK.value());
        httpServletResponse.setCharacterEncoding(Charset.defaultCharset().name());
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        PrintWriter printWriter = httpServletResponse.getWriter();
        String body = JSONUtil.toJSONString(ResponseEntity.error(DefinedCode.NOTAUTH, "登录超时，请重新登录！"));
        printWriter.write(body);
        printWriter.flush();
    }

    /**
     * 角色验证
     *
     * @param httpServletRequest
     * @param httpServletResponse
     * @param e
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
        httpServletResponse.setStatus(HttpStatus.OK.value());
        httpServletResponse.setCharacterEncoding(Charset.defaultCharset().name());
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        PrintWriter printWriter = httpServletResponse.getWriter();
        String body = JSONUtil.toJSONString(ResponseEntity.error(DefinedCode.NOTAUTH_OPTION, "无权限操作！"));
        printWriter.write(body);
        printWriter.flush();
    }

    private boolean matchPattern(String uri) {
        return antPathMatcher.match(SecurityConfig.AUTH_MATCHERS[0], uri);
    }

    private void writeErrorMsg(HttpServletResponse response, String body) {
        response.setStatus(HttpStatus.OK.value());
        response.setCharacterEncoding(Charset.defaultCharset().name());
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        PrintWriter printWriter = null;
        try {
            printWriter = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        printWriter.write(body);
        printWriter.flush();
    }

    /**
     * 对requestBody进行解密
     */
    public class DecodeRequestWrapper extends HttpServletRequestWrapper {
        /**
         * 请求体
         */
        private String body;

        public DecodeRequestWrapper(HttpServletRequest request) {
            super(request);
            // 将body数据存储起来
            String body = this.getBody(request);
            if (StringUtils.isNotBlank(body)) {
                try {
                    JSONObject object = JSONObject.parseObject(body);
                    String paramsEncode = object.getString("p");
                    if (StringUtils.isNotBlank(paramsEncode)) {
                        // 解密请求参数
                        body = CommonUtil.aesDecodeRequest(paramsEncode);
                    } else {
                        body = "";
                    }
                } catch (Exception e) {
                    body = "";
                }
            }
            this.body = body;
        }

        /**
         * 获取请求体
         *
         * @param request 请求
         * @return 请求体
         */
        private String getBody(HttpServletRequest request) {
            return AuthenticationInterceptor.getBodyString(request);
        }

        /**
         * 获取请求体
         *
         * @return 请求体
         */
        public String getBody() {
            return body;
        }

        @Override
        public BufferedReader getReader() throws IOException {
            return new BufferedReader(new InputStreamReader(this.getInputStream()));
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            // 创建字节数组输入流
            final ByteArrayInputStream bais = new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8));

            return new ServletInputStream() {
                @Override
                public boolean isFinished() {
                    return false;
                }

                @Override
                public boolean isReady() {
                    return false;
                }

                @Override
                public void setReadListener(ReadListener readListener) {
                }

                @Override
                public int read() throws IOException {
                    return bais.read();
                }
            };
        }
    }

    /**
     * 获取请求Body
     *
     * @param request
     * @return
     */
    public static String getBodyString(ServletRequest request) {
        InputStream inputStream = null;
        BufferedReader reader = null;
        try {
            inputStream = request.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            return IoUtil.read(reader);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtil.close(inputStream);
            IoUtil.close(reader);
        }
        return "";
    }
}
