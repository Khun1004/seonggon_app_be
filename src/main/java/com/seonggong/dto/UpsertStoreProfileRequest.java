package com.seonggong.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpsertStoreProfileRequest {
    private String address;
    private String phone;
    private String openTime;
    private String closeTime;
    private String lastOrderTime;
    private Double naverRating;
    private Integer naverReviewCount;
    private Integer blogReviewCount;
}