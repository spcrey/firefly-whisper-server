package com.spcrey.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.spcrey.interceptor.LoginInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor).excludePathPatterns(
            "/", 
            "/user/register", "/user/sendSms", "/user/infoOther", "/user/loginByCode", "/user/loginByPassword", "/user/updatePassword",
            "/article", "/article/list", "/article/listComments"
            );
    }
}
