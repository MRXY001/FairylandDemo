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
        // 检查方法名是否是“error”，如果是则跳过。也可以加注解，用注解过滤不需要权限的方法
        HandlerMethod handlerMethod = (HandlerMethod) object;
        Method method = handlerMethod.getMethod();
        String methodName = method.getName();
        if ("error".equals(methodName)) {
            return true;
        }

        // 执行认证，获取header中的token字段
        // 如果不是允许的方法，不会到达这边来，因此是必须需要token
        String token = request.getHeader(ConstantKey.TOKEN_HEADER);// 从 http 请求头中取出 token
        if (token == null) {
            throw new RuntimeException("无 token，请登录：" + methodName);
        }

        // 获取 token 中的 name
        String userId = TokenUtil.getUserIdByToken(token);

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
