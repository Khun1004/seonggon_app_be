package com.seonggong.dto;

import com.seonggong.entity.Notice;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NoticeResponse {
    private Long id;
    private String date;
    private String title;
    private String content;
    private boolean active;

    public static NoticeResponse from(Notice n) {
        return new NoticeResponse(
                n.getId(), n.getDate().toString(), n.getTitle(), n.getContent(), n.isActive());
    }
}