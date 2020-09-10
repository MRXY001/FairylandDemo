package com.iwxyi.fairyland.Interceptor;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
public class InterceptorConfig extends WebMvcConfigurationSupport {
    /**
     * 自动调用，配置拦截器，用来检测token等权限
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String[] addPath = { "/**" };
        String[] excludePath = { "/user/login", "/user/register", "/user/sendPhoneValidation" };
        registry.addInterceptor(authenticationInterceptor()).addPathPatterns(addPath).excludePathPatterns(excludePath);
    }

    /**
     * 登录校验拦截器，在addInterceptor中被调用
     */
    @Bean
    public AuthenticationInterceptor authenticationInterceptor() {
        return new AuthenticationInterceptor();
    }

    /**
     * 参数解析器
     */
    @Override
    protected void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(currentUserMethodArgumentResolver());
        argumentResolvers.add(currentUserIdMethodArgumentResolver());
        super.addArgumentResolvers(argumentResolvers);
    }

    /**
     * CurrentUser 注解参数解析器
     */
    @Bean
    public CurrentUserMethodArgumentResolver currentUserMethodArgumentResolver() {
        return new CurrentUserMethodArgumentResolver();
    }
    
    @Bean
    public CurrentUserIdMethodArgumentResolver currentUserIdMethodArgumentResolver() {
        return new CurrentUserIdMethodArgumentResolver();
    }
}
