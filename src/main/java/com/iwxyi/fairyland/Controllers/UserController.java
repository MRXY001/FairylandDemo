package com.iwxyi.fairyland.Controllers;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.iwxyi.fairyland.Config.ConstantKey;
import com.iwxyi.fairyland.Exception.FormatedException;
import com.iwxyi.fairyland.Exception.GlobalResponse;
import com.iwxyi.fairyland.Models.User;
import com.iwxyi.fairyland.Services.LoginService;
import com.iwxyi.fairyland.Services.MailService;
import com.iwxyi.fairyland.Services.PhoneValidationService;
import com.iwxyi.fairyland.Services.UserService;
import com.iwxyi.fairyland.Tools.IpUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RestController
@RequestMapping(value = "/user", produces = "application/json;charset=UTF-8")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*") // 解决跨域问题
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    PhoneValidationService phoneValidationService;
    @Autowired
    private MailService mailService;
    @Autowired
    private LoginService loginService;

    /**
     * 用户注册
     */
    @PostMapping(value = "/register")
    public GlobalResponse<?> register(@RequestParam("username") String username,
            @RequestParam("password") String password, @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("captcha") String captcha) {
        // 判断验证码
        boolean valid = phoneValidationService.validateCaptcha(phoneNumber, captcha);
        if (!valid) {
            throw new RuntimeException("验证码错误");
        }

        // 尝试注册
        User user = userService.register(username, password, phoneNumber);
        // 记入登录历史
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        String ip = IpUtil.getIpAddr(request);
        loginService.saveLogin(user.getUserId(), username, ip, "初次注册");
        // 注册成功，创建token
        String token = JWT.create().withAudience(user.getUserId() + "")// 将 user id 保存到 token 里面
                .withExpiresAt(new Date(System.currentTimeMillis() + 30 * 60 * 1000))// 定义token的有效期
                .sign(Algorithm.HMAC256(ConstantKey.USER_JWT_KEY));// 加密秘钥，也可以使用用户保持在数据库中的密码字符串
        return GlobalResponse.map("token", token);
    }

    /**
     * 用户登录
     * 
     * @param username 这个不只是用户名，也可以是手机号、邮箱等
     * @param password 原密码，不是加密后的
     */
    @PostMapping(value = "/login")
    public GlobalResponse<?> login(@RequestParam("username") String username,
            @RequestParam("password") String password) {
        // 判断能否登录
        User user = userService.login(username, password);
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 记入登录历史
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        String ip = IpUtil.getIpAddr(request);
        loginService.saveLogin(user.getUserId(), username, ip, "登录");

        // 登录成功，创建token
        // 如果之前就带有token, 会把原来的token覆盖掉
        String token = JWT.create().withAudience(user.getUserId() + "")// 将 user id 保存到 token 里面
                .withExpiresAt(new Date(System.currentTimeMillis() + 30 * 60 * 1000))// 定义token的有效期
                .sign(Algorithm.HMAC256(ConstantKey.USER_JWT_KEY));// 加密秘钥，也可以使用用户保持在数据库中的密码字符串
        return GlobalResponse.map("token", token);
    }

    /**
     * 发送手机验证
     */
    @RequestMapping("/sendPhoneValidation")
    public GlobalResponse<?> sendPhoneValidation(@RequestParam("phoneNumber") String phoneNumber) {
        phoneValidationService.sendCaptcha(phoneNumber);
        return GlobalResponse.success();
    }

    /**
     * 发送邮箱验证
     */
    @RequestMapping("/sendMailValidation")
    @ResponseBody
    public GlobalResponse<?> sendMailValidation(@RequestParam("mail") String email) {
        mailService.sendMailValidation(email);
        return GlobalResponse.success();
    }

    /**
     * 测试token能否使用 1. 登录（此时会报无token） 2. 获取生成的token 3. 使用postMan等在header放入token=xxx
     * 
     * @return 测试结果
     */
    @RequestMapping("/testToken")
    public GlobalResponse<?> testToken() {
        return GlobalResponse.success("通过token验证");
    }

    /**
     * 单纯的测试（大概也会有黑客从这里进入测试吧？）
     */
    @RequestMapping("/test")
    public void test() {
        if (true) {
            throw new FormatedException("恭喜您，成功找到了测试入口~", 5001);
        }
    }

}
