package com.seonggong.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpsertReviewGoodPointOptionRequest {
    private String emoji;
    private String label;
    private int displayOrder;
    private boolean active;
}