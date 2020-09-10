package com.iwxyi.fairyland.Services;



import java.util.Date;

import com.iwxyi.fairyland.Models.User;
import com.iwxyi.fairyland.Repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired // 装配数据库操作类
    UserRepository userRepository;

    /**
     * 注册
     * 
     * @param username    用户名
     * @param password    原密码（不是MD5）
     * @param phoneNumber 手机号，需要验证码
     * @return 注册的用户，或者null
     */
    public User register(String username, String password, String phoneNumber) {
        if (username == null || password == null || phoneNumber == null) {
            throw new RuntimeException("数据不能为空");
        }
        username = username.trim();
        password = password.trim();
        phoneNumber = phoneNumber.trim();
        
        // 判断能否注册
        if (userRepository.findByUsername(username) != null) {
            throw new RuntimeException("用户名已存在");
        }
        if (userRepository.findByPhoneNumber(phoneNumber) != null) {
            throw new RuntimeException("该手机号已注册");
        }

        // 密码hash
        String passwordHash = bcryptPasswordEncoder().encode(password);

        // 真正注册的代码
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(passwordHash);
        user.setPhoneNumber(phoneNumber);
        user.setCreateTime(new Date());

        user = userRepository.save(user); // 在这里创建了ID以及其他默认值

        return user;
    }

    /**
     * 基础登录逻辑
     * 
     * @param username // 不只是名字，可以是手机号、邮箱等等
     * @param password // 原密码，使用 matches 才能进行匹配，不能直接比较
     * @return 登录的用户，或者null
     */
    public User login(String username, String password) {
        // 找到要登录的用户
        User user = userRepository.findByUsernameOrPhoneNumberOrMailAddress(username, username, username);
        if (user == null) {
            return null;
        }
        // 判断密码是否正确
        if (bcryptPasswordEncoder().matches(password, user.getPasswordHash())) {
            return user;
        }
        return null;
    }

    /**
     * 密码加密工具类
     */
    @Bean
    public BCryptPasswordEncoder bcryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    public User getUserByUserId(String userId) {
        return userRepository.findByUserId(userId);
    }
}
