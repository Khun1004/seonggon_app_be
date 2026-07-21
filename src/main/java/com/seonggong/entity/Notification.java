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

// 로그인한 회원에게 보여줄 알림 — 예약 완료/취소, 결제 완료, 회원가입 환영 등
// 실제 이벤트가 발생할 때마다 서버가 이 테이블에 한 줄씩 만들어 넣습니다.
@Entity
@Table(name = "notifications")
@Getter
@Setter
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 이 알림을 받을 회원의 로그인 아이디
    @Column(nullable = false)
    private String loginId;

    // RESERVATION_CREATED / RESERVATION_CANCELLED / PAYMENT_COMPLETED /
    // SIGNUP_WELCOME 등
    @Column(nullable = false, length = 40)
    private String type;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 300)
    private String message;

    // 눌렀을 때 이동할 화면 경로 (예: "/(tabs)/reservationcheck") — 없으면 이동 안 함
    @Column(length = 100)
    private String route;

    @Column(nullable = false)
    private boolean isRead = false;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}