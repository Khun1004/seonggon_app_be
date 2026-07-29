package com.seonggong.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VisitStampSettingsResponse {
    private int requiredVisits;
    private String rewardName;
}