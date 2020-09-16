package com.iwxyi.fairyland.Authentication;

import com.iwxyi.fairyland.Config.ConstantKey;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * @CurrentUser 注解 解析器
 */
public class LoginUserIdMethodArgumentResolver implements HandlerMethodArgumentResolver {
    /**
     * 表示带有 @CurrentUser 注解且为 UserId 的数据格式才进行解析
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginUser.class)
                && parameter.getParameterType().isAssignableFrom(Long.class);
    }

    /**
     * 从 request 中提取 UserId，并放入方法参数中
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        return (Long) webRequest.getAttribute(ConstantKey.CURRENT_USER, RequestAttributes.SCOPE_REQUEST);
    }
}
