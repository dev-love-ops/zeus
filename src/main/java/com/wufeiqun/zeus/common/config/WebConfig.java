package com.wufeiqun.zeus.common.config;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final LoginStatusInterceptor loginStatusInterceptor;
    private final AdminInterceptor adminInterceptor;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginStatusInterceptor).addPathPatterns("/api/**")
                .excludePathPatterns("/api/health/**")
                .excludePathPatterns("/api/login/**");
        registry.addInterceptor(adminInterceptor).addPathPatterns("/admin/**");
        //webSocket接口是 `/wsApi/**`
        //开放平台接口是 `/openApi/**`
    }

}
