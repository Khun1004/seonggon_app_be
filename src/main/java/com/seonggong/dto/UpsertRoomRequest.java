package com.seonggong.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpsertRoomRequest {
    private String number;
    private int floor;
    private String category;
    private String categoryLabel;
    private String capacity;
    private boolean isRoom;
    private String note;
    private String imageUrl; // 업로드된 사진의 서버 경로 — 안 바꾸면 기존 값 그대로 보냅니다.
    private int displayOrder;
    private boolean active;
}