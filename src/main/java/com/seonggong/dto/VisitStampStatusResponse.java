package com.seonggong.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VisitStampStatusResponse {
    private int visitCount; // 0 ~ totalStamps
    private int totalStamps;
    private boolean canClaim; // visitCount == totalStamps 이면 true
    private String lastClaimedAt; // ISO 문자열, 한 번도 안 받았으면 null
}