package com.iwxyi.fairyland.Services;

import java.util.Date;

import com.iwxyi.fairyland.Exception.FormatedException;
import com.iwxyi.fairyland.Models.PhoneValidation;
import com.iwxyi.fairyland.Repositories.PhoneValidationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PhoneValidationService {
    @Autowired
    PhoneValidationRepository phoneValidationRepository;

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
        phoneValidationRepository.save(validation);
        
        // 调用API发送验证码
    }

    /**
     * 验证手机验证码
     * 
     * @param number  手机号
     * @param captcha 验证码
     * @return 是否成功
     */
    public boolean validateCaptcha(String number, String captcha) {
        PhoneValidation validation = phoneValidationRepository.findByNumberAndCaptcha(number, captcha);
        if (validation != null) {
            if (validation.getVerfyCount() > 5) {
                throw new FormatedException("验证次数过多，请稍后重试", 250);
            }
            validation.setVerified(number); // 设置为已经使用
            return true;
        }
        return false;
    }
}
