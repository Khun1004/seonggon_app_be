package com.seonggong.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VisitRewardClaimResponse {
    private Long id;
    private String claimedAt; // ISO 문자열
    private List<String> visitDates; // 이 회차에 쓰인 방문 날짜들 (오래된 순)
}