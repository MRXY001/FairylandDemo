package com.iwxyi.fairyland.server.Authentication;

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
        String[] addPath = { "/server/**" }; // 只拦截一部分
        String[] excludePath = {};
        // 拦截器添加的顺序就是执行顺序
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
    public LoginUserMethodArgumentResolver currentUserMethodArgumentResolver() {
        return new LoginUserMethodArgumentResolver();
    }

    @Bean
    public LoginUserIdMethodArgumentResolver currentUserIdMethodArgumentResolver() {
        return new LoginUserIdMethodArgumentResolver();
    }
}
