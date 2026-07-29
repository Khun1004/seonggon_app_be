package com.seonggong.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpsertVisitStampSettingsRequest {
    private int requiredVisits;
    private String rewardName;
}