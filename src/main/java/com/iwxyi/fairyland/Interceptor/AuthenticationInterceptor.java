package com.iwxyi.fairyland.Interceptor;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.iwxyi.fairyland.Config.ConstantKey;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class AuthenticationInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse httpServletResponse,
            Object object) throws Exception {
        // 如果不是映射到方法直接通过
        if (!(object instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) object;
        Method method = handlerMethod.getMethod();
        String methodName = method.getName();
        // 检查方法名是否是“login”如果是则跳过，也可以加注解，用注解过滤不需要权限的方法
        if ("error".equals(methodName)) {
            return true;
        }
        // 执行认证
        String token = request.getHeader("token");// 从 http 请求头中取出 token
        if (token == null) {
            // 不带token的话会出错，控制台报错
            // 但是问题不大，不用理会它
            // throw new ServiceException(401, "无 token，请登录：" + methodName);
            throw new RuntimeException("无 token，请登录：" + methodName);
        }
        // 获取 token 中的 name
        String userId;
        try {
            userId = JWT.decode(token).getAudience().get(0);
        } catch (JWTDecodeException j) {
            // throw new ServiceException(401, "无效 token，请重新登录：" + methodName);
            throw new RuntimeException("无效 token，请重新登录");
        }
        // 验证 token
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(ConstantKey.USER_JWT_KEY)).build();
        try {
            jwtVerifier.verify(token);
        } catch (JWTVerificationException e) {
            // throw new ServiceException(401, "无效 token，请重新登录：" + methodName);
            throw new RuntimeException("无效 token，请重新登录");
        }
        request.setAttribute("user_id", userId); // 保存解析出来的UserID
        return true;
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
