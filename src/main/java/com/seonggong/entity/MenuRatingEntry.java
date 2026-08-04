package com.seonggong.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 리뷰 하나에 "메뉴 이름 + 그 메뉴에 대한 별점" 쌍을 여러 개 담기 위한
// 작은 값 객체예요. Review.menuRatings 리스트의 항목 하나가 이거예요.
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MenuRatingEntry {

    @Column(name = "menu_name", length = 50)
    private String menuName;

    @Column(name = "menu_rating")
    private double rating;
}