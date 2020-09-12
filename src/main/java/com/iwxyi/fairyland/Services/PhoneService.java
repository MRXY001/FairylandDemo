package com.iwxyi.fairyland.Services;

import java.util.Date;

import com.iwxyi.fairyland.Config.ConstantValue;
import com.iwxyi.fairyland.Exception.FormatedException;
import com.iwxyi.fairyland.Models.PhoneValidation;
import com.iwxyi.fairyland.Repositories.PhoneRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PhoneService {
    @Autowired
    PhoneRepository phoneRepository;

    /**
     * 发送验证码
     * 
     * @param number 手机号（不包含区号吧）
     */
    public void sendCaptcha(String number) {
        int num = (int) (Math.random() * 9000 + 1000); // 生成随机数
        String captcha = String.valueOf(num);
        Date time = new Date();
        PhoneValidation validation = new PhoneValidation(number, captcha, time);
        phoneRepository.save(validation);

        // 调用API发送验证码
    }

    /**
     * 验证手机验证码
     * 
     * @param number  手机号
     * @param captcha 验证码
     * @return 是否成功
     */
    public void validateCaptcha(String number, String captcha) {
        PhoneValidation validation = phoneRepository.findFirstByNumberOrderByCreateTimeDesc(number);
        if (validation == null) { // 未检测到这个号码
            throw new FormatedException("未发送" + number + "的验证码");
        }
        if (validation.isVerified()) { // 已经使用过的验证码
            throw new FormatedException("请重新发送验证码");
        }
        if (validation.getFailCount() > 3) { // 多次验证失败
            throw new FormatedException("验证次数过多，请稍后重试", 250);
        }
        if (validation.getCreateTime().getTime() + ConstantValue.PHONE_VALIDATION_VALID > (new Date()).getTime()) {
            throw new FormatedException("验证码已失效，请重新发送");
        }
        
        // 进行验证，判断是否正确
        if (validation.getCaptcha().equals(captcha)) {
            // 验证成功
            validation.setVerified(); // 设置为已经使用
            phoneRepository.save(validation);
            return ;
        }
        // 验证失败，失败次数+1
        validation.setFailCount(validation.getFailCount() + 1);
        phoneRepository.save(validation);
        throw new FormatedException("请输入正确的验证码");
    }
}
