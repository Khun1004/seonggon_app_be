package com.seonggong.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateReviewRequest {
    private String loginId;
    private String displayName;
    private String text;
    // 손님이 고른 메뉴들과 메뉴마다 매긴 별점 — 최소 1개는 있어야 해요.
    private List<MenuRatingItem> menuRatings;
    private Long reservationId;
    private List<String> keywords;
    private List<String> photos;
    private boolean rewardEligible;
}