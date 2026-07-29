package com.seonggong.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 쿠폰 탭 아래에 있는 "꼭 읽어주세요!" 안내 문구 — 사장님이 자유롭게
// 수정할 수 있게, 줄 하나가 안내 항목 하나가 되는 텍스트로 저장해요.
// StoreProfile처럼 딱 한 줄(싱글턴, id=1)만 있어요.
@Entity
@Table(name = "coupon_notice")
@Getter
@Setter
@NoArgsConstructor
public class CouponNotice {

    @Id
    private Long id = 1L;

    @Column(nullable = false, length = 2000)
    private String content = "리뷰를 작성하시기 전, 꼭 직원에게 영수증 요청을 해주세요.\n"
            + "위 쿠폰 중 한 가지만 선택 가능합니다.\n"
            + "리뷰 작성을 완료한 후, 직원에게 화면을 보여주셔야 혜택을 받으실 수 있습니다.\n"
            + "사진이 포함된 포토리뷰일 때만 해당 쿠폰 혜택이 적용됩니다 (음료 제외).";
}