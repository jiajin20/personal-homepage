package com.bzj.index.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;


/**
 * 添加Swagger全局认证按钮
 * @author Yoooum
 */
@Configuration
public class Swaggerconfig {
    /**
     * 添加全局认证，配置OpenAPI规范
     * @return
     */
// 定义一个Bean，用于定制OpenAPI的配置
    @Bean
    public OpenAPI customOpenAPI() {
        // 创建一个新的OpenAPI对象
        return new OpenAPI()
                // 设置schema要求，这里指定了需要认证的HTTP头信息
                .schemaRequirement(HttpHeaders.AUTHORIZATION, this.securityScheme())
                // 添加安全项，这里指定了认证信息应该包含在请求头中的Authorization字段
                .addSecurityItem(new SecurityRequirement().addList(HttpHeaders.AUTHORIZATION))
                // 设置API的基本信息
                .info(new Info()
                        // 设置API的标题
                        .title("个人主页")
                        // 设置API的描述信息（简介）
                        .description("个人主页接口文档")
                        // 设置API的版本
                        .version("v0.1.0")
                        // 设置API的许可证信息
                        .license(new License().name("Apache 2.0").url("http://springdoc.org"))
                        // 设置API的联系信息（作者）
                        .contact(new io.swagger.v3.oas.models.info.Contact().name("BZJ").url("")));
    }
    /**
     * 配置认证信息,swagger安全方案
     * @return
     */
    private SecurityScheme securityScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)// 设置认证方式
                .scheme("Bearer")//方案bearer
                .bearerFormat("JWT")//格式JWT
                .name(HttpHeaders.AUTHORIZATION)//认证名称
                .in(SecurityScheme.In.HEADER);//认证位置
    }
}