package com.iwxyi.fairyland.server.Services;

import java.sql.Timestamp;
import java.util.Date;

import com.iwxyi.fairyland.server.Config.ConstantValue;
import com.iwxyi.fairyland.server.Config.ErrorCode;
import com.iwxyi.fairyland.server.Exception.FormatedException;
import com.iwxyi.fairyland.server.Models.Coupon;
import com.iwxyi.fairyland.server.Models.Referee;
import com.iwxyi.fairyland.server.Models.User;
import com.iwxyi.fairyland.server.Models.VipPayment;
import com.iwxyi.fairyland.server.Repositories.CouponRepository;
import com.iwxyi.fairyland.server.Repositories.RefereeRepository;
import com.iwxyi.fairyland.server.Repositories.UserRepository;
import com.iwxyi.fairyland.server.Repositories.VipPaymentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VipPaymentService {
    @Autowired
    VipPaymentRepository paymentRepository;
    @Autowired
    CouponRepository couponRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RefereeRepository refereeRepository;

    public Coupon getCoupon(String couponCode) {
        Coupon coupon = couponRepository.findFirstByCode(couponCode);
        return coupon;
    }

    public User savePayment(User user, double paymentAmount, double originalAmount, String couponCode, int days,
            Timestamp paymentTime, Timestamp createTime) {
        // #检验金额（是否被攻击）
        final int MAX_AMOUNT = 100000; // 啊啊啊啊啊啊我就不信会有人一口气付十万块！！！
        if (paymentAmount <= 0 || originalAmount <= 0 || paymentAmount > MAX_AMOUNT || originalAmount > MAX_AMOUNT) {
            throw new FormatedException("金额不正确", ErrorCode.Data);
        }

        // #检验优惠券（允许误差）
        Long couponId = null;
        if (couponCode != null) {
            Coupon coupon = couponRepository.findFirstByCode(couponCode);
            if (coupon == null) {
                throw new FormatedException("未找到优惠券，请联系开发者", ErrorCode.NotExist);
            }
            couponId = coupon.getCouponId();
            // 检验优惠券的有效性
            if (!coupon.isValid()) {
                throw new FormatedException("优惠券已失效", ErrorCode.NotExist);
            }
            if (coupon.getWithAmout() != null && coupon.getWithAmout() < originalAmount) {
                throw new FormatedException("原价 " + paymentAmount + " 未达到优惠券满减条件：" + coupon.getWithAmout(),
                        ErrorCode.Insufficient);
            }
            // 检验金额的正确性
            final int type = coupon.getType();
            if (type == 1) {
                // 折扣
                if (Math.abs(originalAmount * (1 - coupon.getDiscount()) - paymentAmount) > 1) {
                    throw new FormatedException(
                            "金额不正确：折后：" + originalAmount * (1 - coupon.getDiscount()) + "，支付：" + paymentAmount,
                            ErrorCode.Incorrect);
                }
            } else if (type == 2) {
                // 满减(可以少减)
                if (originalAmount - paymentAmount > coupon.getDiscount()) {
                    throw new FormatedException(
                            "满减金额不正确：" + originalAmount + " - " + coupon.getDiscount() + " > " + paymentAmount,
                            ErrorCode.Incorrect);
                }
            } else if (type == 10) {
                // 固定天数（没设置条件的话，付0元也行）
                if (originalAmount != paymentAmount) {
                    throw new FormatedException("支付金额不正确：" + originalAmount + ", " + paymentAmount,
                            ErrorCode.Incorrect);
                }
            } else if (type == 11) {
                // 赠送天数
                if (originalAmount != paymentAmount) {
                    throw new FormatedException("支付金额不正确：" + originalAmount + ", " + paymentAmount,
                            ErrorCode.Incorrect);
                }
            }
        } else if (paymentAmount != originalAmount) {
            throw new FormatedException("没有优惠券，支付金额不正确，请联系开发者", ErrorCode.Incorrect);
        }

        // #保存支付记录
        paymentRepository.save(new VipPayment(user.getUserId(), paymentAmount, originalAmount, days, couponId,
                paymentTime, createTime));

        // #保存用户信息
        user.setTotalPay(user.getTotalPay() + paymentAmount); // 支付总金额
        Date deadline = user.getVipDeadline();
        Date now = new Date();
        if (deadline == null || deadline.getTime() < now.getTime()) {
            deadline = now;
        }
        // 添加用户VIP时长
        long addin = days * 86400000;
        deadline = new Date(deadline.getTime() + addin);
        user.setVipDeadline(deadline);
        userRepository.save(user);

        // #用户返现（三级）
        Referee currUser = refereeRepository.findByUserId(user.getUserId());
        Referee referee = refereeRepository.findByUserId(currUser.getRefereeId());
        if (referee != null) {
            referee.setAmount(referee.getAmount() + paymentAmount * ConstantValue.PAYMENT_REFEREE_1);
            refereeRepository.save(referee);
        }
        
        return user;
    }
}
