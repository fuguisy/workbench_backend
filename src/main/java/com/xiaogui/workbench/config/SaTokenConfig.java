package com.xiaogui.workbench.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handle -> {
            // 对所有请求进行登录校验
            StpUtil.checkLogin();
        }))
        .addPathPatterns("/**")
        .excludePathPatterns(
            "/auth/login",
            "/auth/register",
            "/auth/info",
            "/feishu/**",
            "/error",
            "/actuator/**"
        );
    }
}
