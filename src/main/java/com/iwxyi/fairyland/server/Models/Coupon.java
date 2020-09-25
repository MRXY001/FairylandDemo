package com.iwxyi.fairyland.server.Models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通用优惠券
 * 通过发放优惠码，用户输入后支付，获得一定量的优惠
 * 优惠券类型：
 * - 折扣：付费打折
 * - 天数：赠送体验天数
 */

@Entity
@Data
@NoArgsConstructor
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long couponId; // 优惠券主键，也是VipPayment的外键
    @NotNull
    private String code; // 优惠券随机码
    private String name; // 券名
    private Long activityId; // 平台活动Id
    private int amount = 1; // 总共可用次数
    private int used = 0; // 已经使用的次数
    @NotNull
    private Date releaseTime; // 发布时间
    private Date endTime; // 截止时间
    private Double discount; // 折扣率（减少量，非剩余量）：null为没有折扣
    private Integer freeDays; // 赠送免费天数

}
