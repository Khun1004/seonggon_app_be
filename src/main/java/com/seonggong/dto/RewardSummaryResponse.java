package com.seonggong.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RewardSummaryResponse {
    private int earned; // 지금까지 적립된 총액 (적립 대상 리뷰 개수 * 1,500원)
    private int spent; // 지금까지 사용한 총액
    private int balance; // 남은 잔액 = earned - spent
}