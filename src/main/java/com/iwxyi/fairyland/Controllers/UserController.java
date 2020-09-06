package com.iwxyi.fairyland.Controllers;

import java.util.Date;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.iwxyi.fairyland.Config.ConstantKey;
import com.iwxyi.fairyland.Models.User;
import com.iwxyi.fairyland.Services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/user")
public class UserController {
    @Autowired
    UserService userService;
    
    @PostMapping(value = "/register")
    public String register(@RequestParam("username") String username, 
            @RequestParam("password") String password,
            @RequestParam("phoneNumber") String phoneNumber) {
        User user = userService.register(username, password, phoneNumber);
        // 注册成功，创建token
        String token = JWT.create().withAudience(user.getUserId() + "")// 将 user id 保存到 token 里面
                .withExpiresAt(new Date(System.currentTimeMillis() + 30 * 60 * 1000))// 定义token的有效期
                .sign(Algorithm.HMAC256(ConstantKey.USER_JWT_KEY));// 加密秘钥，也可以使用用户保持在数据库中的密码字符串
        return token;
    }
    
    @PostMapping(value = "/login")
    public String login(@RequestParam("username") String username, @RequestParam("password") String password) {
        // 判断能否登录
        User user = userService.login(username, password);
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 登录成功，创建token
        // 如果之前就带有token, 会把原来的token覆盖掉
        String token = JWT.create().withAudience(user.getUserId() + "")// 将 user id 保存到 token 里面
                .withExpiresAt(new Date(System.currentTimeMillis() + 30 * 60 * 1000))// 定义token的有效期
                .sign(Algorithm.HMAC256(ConstantKey.USER_JWT_KEY));// 加密秘钥，也可以使用用户保持在数据库中的密码字符串
        return token;
    }
    
    /* @RequestMapping(value = "/login/{username}/{password}", method = RequestMethod.GET)
    public String loginG(@PathVariable("username") String username, @PathVariable("password") String password) */ 
    
    /**
     * 测试token能否使用
     * 1. 登录（此时会报无token）
     * 2. 获取生成的token
     * 3. 使用postMan等在header放入token=xxx
     * @return 测试结果
     */
    @RequestMapping("/testToken")
    public String testToken() {
        return "通过验证";
    }
}
