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

// 리뷰 적립금(1,500원)으로 교환할 수 있는 메뉴 목록 — 사장님이 이름/가격/
// 사진을 자유롭게 추가/수정/삭제할 수 있어요.
@Entity
@Table(name = "reward_redeemable_items")
@Getter
@Setter
@NoArgsConstructor
public class RewardRedeemableItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private int price;

    @Column(length = 500)
    private String imageUrl;

    @Column(nullable = false)
    private int displayOrder = 0;

    @Column(nullable = false)
    private boolean active = true;
}