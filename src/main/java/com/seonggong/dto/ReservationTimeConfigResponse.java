package com.seonggong.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReservationTimeConfigResponse {
    private String type;
    private String startTime;
    private String endTime;
    private int intervalMinutes;
    private List<String> slots; // 실제로 화면에서 고를 수 있는 시간 목록 (계산된 값)
}