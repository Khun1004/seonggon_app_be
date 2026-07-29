package com.seonggong.dto;

import com.seonggong.entity.Coupon;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CouponResponse {
    private Long id;
    private String title;
    private String subtitle;
    private String icon;
    private String count;
    private int displayOrder;
    private boolean active;

    public static CouponResponse from(Coupon c) {
        return new CouponResponse(
                c.getId(), c.getTitle(), c.getSubtitle(), c.getIcon(),
                c.getCount(), c.getDisplayOrder(), c.isActive());
    }
}