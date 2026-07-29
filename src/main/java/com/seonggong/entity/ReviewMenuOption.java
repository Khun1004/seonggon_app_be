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

// 리뷰 작성 화면의 "어떤 메뉴를 드셨나요?" 목록 — 실제 메뉴판(Menu)과는
// 별도로, 리뷰에서 고를 수 있는 메뉴 이름만 관리자가 자유롭게 관리해요.
@Entity
@Table(name = "review_menu_options")
@Getter
@Setter
@NoArgsConstructor
public class ReviewMenuOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private int displayOrder = 0;

    @Column(nullable = false)
    private boolean active = true;
}