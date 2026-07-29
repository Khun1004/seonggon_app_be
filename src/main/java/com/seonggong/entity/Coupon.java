package com.seonggong.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 손님 화면의 "쿠폰" 탭에 보이는 쿠폰 목록 — 사장님이 관리자 화면에서
// 직접 추가/수정/삭제합니다.
@Entity
@Table(name = "coupons")
@Getter
@Setter
@NoArgsConstructor
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title; // 예: "도토리묵 증정"

    @Column(nullable = false, length = 100)
    private String subtitle; // 예: "네이버 포토리뷰 작성 시"

    // Ionicons 이름 (예: "restaurant-outline", "beer-outline")
    @Column(nullable = false, length = 50)
    private String icon = "pricetag-outline";

    @Column(length = 50)
    private String count; // 예: "총 3매 제공" — 없으면 null

    @Column(nullable = false)
    private int displayOrder = 0;

    @Column(nullable = false)
    private boolean active = true;
}