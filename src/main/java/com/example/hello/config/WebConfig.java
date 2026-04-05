package com.example.hello.config;

import com.example.hello.interceptor.ApiTokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置
 * 注册拦截器
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private ApiTokenInterceptor apiTokenInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 API Token 拦截器，拦截所有请求
        // 具体是否需要验证由 @RequireApiToken 注解决定
        registry.addInterceptor(apiTokenInterceptor)
                .addPathPatterns("/api/**");
    }
}

