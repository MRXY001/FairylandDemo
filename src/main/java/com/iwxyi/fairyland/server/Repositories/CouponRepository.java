package com.iwxyi.fairyland.server.Repositories;

import com.iwxyi.fairyland.server.Models.Coupon;

import org.springframework.data.repository.CrudRepository;

public interface CouponRepository extends CrudRepository<Coupon, Long> {
    Coupon findFirstByCouponId(Long couponId);
    Coupon findFirstByCode(String couponCode);
    
}
