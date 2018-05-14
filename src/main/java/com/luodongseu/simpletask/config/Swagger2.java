package com.luodongseu.simpletask.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger 的全局配置
 *
 * @author luodongseu
 */
@Configuration
@EnableSwagger2
public class Swagger2 {

    @Bean
    public Docket buildApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                        .title("Simple Task Restful Apis")
                        .description("@github luodongseu/simple-task")
                        .termsOfServiceUrl("https://github.com/luodongseu/simple-task")
                        .contact(new Contact("luodongseu", "https://github.com/luodongseu", "luodongseu@gmail.com"))
                        .version("1.0")
                        .build())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.luodongseu.simpletask"))
                .paths(PathSelectors.any())
                .build();
    }

}
