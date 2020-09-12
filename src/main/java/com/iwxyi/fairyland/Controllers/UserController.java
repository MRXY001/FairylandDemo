package com.iwxyi.fairyland.Controllers;

import javax.servlet.http.HttpServletRequest;

import com.iwxyi.fairyland.Exception.FormatedException;
import com.iwxyi.fairyland.Exception.GlobalResponse;
import com.iwxyi.fairyland.Interceptor.LoginUser;
import com.iwxyi.fairyland.Interceptor.LoginRequired;
import com.iwxyi.fairyland.Models.User;
import com.iwxyi.fairyland.Services.LoginService;
import com.iwxyi.fairyland.Services.MailService;
import com.iwxyi.fairyland.Services.PhoneService;
import com.iwxyi.fairyland.Services.UserService;
import com.iwxyi.fairyland.Tools.IpUtil;
import com.iwxyi.fairyland.Tools.TokenUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/user", produces = "application/json;charset=UTF-8")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*") // 解决跨域问题
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    PhoneService phoneValidationService;
    @Autowired
    private MailService mailService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private HttpServletRequest request;

    Logger logger = LoggerFactory.getLogger(UserController.class);

    /**
     * 用户注册
     * @param captcha 验证码，需要先传入手机发送，带着验证码来注册
     */
    @PostMapping(value = "/register")
    public GlobalResponse<?> register(@RequestParam("username") String username,
            @RequestParam("password") String password, @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("captcha") String captcha) {
        // 判断手机验证码
        phoneValidationService.validateCaptcha(phoneNumber, captcha);

        // 尝试注册
        User user = userService.register(username, password, phoneNumber);

        // 记入登录历史
        String ip = IpUtil.getIpAddr(request);
        loginService.saveLogin(user.getUserId(), username, ip, "初次注册");

        // 注册成功，创建token
        String token = TokenUtil.createTokenByUser(user);

        // 返回新账号后的一些信息
        return GlobalResponse.map("token", token, "user", user);
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

        // 记入登录历史
        String ip = IpUtil.getIpAddr(request);
        loginService.saveLogin(user.getUserId(), username, ip, "登录");

        // 登录成功，创建token
        // 如果之前就带有token, 会把原来的token覆盖掉
        String token = TokenUtil.createTokenByUser(user);

        // 返回登录后的一些信息
        return GlobalResponse.map("token", token, "user", user);
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
     * 验证手机验证码，例如修改密码的时候
     */
    @RequestMapping("/validPhoneCaptcha")
    @LoginRequired
    public GlobalResponse<?> validPhoneCaptcha(@LoginUser User user, @RequestParam("captcha") String captcha) {
        phoneValidationService.validateCaptcha(user.getPhoneNumber(), captcha);
        return GlobalResponse.success();
    }

    /**
     * 修改昵称
     */
    @RequestMapping("/modifyNickname")
    @ResponseBody
    @LoginRequired
    public GlobalResponse<?> modifyNickname(@LoginUser User user, String nickname) {
        userService.setNickname(user, nickname);
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
        throw new FormatedException("恭喜您，成功找到了测试入口~", 5001);
    }

}
