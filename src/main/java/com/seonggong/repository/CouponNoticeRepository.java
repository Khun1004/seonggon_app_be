package com.seonggong.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.seonggong.entity.CouponNotice;

public interface CouponNoticeRepository extends JpaRepository<CouponNotice, Long> {
}