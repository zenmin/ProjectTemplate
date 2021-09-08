package com.zm.project_template.config;

import com.zm.project_template.components.filter.AuthenticationInterceptor;
import com.zm.project_template.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @Describle This Class Is SpringSecurity配置
 * @Author ZengMin
 * @Date 2019/4/13 11:45
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(jsr250Enabled = true)   // 启用RolesAllowed注解
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * 必须认证访问的URI
     */
    public static final String[] AUTH_MATCHERS = {"/api/**"};

    /**
     * 允许匿名访问URI
     */
    public static final String[] NOAUTH_MATCHERS = {"/open/**", "/api/login/**", "/error/**", "/doc"};

    @Autowired
    private AccessDeniedHandler accessDeniedResolve;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationInterceptor authenticationInterceptor;

    @Autowired
    public void configureAuthentication(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                // 设置UserDetailsService
                .userDetailsService(userDetailsService)
                // 使用BCrypt进行密码的hash
                .passwordEncoder(CommonUtil.BCRYPT_PASSWORD_ENCODER);
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.
                exceptionHandling().accessDeniedHandler(accessDeniedResolve).and()
                // 不需要csrf
                .csrf().disable()
                .exceptionHandling().authenticationEntryPoint(authenticationInterceptor).and()
                // 基于token，所以不需要session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests()
                // 允许匿名访问的uri
                .mvcMatchers(NOAUTH_MATCHERS).permitAll()
                // 必须授权访问的uri
                .mvcMatchers(AUTH_MATCHERS).authenticated()
                // 除此之外都可以访问
                .anyRequest().permitAll();
        // 放行cors
        httpSecurity.cors();
        // 禁用缓存
        httpSecurity.headers().cacheControl();

        // 添加AuthFilter
        httpSecurity
                .addFilterBefore(authenticationInterceptor, UsernamePasswordAuthenticationFilter.class);
    }

    /**
     * 放开静态资源
     *
     * @param web
     */
    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers(
                "/webjars/**",
                "/*.ico",
                "/**/*.css",
                "/**/*.js",
                "/**/*.png",
                "/**/*.gif",
                "/swagger-resources/**",
                "/v2/**",
                "/**/*.ttf",
                "/**/*.json",
                "/**/*.txt",
                "/**/*.html",
                "/v2/api-docs",
                "/druid/*"
        );
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
