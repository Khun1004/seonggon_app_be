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

// 리뷰 작성 화면의 "어떤 점이 좋았나요?" 선택지 — 사장님이 직접 추가/삭제
// 할 수 있어요. 라벨(label)은 리뷰 저장 시 keywords에 그대로 문자열로
// 저장되기 때문에, 라벨을 수정하면 예전 리뷰의 통계와 안 맞을 수 있어요
// (새로 추가/비활성화는 안전해요).
@Entity
@Table(name = "review_good_point_options")
@Getter
@Setter
@NoArgsConstructor
public class ReviewGoodPointOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String emoji;

    @Column(nullable = false, length = 50)
    private String label;

    @Column(nullable = false)
    private int displayOrder = 0;

    @Column(nullable = false)
    private boolean active = true;
}