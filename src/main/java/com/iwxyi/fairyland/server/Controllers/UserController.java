package com.iwxyi.fairyland.server.Controllers;

import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;

import com.iwxyi.fairyland.server.Authentication.LoginRequired;
import com.iwxyi.fairyland.server.Authentication.LoginUser;
import com.iwxyi.fairyland.server.Config.ErrorCode;
import com.iwxyi.fairyland.server.Exception.FormatedException;
import com.iwxyi.fairyland.server.Exception.GlobalResponse;
import com.iwxyi.fairyland.server.Models.Coupon;
import com.iwxyi.fairyland.server.Models.User;
import com.iwxyi.fairyland.server.Services.LoginService;
import com.iwxyi.fairyland.server.Services.MailService;
import com.iwxyi.fairyland.server.Services.PhoneService;
import com.iwxyi.fairyland.server.Services.UserService;
import com.iwxyi.fairyland.server.Services.VipPaymentService;
import com.iwxyi.fairyland.server.Tools.IpUtil;
import com.iwxyi.fairyland.server.Tools.TokenUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/server/user", produces = "application/json;charset=UTF-8")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*") // 解决跨域问题
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    PhoneService phoneService;
    @Autowired
    private MailService mailService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private VipPaymentService vipPaymentService;
    @Autowired
    private HttpServletRequest request;

    Logger logger = LoggerFactory.getLogger(UserController.class);

    /* -------------------------------------------------------------------------- */
    /*                                     账号                                     */
    /* -------------------------------------------------------------------------- */

    /**
     * 用户注册
     * @param captcha 验证码，需要先传入手机发送，带着验证码来注册
     */
    @PostMapping(value = "/register")
    public GlobalResponse<?> register(@RequestParam("username") String username,
            @RequestParam("password") String password, @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("captcha") String captcha, @RequestParam(value = "cpuId", required = false) String cpuId) {
        // 判断手机验证码
        phoneService.validateCaptcha(phoneNumber, captcha);
        // 尝试注册
        User user = userService.register(username, password, phoneNumber);
        // 记入登录历史
        loginService.saveLogin(user.getUserId(), username, cpuId, "初次注册");
        // 注册成功，创建token
        String token = TokenUtil.createTokenByUser(user);
        // 返回新账号后的一些信息
        return GlobalResponse.map("token", token, "user", user);
    }

    /**
     * 用户登录
     * @param username 这个不只是用户名，也可以是手机号、邮箱等
     * @param password 原密码，不是加密后的
     */
    @PostMapping(value = "/login")
    public GlobalResponse<?> login(@RequestParam("username") @NotBlank String username,
            @RequestParam("password") String password, @RequestParam(value = "cpuId", required = false) String cpuId) {
        // 判断能否登录
        User user = userService.login(username, password);
        // 记入登录历史
        loginService.saveLogin(user.getUserId(), username, cpuId, "登录");
        // 登录成功，创建token （如果之前就带有token, 会把原来的token覆盖掉）
        String token = TokenUtil.createTokenByUser(user);
        // 返回登录后的一些信息
        return GlobalResponse.map("token", token, "user", user);
    }

    /**
     * 发送手机验证（免登录）
     */
    @RequestMapping("/sendPhoneValidation")
    public GlobalResponse<?> sendPhoneValidation(@RequestParam("phoneNumber") String phoneNumber,
            @RequestParam(value = "cpuId", required = false) String cpuId,
            @RequestParam(value = "message", required = false) String message) {
        phoneService.sendCaptcha(phoneNumber, IpUtil.getIpAddr(request), cpuId, message);
        return GlobalResponse.success();
    }

    /**
     * 忘记密码，发送手机号验证
     * 需要用户名与手机号匹配
     */
    @RequestMapping("/sendUserPhoneValidation")
    public GlobalResponse<?> sendUserPhoneValidation(@RequestParam("username") String username, @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam(value = "cpuId", required = false) String cpuId,
            @RequestParam(value = "message", required = false) String message) {
        userService.validUserPhoneNumber(username, phoneNumber);
        phoneService.sendCaptcha(phoneNumber, IpUtil.getIpAddr(request), cpuId, message);
        return GlobalResponse.success();
    }

    /**
     * 发送邮箱验证
     */
    @RequestMapping("/sendMailValidation")
    public GlobalResponse<?> sendMailValidation(@RequestParam("mail") String email) {
        mailService.sendMailValidation(email);
        return GlobalResponse.success();
    }

    /**
     * 验证手机验证码，例如修改密码的时候。需要用户登录，不需要上传手机号
     */
    @RequestMapping("/validateUserPhoneCaptcha")
    @LoginRequired
    public GlobalResponse<?> validateUserPhoneCaptcha(@LoginUser User user, @RequestParam("captcha") String captcha) {
        phoneService.validateCaptcha(user.getPhoneNumber(), captcha);
        return GlobalResponse.success();
    }

    /**
     * 验证手机验证码，不需要用户登录
     */
    @RequestMapping("/validatePhoneCaptcha")
    public GlobalResponse<?> validatePhoneCaptcha(@RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("captcha") String captcha) {
        phoneService.validateCaptcha(phoneNumber, captcha);
        return GlobalResponse.success();
    }

    /**
     * 修改昵称
     */
    @RequestMapping("/modifyNickname")
    @LoginRequired
    public GlobalResponse<?> modifyNickname(@LoginUser User user, String nickname) {
        userService.modifyNickname(user, nickname);
        return GlobalResponse.success();
    }

    /**
     * 修改密码
     */
    @RequestMapping("/modifyPassword")
    @LoginRequired
    public GlobalResponse<?> modifyPassword(@LoginUser User user, @RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword) {
        userService.modifyPassword(user, oldPassword, newPassword);
        return GlobalResponse.success();
    }

    /**
     * 忘记密码，需要发送手机验证码
     */
    @RequestMapping("/forgetPassword")
    @LoginRequired
    public GlobalResponse<?> forgetPassword(@LoginUser User user, @RequestParam("password") String password,
            @RequestParam("captcha") String captcha) {
        phoneService.validateCaptcha(user.getPhoneNumber(), captcha);
        userService.setPassword(user, password);
        return GlobalResponse.success();
    }

    /**
     * 测试token能否使用
     * 1. 登录
     * 2. 获取生成的token
     * 3. 使用postMan等在header放入token=xxx
     */
    @RequestMapping("/testToken")
    public GlobalResponse<?> testToken() {
        return GlobalResponse.success("通过token验证");
    }

    /* -------------------------------------------------------------------------- */
    /*                                     积分                                     */
    /* -------------------------------------------------------------------------- */

    /**
     * 增加用户的一些小积分项
     * 不会超额太多
     */
    @RequestMapping("/increaseIntegral")
    @LoginRequired
    public GlobalResponse<?> uploadIntegral(@LoginUser User user, @RequestParam("words") int words,
            @RequestParam("times") int times, @RequestParam("useds") int useds, @RequestParam("bonus") int bonus,
            @RequestParam("speed") Integer speed) {
        user = userService.increaseIntegral(user, words, times, useds, bonus, speed);
        return GlobalResponse.success(user);
    }

    /**
     * 查看所有的排名
     */
    @RequestMapping("/rank")
    @ResponseBody
    public GlobalResponse<?> rank(@RequestParam(value = "pageNumber", required = false) Integer pageNumber) {
        if (pageNumber == null) {
            pageNumber = 1;
        }

        // 排序方式，这里以等级进行排序
        Page<User> users = userService.pagedRank(pageNumber - 1, 20, Sort.by(Sort.Direction.DESC, "level"));
        return GlobalResponse.success(users);
    }

    /* -------------------------------------------------------------------------- */
    /*                                     支付                                     */
    /* -------------------------------------------------------------------------- */

    /**
     * 查看优惠券信息
     */
    @RequestMapping("/couponInfo")
    @ResponseBody
    @LoginRequired
    public GlobalResponse<?> couponInfo(@RequestParam("couponCode") String couponCode) {
        Coupon coupon = vipPaymentService.getCoupon(couponCode);
        if (coupon == null) {
            throw new FormatedException("未找到优惠券", ErrorCode.NotExist);
        } else if (coupon.isValid()) {
            throw new FormatedException("优惠券已失效", ErrorCode.Overdue);
        } else if (coupon.isAllUsed()) {
            throw new FormatedException("优惠券已被使用", ErrorCode.Insufficient);
        }
        return GlobalResponse.success(coupon);
    }

    /**
     * 支付平台的回调
     * 具体参数根据其平台的API进行调整
     * @param user 支付的用户（允许为其他用户，即给别人付款）
     * @param paymentAmount 支付金额
     * @param originalAmount 不含优惠券与其它优惠的原价
     * @param couponCode 优惠券
     * @param days 购买的真实天数（包括免费送的）
     * @param paymentTime 支付时间（肯定比现在的早）
     * @return
     */
    @RequestMapping("/paypaypayCallback") // !这个API需要改得复杂一点，免得被攻击
    @ResponseBody
    @LoginRequired
    public GlobalResponse<?> payCallback(@LoginUser User user, @RequestParam("paymentAmount") double paymentAmount,
            @RequestParam("originalAmount") double originalAmount, @RequestParam("couponCode") String couponCode,
            @RequestParam("days") Integer days, @RequestParam("paymentTime") Timestamp paymentTime) {
        user = vipPaymentService.savePayment(user, paymentAmount, originalAmount, couponCode, days, paymentTime,
                new Timestamp(System.currentTimeMillis()));
        return GlobalResponse.map("vipDeadline", user.getVipDeadline());
    }
}
