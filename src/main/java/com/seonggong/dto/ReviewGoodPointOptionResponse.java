package com.seonggong.dto;

import com.seonggong.entity.ReviewGoodPointOption;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewGoodPointOptionResponse {
    private Long id;
    private String emoji;
    private String label;
    private int displayOrder;
    private boolean active;

    public static ReviewGoodPointOptionResponse from(ReviewGoodPointOption o) {
        return new ReviewGoodPointOptionResponse(
                o.getId(), o.getEmoji(), o.getLabel(), o.getDisplayOrder(), o.isActive());
    }
}