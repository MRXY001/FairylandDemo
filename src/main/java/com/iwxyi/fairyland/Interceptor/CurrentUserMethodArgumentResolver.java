package com.iwxyi.fairyland.Interceptor;

import com.iwxyi.fairyland.Config.ConstantKey;
import com.iwxyi.fairyland.Models.User;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * @CurrentUser 注解 解析器
 */
public class CurrentUserMethodArgumentResolver implements HandlerMethodArgumentResolver {
    /**
     * 表示带有 @CurrentUser 注解且为 UserId 的数据格式才进行解析
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class)
                && parameter.getParameterType().isAssignableFrom(User.class);
    }

    /**
     * 从 request 中提取 UserId
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        return (User) webRequest.getAttribute(ConstantKey.CURRENT_USER, RequestAttributes.SCOPE_REQUEST);
    }
}
