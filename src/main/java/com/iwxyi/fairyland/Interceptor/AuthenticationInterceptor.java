package com.iwxyi.fairyland.Interceptor;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.iwxyi.fairyland.Config.ConstantKey;
import com.iwxyi.fairyland.Tools.TokenUtil;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class AuthenticationInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse httpServletResponse, Object object)
            throws Exception {
        // 如果不是映射到方法直接通过
        if (!(object instanceof HandlerMethod)) {
            return true;
        }
        // 检查方法名。也可以加注解，用注解过滤不需要权限的方法
        HandlerMethod handlerMethod = (HandlerMethod) object;
        Method method = handlerMethod.getMethod();
        LoginRequired methodAnnotation = method.getAnnotation(LoginRequired.class);
        if (methodAnnotation == null) // 没有 @LoginRequired，不需要登录即可使用
            return true;
        String methodName = method.getName();
        if ("error".equals(methodName)) {
            return true;
        }
        
        // 执行认证，获取header中的token字段
        // 如果不是允许的方法，不会到达这边来，因此是必须需要token
        String token = request.getHeader(ConstantKey.TOKEN_HEADER);// 从 http 请求头中取出 token
        if (token == null) {
            throw new RuntimeException("请先登录账户");
        }

        // 获取 token 中的 name
        Long userId = TokenUtil.getUserIdByToken(token);
        // User user = TokenUtil.getUserByToken(token); // 从 token 中获取整个 User 对象

        // 验证 token
        TokenUtil.verifyToken(token);

        // 将用户信息放入到request中，全局可用
        request.setAttribute(ConstantKey.CURRENT_USER, userId); // 保存解析出来的UserID
        return true; // true时才进行处理该请求，false则忽略后续请求
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o,
            ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
            Object o, Exception e) throws Exception {

    }
}
