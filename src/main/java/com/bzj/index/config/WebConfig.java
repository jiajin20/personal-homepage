package com.bzj.index.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {


    // 配置静态资源路径
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射静态资源路径
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/", "file:" + System.getProperty("user.dir") + "/static/img/");
    }

    // 配置拦截器
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new LoginInterceptor(redisTemplate))
//                .addPathPatterns("/**") // 拦截所有接口
//                .excludePathPatterns("/user/loginjson","/user/getCode","/user/logout","/user/maillogin","/user/profile","/user/studentregisterjson","/user/updatapasswordjson","/user/teacherregisterjson",
//                        "/doc.html", "/swagger-ui.html", "/swagger-ui/**", "/swagger-resources/**", "/v3/api-docs/**", "/webjars/**", "/static/**"); // 放行登录注册等接口
//    }

}
