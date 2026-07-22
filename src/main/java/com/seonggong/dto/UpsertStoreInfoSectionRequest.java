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
    private String imageUrl; // 업로드된 사진의 서버 경로 — 안 바꾸면 기존 값 그대로 보냅니다.
    private int displayOrder;
    private boolean active;
}