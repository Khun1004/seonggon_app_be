package com.seonggong.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

// 메뉴 하나(백숙/고기/사이드 등)를 나타냅니다. 예전에는 앱 코드 안에 고정값으로
// 박혀 있던 데이터였는데, 사장님이 앱에서 직접 이름/가격/설명/사진을 바꾸실 수
// 있도록 DB로 옮겼습니다.
@Entity
@Table(name = "menu_items")
@Getter
@Setter
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // "백숙" / "고기" / "사이드" 등 — 프론트엔드 탭과 동일한 이름을 씁니다.
    @Column(nullable = false, length = 20)
    private String category;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    // 화면에 그대로 보여줄 문자열 (예: "69,000원")
    @Column(nullable = false, length = 30)
    private String price;

    // 장바구니/합계 계산에 쓰는 숫자 값 (예: 69000)
    @Column(nullable = false)
    private int priceVal;

    // "인기" 뱃지 표시 여부
    @Column(nullable = false)
    private boolean isHot = false;

    // 사장님이 사진을 새로 업로드하면 여기 서버 경로가 들어갑니다.
    // 아직 업로드 안 한 예전 메뉴는 null이고, 이 경우 앱이 기존 기본 사진을 보여줍니다.
    @Column(length = 300)
    private String imageUrl;

    // 같은 카테고리 안에서 보여줄 순서
    @Column(nullable = false)
    private int displayOrder = 0;

    // 삭제 대신 숨김 처리 — 리뷰/장바구니 등 다른 곳에서 이 메뉴 id를 참조하고
    // 있을 수 있어서, 진짜로 지우지 않고 목록에서만 안 보이게 합니다.
    @Column(nullable = false)
    private boolean active = true;
}