package com.seonggong.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 방문 도장 설정 — 몇 번 방문하면 무엇을 주는지. StoreProfile처럼 딱
// 한 줄(싱글턴, id=1)만 있어요.
@Entity
@Table(name = "visit_stamp_settings")
@Getter
@Setter
@NoArgsConstructor
public class VisitStampSettings {

    @Id
    private Long id = 1L;

    @Column(nullable = false)
    private int requiredVisits = 5;

    @Column(nullable = false, length = 50)
    private String rewardName = "해물파전";
}