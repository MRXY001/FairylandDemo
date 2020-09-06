package com.iwxyi.fairyland.Services;

import com.iwxyi.fairyland.Models.User;
import com.iwxyi.fairyland.Repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired // 装配数据库操作类
    UserRepository userRepository;

    /**
     * 基础登录逻辑
     * 
     * @param username     // 不只是名字，可以是手机号、邮箱等等
     * @param passwordHash // 密码 MD5 hashed
     * @return 是否匹配成功
     */
    public boolean login(String username, String passwordHash) {
        Iterable<User> users = userRepository.findAll();
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPasswordHash().equals(passwordHash)) {
                return true;
            }
        }
        return false;
    }
}
