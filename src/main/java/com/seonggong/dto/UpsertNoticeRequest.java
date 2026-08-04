package com.seonggong.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpsertNoticeRequest {
    private String date; // "yyyy-MM-dd" — 비어있으면 오늘 날짜로 저장돼요.
    private String title;
    private String content;
    private boolean active;
}