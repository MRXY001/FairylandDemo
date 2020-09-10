package com.iwxyi.fairyland.Config;

public class ConstantKey {
    public static final String TOKEN_HEADER = "Authorization"; // request的header中存储token的key
	public static final String CURRENT_USER = "user_id"; // Token中的当前用户
    public static final String USER_JWT_KEY = "spring-boot-jwt-key~#^"; // JWT秘钥，发布要改
}