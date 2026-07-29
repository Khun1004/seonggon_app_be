package com.seonggong.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.seonggong.entity.Coupon;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    List<Coupon> findAllByOrderByDisplayOrderAsc();

    List<Coupon> findAllByActiveTrueOrderByDisplayOrderAsc();
}