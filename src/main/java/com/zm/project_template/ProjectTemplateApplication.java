package com.zm.project_template;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableSwagger2Doc
public class ProjectTemplateApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectTemplateApplication.class, args);
    }

}
