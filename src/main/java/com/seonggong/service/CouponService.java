package com.seonggong.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seonggong.dto.CouponResponse;
import com.seonggong.dto.UpsertCouponRequest;
import com.seonggong.entity.Coupon;
import com.seonggong.repository.CouponRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    // 손님용 — 켜져있는(active) 쿠폰만, 정해둔 순서대로
    public List<CouponResponse> getActiveCoupons() {
        return couponRepository.findAllByActiveTrueOrderByDisplayOrderAsc().stream()
                .map(CouponResponse::from)
                .toList();
    }

    // 관리자용 — 꺼둔 것까지 전부
    public List<CouponResponse> getAllCoupons() {
        return couponRepository.findAllByOrderByDisplayOrderAsc().stream()
                .map(CouponResponse::from)
                .toList();
    }

    @Transactional
    public CouponResponse createCoupon(UpsertCouponRequest request) {
        Coupon coupon = new Coupon();
        applyRequest(coupon, request);
        return CouponResponse.from(couponRepository.save(coupon));
    }

    @Transactional
    public CouponResponse updateCoupon(Long id, UpsertCouponRequest request) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));
        applyRequest(coupon, request);
        return CouponResponse.from(coupon);
    }

    @Transactional
    public void deleteCoupon(Long id) {
        if (!couponRepository.existsById(id)) {
            throw new IllegalArgumentException("쿠폰을 찾을 수 없습니다.");
        }
        couponRepository.deleteById(id);
    }

    private void applyRequest(Coupon coupon, UpsertCouponRequest request) {
        coupon.setTitle(request.getTitle());
        coupon.setSubtitle(request.getSubtitle());
        coupon.setIcon(request.getIcon() == null || request.getIcon().isBlank()
                ? "pricetag-outline"
                : request.getIcon());
        coupon.setCount(request.getCount());
        coupon.setDisplayOrder(request.getDisplayOrder());
        coupon.setActive(request.isActive());
    }
}