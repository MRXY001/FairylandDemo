package com.iwxyi.fairyland.server.Models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class VipPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;
    @NotNull
    private Long userId; // 付费的用户
    @NotNull
    private double paymentAmount; // 付费总金额
    @NotNull
    private double originalAmount; // 原价
    @NotNull
    private int days; // 购买的天数
    private Long couponId; // 优惠券Id：每个用户每张优惠券只能使用一次
    private Long referralId; // 推荐人的用户Id：返利20%+10%+5%
    @NotNull
    private Date paymentTime; // 支付时间
    @NotNull
    private Date createTime; // 创建时间，应该比创建时间晚一点

    public VipPayment(Long userId, double paymentAmount, double originalAmount, int days, Long couponId, Date paymentTime,
            Date createTime) {
        this.userId = userId;
        this.paymentAmount = paymentAmount;
        this.originalAmount = originalAmount;
        this.days = days;
        this.couponId = couponId;
        this.paymentTime = paymentTime;
        this.createTime = createTime;
    }
}
