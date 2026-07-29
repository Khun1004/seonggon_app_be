package com.seonggong.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpsertReviewMenuOptionRequest {
    private String name;
    private int displayOrder;
    private boolean active;
}