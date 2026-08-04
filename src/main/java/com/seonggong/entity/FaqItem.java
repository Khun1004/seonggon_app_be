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

// 손님 화면의 "자주 묻는 질문" 목록 — 사장님이 관리자 화면에서 직접
// 등록/수정/삭제합니다. category가 같은 항목들끼리 화면에서 묶여서 보여요.
@Entity
@Table(name = "faq_items")
@Getter
@Setter
@NoArgsConstructor
public class FaqItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String category; // 예: "예약", "영업시간", "주차", "결제", "단체석"

    // Ionicons 이름 (예: "calendar-outline")
    @Column(nullable = false, length = 50)
    private String categoryIcon = "help-circle-outline";

    @Column(nullable = false, length = 200)
    private String question;

    @Column(nullable = false, length = 1000)
    private String answer;

    @Column(nullable = false)
    private int displayOrder = 0;

    @Column(nullable = false)
    private boolean active = true;
}