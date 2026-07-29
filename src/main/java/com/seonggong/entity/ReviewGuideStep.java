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

// "리뷰 작성법" 화면에 나오는 단계별 안내 — 사장님이 문구를 직접 고칠 수 있어요.
@Entity
@Table(name = "review_guide_steps")
@Getter
@Setter
@NoArgsConstructor
public class ReviewGuideStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(length = 500)
    private String imageUrl;

    @Column(nullable = false)
    private int displayOrder = 0;

    @Column(nullable = false)
    private boolean active = true;
}