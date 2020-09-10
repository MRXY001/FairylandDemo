package com.iwxyi.fairyland.Tools;

import java.util.Date;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.iwxyi.fairyland.Config.ConstantKey;
import com.iwxyi.fairyland.Models.User;
import com.iwxyi.fairyland.Services.UserService;

public class TokenUtil {
    public static String createTokenByUser(User user) {
        return JWT.create().withAudience(user.getUserId() + "")// 将 user id 保存到 token 里面
                .withExpiresAt(new Date(System.currentTimeMillis() + 30 * 60 * 1000))// 定义token的有效期
                .sign(Algorithm.HMAC256(ConstantKey.USER_JWT_KEY));// 加密秘钥，也可以使用用户保持在数据库中的密码字符串
    }
    
    public static User getUserByToken(String token) {
        String userId = getUserIdByToken(token);
        return (new UserService()).getUserByUserId(userId);
    }
    
    public static String getUserIdByToken(String token) {
        return JWT.decode(token).getAudience().get(0);
    }
}
