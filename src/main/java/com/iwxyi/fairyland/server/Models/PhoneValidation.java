package com.iwxyi.fairyland.server.Models;


import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class PhoneValidation {
    @Id // 主键
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long validationId;
    @Length(min = 11, max = 11, message = "请输入正确的手机号") // 验证格式要和User的一模一样
    @Pattern(regexp = "^(((13[0-9])|(14[579])|(15([0-3]|[5-9]))|(16[6])|(17[0135678])|(18[0-9])|(19[89]))\\d{8})$", message = "请输入正确的手机号")
    private String number;
    private String captcha;
    private String ip;
    private String cpuId;
    private Date createTime;
    private int failCount;
    private boolean verified;
    
    public PhoneValidation(String number, String captcha, Date time) {
        this.number = number;
        this.captcha = captcha;
        this.createTime = time;
    }
    
    public void setVerified() {
        verified = true;
    }
}
