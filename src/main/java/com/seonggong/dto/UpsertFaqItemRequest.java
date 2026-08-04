package com.seonggong.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpsertFaqItemRequest {
    private String category;
    private String categoryIcon;
    private String question;
    private String answer;
    private int displayOrder;
    private boolean active;
}