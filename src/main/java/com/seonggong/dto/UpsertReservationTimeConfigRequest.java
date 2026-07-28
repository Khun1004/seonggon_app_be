package com.seonggong.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpsertReservationTimeConfigRequest {
    private String startTime;
    private String endTime;
    private int intervalMinutes;
}