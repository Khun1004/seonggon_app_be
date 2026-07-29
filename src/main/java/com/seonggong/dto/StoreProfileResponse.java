package com.seonggong.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.seonggong.entity.StoreProfile;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StoreProfileResponse {
    private String address;
    private String phone;
    private String openTime;
    private String closeTime;
    private String lastOrderTime;
    private Double naverRating;
    private Integer naverReviewCount;
    private Integer blogReviewCount;

    @JsonProperty("allowWeekendReservations")
    private boolean allowWeekendReservations;

    public static StoreProfileResponse from(StoreProfile p) {
        return new StoreProfileResponse(
                p.getAddress(), p.getPhone(), p.getOpenTime(), p.getCloseTime(), p.getLastOrderTime(),
                p.getNaverRating(), p.getNaverReviewCount(), p.getBlogReviewCount(),
                p.isAllowWeekendReservations());
    }
}