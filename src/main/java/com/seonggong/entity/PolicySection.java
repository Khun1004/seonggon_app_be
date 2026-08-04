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

// "이용약관"/"개인정보 처리방침" 화면의 조항들 — 사장님이 관리자 화면에서
// 직접 등록/수정/삭제합니다. policyType으로 두 문서를 구분해요.
@Entity
@Table(name = "policy_sections")
@Getter
@Setter
@NoArgsConstructor
public class PolicySection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // "TERMS"(이용약관) 또는 "PRIVACY"(개인정보 처리방침)
    @Column(nullable = false, length = 20)
    private String policyType;

    @Column(nullable = false, length = 100)
    private String heading;

    @Column(nullable = false, length = 2000)
    private String body;

    @Column(nullable = false)
    private int displayOrder = 0;

    @Column(nullable = false)
    private boolean active = true;
}