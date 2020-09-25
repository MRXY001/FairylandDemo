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
 * - 1  折扣：付费打折
 * - 2  满减：满多少减多少
 * - 10 天数：赠送固定天数
 * - 11 天数：按比例赠送天数
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
    private String title; // 券名
    private Long activityId; // 平台活动Id（如果有）

    @NotNull
    private Integer type; // 0未知，1折扣，2满减，10固定天数，11赠送天数
    private int quota = 1; // 总共发放数量
    private int takedCount = 0; // 已领取的数量（待使用）
    private int usedCount = 0; // 已经使用的数量
    
    @NotNull
    private Date startTime; // 发布时间
    private Date endTime; // 截止时间
    private boolean valid = true; // 有效状态

    private Double discount; // 折扣率（减少量，非剩余量）：null为没有折扣
    private Integer freeDays; // 赠送免费天数
    private Double withAmout; // 满多少生效

    /**
     * 是否还有配额
     * 检验优惠券是否还有未领取的
     * (已领取的有了固定数量，不在其中)
     */
    public boolean hasAllotCount() {
        return quota > takedCount;
    }
    
    /**
     * 是否已经全部使用了
     * 偏向单张优惠券的类型
     */
    public boolean isAllUsed() {
        return quota <= usedCount;
    }

}
