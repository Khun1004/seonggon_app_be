package com.seonggong.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

// 사장님이 "이 날은 쉬어요"라고 콕 집어서 등록한 날짜 하나를 나타냅니다.
// 이 목록에 오늘 날짜가 있으면, 손님 화면에서 시간과 상관없이 "휴무"로 보여요.
@Entity
@Table(name = "closed_dates")
@Getter
@Setter
public class ClosedDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private LocalDate date;

    // 예: "명절 휴무", "개인 사정으로 휴무" — 없어도 됩니다.
    @Column(length = 100)
    private String reason;
}