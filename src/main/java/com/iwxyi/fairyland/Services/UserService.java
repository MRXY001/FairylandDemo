package com.iwxyi.fairyland.Services;

import java.util.Date;

import com.iwxyi.fairyland.Config.ConstantValue;
import com.iwxyi.fairyland.Config.ErrorCode;
import com.iwxyi.fairyland.Exception.FormatedException;
import com.iwxyi.fairyland.Models.User;
import com.iwxyi.fairyland.Repositories.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired // 装配数据库操作类
    UserRepository userRepository;

    Logger logger = LoggerFactory.getLogger(UserService.class);

    /*****************************************************************************************
     *                                           账号
     *****************************************************************************************/

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
            throw new FormatedException("数据不能为空", ErrorCode.Arg);
        }
        username = username.trim();
        password = password.trim();
        phoneNumber = phoneNumber.trim();

        // 判断能否注册
        if (userRepository.findByUsername(username) != null) {
            throw new FormatedException("用户名已存在", ErrorCode.Exist);
        }
        if (userRepository.findByPhoneNumber(phoneNumber) != null) {
            throw new FormatedException("该手机号已注册", ErrorCode.Exist);
        }

        // 密码hash
        String passwordHash = bcryptPasswordEncoder().encode(password);

        // 真正注册的代码
        User user = new User(username, passwordHash, phoneNumber, new Date());

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
        User user = userRepository.findByUsernameOrPhoneNumberOrEmailAddress(username, username, username);
        if (user == null) {
            throw new FormatedException("用户未注册", ErrorCode.NotExist);
        }
        // 判断是否处在登录出错冻结状态
        if (user.getLoginForbidTime() != null) {
            Long delta = user.getLoginForbidTime().getTime() - (new Date()).getTime();
            if (delta > 0) {
                delta /= 1000; // 转换为秒
                String time;
                // 时间差转换为中文
                if (delta < 60) {
                    time = delta + "秒";
                } else if (delta < 60 * 60) {
                    time = delta / 60 + "分钟";
                } else if (delta < 60 * 60 * 30) {
                    time = delta / 60 / 60 + "小时";
                } else {
                    throw new FormatedException("太多次错误，建议重设密码", ErrorCode.Blocked);
                }
                throw new FormatedException("多次密码错误，已临时冻结，请在" + time + "后再试", ErrorCode.Blocked);
            }
        }
        
        // 判断密码是否正确
        if (bcryptPasswordEncoder().matches(password, user.getPasswordHash())) {
            // 允许登录
            if (user.getLoginFailedCount() > 0) {
                // 清空错误登录次数
                user.setLoginFailedCount(0);
                userRepository.save(user);
            }
            return user;
        }
        // 设置错误次数+1
        int failCount = user.getLoginFailedCount()+1;
        user.setLoginFailedCount(failCount);
        // 根据错误次数，设置不同的时间
        String msg = "账号或密码错误";
        long time = (new Date()).getTime();
        if (failCount <= 3) {
            // 不用管
            time = 0;
        } else if (failCount <= 4) {
            // 设置为1分钟
            time += 60 * 1000;
            msg = "密码错误，请1分钟后再试";
        } else if (failCount <= 5) {
            // 设置为3分钟
            time += 3 * 60 * 1000;
            msg = "密码错误，请3分钟后再试";
        } else if (failCount <= 6) {
            // 设置为5分钟
            time += 5 * 60 * 1000;
            msg = "密码错误，请5分钟后再试";
        } else if (failCount <= 7) {
            // 设置为10分钟
            time += 10 * 60 * 1000;
            msg = "密码错误，请10分钟后再试";
        } else if (failCount <= 8) {
            // 设置为30分钟
            time += 30 * 60 * 1000;
            msg = "密码错误，请30分钟后再试";
        } else if (failCount <= 9) {
            // 设置为1小时
            time += 60 * 60 * 1000;
            msg = "密码错误，请1小时后再试";
        } else if (failCount <= 10) {
            // 设置为3小时
            time += 3 * 60 * 60 * 1000;
            msg = "密码错误，请3小时后再试";
        } else if ( failCount <= 11) {
            // 设置为6小时
            time += 6 * 60 * 60 * 1000;
            msg = "密码错误，请6小时后再试";
        } else if ( failCount <= 12) {
            // 设置为12小时
            time += 12 * 60 * 60 * 1000;
            msg = "密码错误，请12小时后再试";
        } else {
            // 设置为24小时
            time += 24 * 60 * 60 * 1000;
            msg = "密码错误，请24小时后再试";
        }
        if (time > 0) {
            user.setLoginForbidTime(new Date(time));
        }
        userRepository.save(user);
        throw new FormatedException(msg, ErrorCode.Incorrect);
    }

    /**
     * 密码加密工具类
     */
    @Bean
    public BCryptPasswordEncoder bcryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public User getUserByUserId(Long userId) {
        return userRepository.findByUserId(userId);
    }

    public void modifyNickname(User user, String nickname) {
        Date prevTime = user.getNicknameModifyTime();
        Date currTime = new Date();
        if (prevTime != null && prevTime.getTime() + ConstantValue.NICKNAME_MODIFY_INTERVAL > currTime.getTime()) {
            throw new FormatedException("一周只能修改一次昵称", ErrorCode.FrequencyTime);
        }
        user.setNickname(nickname);
        user.setNicknameModifyTime(currTime);
        userRepository.save(user);
        return ;
    }
    
    public void modifyPassword(User user, String oldPassword, String newPassword) {
        // 判断旧密码是否正确
        if (!bcryptPasswordEncoder().matches(oldPassword, user.getPasswordHash())) {
            throw new FormatedException("旧密码错误，请重试", ErrorCode.Incorrect);
        }
        // 密码hash
        String passwordHash = bcryptPasswordEncoder().encode(newPassword);
        user.setPasswordHash(passwordHash);
        userRepository.save(user);
    }
    
    public void setPassword(User user, String password) {
        // 密码hash
        String passwordHash = bcryptPasswordEncoder().encode(password);
        user.setPasswordHash(passwordHash);
        userRepository.save(user);
    }
    
    /*****************************************************************************************
     *                                           积分
     *****************************************************************************************/
    
    public User increaseIntegral(User user, int words, int times, int useds, int bonus) {
        user.setAllWords(user.getAllWords() + words);
        user.setAllTimes(user.getAllTimes() + times);
        user.setAllUseds(user.getAllUseds() + useds);
        user.setAllBonus(user.getAllBonus() + bonus);
        userRepository.save(user);
        return user;
    }
    
    public Page<User> pagedRank(int page, int size, Sort sort) {
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<User> users = userRepository.findAll(pageable);
        return users;
    }
}
