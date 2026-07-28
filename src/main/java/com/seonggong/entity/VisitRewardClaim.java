package com.seonggong.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

// 손님이 "방문 도장 5개 채워서 혜택 받기"를 눌렀을 때 한 줄 남습니다.
// 이 기록 이후에 확정된 방문 예약만 "다음 회차 도장"으로 다시 세어져요 —
// 그래서 한 번 받은 혜택이 도장판에 계속 남아있지 않고 깨끗하게 새로 시작돼요.
@Entity
@Table(name = "visit_reward_claims")
@Getter
@Setter
public class VisitRewardClaim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String loginId;

    @Column(nullable = false)
    private LocalDateTime claimedAt = LocalDateTime.now();
}