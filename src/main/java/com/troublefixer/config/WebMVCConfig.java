package com.troublefixer.config;

import com.troublefixer.handler.LoginInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMVCConfig implements WebMvcConfigurer {

    private final LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/regist")
                .excludePathPatterns("/login");
//                .excludePathPatterns("/upload/forTrain").excludePathPatterns("/upload/forTest");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {//解决跨域问题
        registry.addMapping("/**").allowedOrigins("http://localhost:8080");
    }
}
