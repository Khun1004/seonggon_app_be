package com.seonggong.dto;

import com.seonggong.entity.ClosedDate;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ClosedDateResponse {
    private Long id;
    private String date;
    private String reason;

    public static ClosedDateResponse from(ClosedDate c) {
        return new ClosedDateResponse(c.getId(), c.getDate().toString(), c.getReason());
    }
}