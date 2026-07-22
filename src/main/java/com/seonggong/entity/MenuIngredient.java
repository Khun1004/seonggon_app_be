package com.seonggong.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

// 메뉴 하나에 들어가는 재료(백숙류의 버섯 종류 등) 한 가지를 나타냅니다.
// 메뉴 상세 화면의 "종류" 탭에서 이 목록을 보여줘요.
@Embeddable
@Getter
@Setter
public class MenuIngredient {

    @Column(length = 50)
    private String name;

    // 사진은 선택사항 — 없으면 화면에서 기본 아이콘으로 보여줍니다.
    @Column(length = 300)
    private String imageUrl;
}