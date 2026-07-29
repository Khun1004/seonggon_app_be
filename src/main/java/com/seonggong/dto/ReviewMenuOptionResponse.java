package com.seonggong.dto;

import com.seonggong.entity.ReviewMenuOption;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewMenuOptionResponse {
    private Long id;
    private String name;
    private int displayOrder;
    private boolean active;

    public static ReviewMenuOptionResponse from(ReviewMenuOption o) {
        return new ReviewMenuOptionResponse(o.getId(), o.getName(), o.getDisplayOrder(), o.isActive());
    }
}