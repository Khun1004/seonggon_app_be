package com.seonggong.dto;

import com.seonggong.entity.FaqItem;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FaqItemResponse {
    private Long id;
    private String category;
    private String categoryIcon;
    private String question;
    private String answer;
    private int displayOrder;
    private boolean active;

    public static FaqItemResponse from(FaqItem f) {
        return new FaqItemResponse(
                f.getId(), f.getCategory(), f.getCategoryIcon(), f.getQuestion(),
                f.getAnswer(), f.getDisplayOrder(), f.isActive());
    }
}