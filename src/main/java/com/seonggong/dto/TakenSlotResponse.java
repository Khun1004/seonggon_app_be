package com.seonggong.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 특정 날짜에 이미 예약이 찬 (자리, 시간) 조합 하나
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TakenSlotResponse {
    private String roomId;
    private String time;
}