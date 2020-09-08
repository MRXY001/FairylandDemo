package com.iwxyi.fairyland.Interceptor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    /**
     * 自动调用，配置拦截器
     * 用来检测token等权限
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String[] addPath = { "/**" };
        String[] excludePath = {"/user/login", "/user/register", "/user/sendPhoneValidation"};
        registry.addInterceptor(authenticationInterceptor()).addPathPatterns(addPath).excludePathPatterns(excludePath);
    }

    @Bean
    public AuthenticationInterceptor authenticationInterceptor() {
        return new AuthenticationInterceptor();
    }
}
