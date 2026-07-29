package com.seonggong.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpsertReviewGuideStepRequest {
    private String title;
    private String description;
    private String imageUrl;
    private int displayOrder;
    private boolean active;
}