package com.seonggong.dto;

import com.seonggong.entity.ReviewGuideStep;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewGuideStepResponse {
    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private int displayOrder;
    private boolean active;

    public static ReviewGuideStepResponse from(ReviewGuideStep s) {
        return new ReviewGuideStepResponse(
                s.getId(), s.getTitle(), s.getDescription(), s.getImageUrl(),
                s.getDisplayOrder(), s.isActive());
    }
}