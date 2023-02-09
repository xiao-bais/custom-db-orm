package com.custom.taskmanager.config;

import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: Xiao-Bai
 * @since : 2021/04/06/14:09
 * @Description:
 */

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket createRestApi(){

        ParameterBuilder ticketPar = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<>();
        ticketPar.name("token").description("登录校验")//name表示名称，description表示描述
                .modelRef(new ModelRef("string")).parameterType("header")
                .defaultValue("admin")//给一个默认值
                .required(true).build();//required表示是否必填，defaultvalue表示默认值
        pars.add(ticketPar.build());//添加完此处一定要把下边的带***的也加上否则不生效

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                //扫描的包
                .apis(RequestHandlerSelectors.basePackage("com.custom.taskmanager.controller"))
                .paths(PathSelectors.any())//代表任何地址
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .build()
                .globalOperationParameters(pars)
                ;
    }

    /**
     * swagger 接口文档地址：localhost:7503/doc.html
     */
    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .title("自定义-任务管理列表")//标题
                .version("1.0")//版本
                .description("任务管理列表")//文档说明
                .termsOfServiceUrl("127.0.0.1:2021")//接口地址前缀
                .build();
    }
}
