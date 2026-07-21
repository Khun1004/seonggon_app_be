package com.seonggong.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 리뷰 적립금을 실제로 사용(도토리묵/해물파전/음료 등으로 교환)한 기록.
// 잔액 = (적립 대상 리뷰 개수 * 1,500원) - 이 테이블에 쌓인 사용 금액의 합
@Entity
@Table(name = "reward_redemptions")
@Getter
@Setter
@NoArgsConstructor
public class RewardRedemption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String loginId;

    @Column(nullable = false, length = 50)
    private String itemName;

    @Column(nullable = false)
    private int amount;

    @Column(nullable = false)
    private LocalDateTime redeemedAt = LocalDateTime.now();
}