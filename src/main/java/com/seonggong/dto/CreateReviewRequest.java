package com.seonggong.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateReviewRequest {
    private String loginId;
    private String displayName;
    private double rating;
    private String text;
    private String menuName;
    private Long reservationId;
    private List<String> keywords;
    private List<String> photos;
    private boolean rewardEligible;
}