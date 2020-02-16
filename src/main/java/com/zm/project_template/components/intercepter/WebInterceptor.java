package com.zm.project_template.components.intercepter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author zm
 */
@Configuration
public class WebInterceptor implements WebMvcConfigurer {

    @Bean
    RequestInterceptor requestInterceptor(){
        return new RequestInterceptor();
    }

    @Bean
    SqlInterceptor sqlInterceptor(){
        return new SqlInterceptor();
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestInterceptor())
                .addPathPatterns("/api/**").excludePathPatterns("/api/login");

        registry.addInterceptor(sqlInterceptor()).addPathPatterns("/api/order/**");

    }

}