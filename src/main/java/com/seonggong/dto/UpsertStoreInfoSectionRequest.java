package com.seonggong.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpsertStoreInfoSectionRequest {
    private String group;
    private String title;
    private String content;
    private String icon;
    private int displayOrder;
    private boolean active;
}