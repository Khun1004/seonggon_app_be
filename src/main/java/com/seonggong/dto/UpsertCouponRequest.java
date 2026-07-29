package com.seonggong.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpsertCouponRequest {
    private String title;
    private String subtitle;
    private String icon;
    private String count;
    private int displayOrder;
    private boolean active;
}