package com.iwxyi.fairyland.Tools;

import javax.annotation.PostConstruct;

import com.iwxyi.fairyland.Models.User;
import com.iwxyi.fairyland.Repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserUtil {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private static UserRepository staticUserRepository;

    /**
     * 初始化静态变量。只找到这么调用静态autowire的方式；不这样做的话repository都是空的
     */
    @PostConstruct
    public void init() {
        staticUserRepository = userRepository;
    }

    /**
     * *静态变量需要使用 @PostConstruct 的方法来初始化
     */
    public static User getUserByUserId(Long userId) {
        return staticUserRepository.findByUserId(userId);
    }
}
