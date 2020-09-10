package com.iwxyi.fairyland.Tools;

import java.util.Date;

import javax.annotation.PostConstruct;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.iwxyi.fairyland.Config.ConstantKey;
import com.iwxyi.fairyland.Models.User;
import com.iwxyi.fairyland.Repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TokenUtil {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private static UserRepository staticUserRepository;

    /**
     * 初始化静态变量。只找到这么调用静态autowire的方式，不这样做的话repository都是空的
     */
    @PostConstruct
    public void init() {
        staticUserRepository = userRepository;
    }

    public static String createTokenByUser(User user) {
        return JWT.create().withAudience(user.getUserId() + "")// 将 user id 保存到 token 里面
                .withExpiresAt(new Date(System.currentTimeMillis() + 30 * 60 * 1000))// 定义token的有效期
                .sign(Algorithm.HMAC256(ConstantKey.USER_JWT_KEY));// 加密秘钥，也可以使用用户保持在数据库中的密码字符串
    }

    public static boolean verifyToken(String token) {
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(ConstantKey.USER_JWT_KEY)).build();
        try {
            jwtVerifier.verify(token);
        } catch (JWTVerificationException e) {
            throw new RuntimeException("无效 token，请重新登录");
        }
        return true;
    }

    public static Long getUserIdByToken(String token) {
        try {
            return Long.parseLong(JWT.decode(token).getAudience().get(0));
        } catch (JWTDecodeException j) {
            throw new RuntimeException("无效 token，请重新登录");
        }
    }

    public static User getUserByToken(String token) {
        Long userId = getUserIdByToken(token);
        return staticUserRepository.findByUserId(userId);
    }
}
