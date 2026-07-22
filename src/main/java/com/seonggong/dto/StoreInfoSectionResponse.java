package com.seonggong.dto;

import com.seonggong.entity.StoreInfoSection;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StoreInfoSectionResponse {
    private Long id;
    private String group;
    private String title;
    private String content;
    private String icon;
    private String imageUrl;
    private int displayOrder;
    private boolean active;

    public static StoreInfoSectionResponse from(StoreInfoSection s) {
        return new StoreInfoSectionResponse(
                s.getId(),
                s.getGroup(),
                s.getTitle(),
                s.getContent(),
                s.getIcon(),
                s.getImageUrl(),
                s.getDisplayOrder(),
                s.isActive());
    }
}