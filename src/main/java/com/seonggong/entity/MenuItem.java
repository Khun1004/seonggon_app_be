package com.seonggong.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "menu_items")
@Getter
@Setter
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String category;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false, length = 30)
    private String price;

    @Column(nullable = false)
    private int priceVal;

    @Column(nullable = false)
    private boolean isHot;

    @Column(length = 300)
    private String imageUrl;

    @Column(nullable = false)
    private int displayOrder = 0;

    // 삭제 대신 숨김 처리 — 리뷰/장바구니 등 다른 곳에서 이 메뉴 id를 참조하고
    // 있을 수 있어서, 진짜로 지우지 않고 목록에서만 안 보이게 합니다.
    @Column(nullable = false)
    private boolean active = true;

    // 메뉴 상세 화면의 "종류" 탭에 보여줄 재료 목록 (예: 백숙에 들어간 버섯 종류)
    // — "재료 세트"를 안 쓰고 이 메뉴만 개별로 재료를 등록했을 때 사용해요.
    @ElementCollection
    @CollectionTable(name = "menu_item_ingredients", joinColumns = @JoinColumn(name = "menu_item_id"))
    @OrderColumn(name = "display_order")
    private List<MenuIngredient> ingredients = new ArrayList<>();

    // 재료 세트를 골라서 연결하면, 위 ingredients 대신 이 세트의 재료 목록을
    // 손님 화면에 보여줍니다. 같은 세트를 여러 메뉴가 함께 쓸 수 있고,
    // 세트 내용을 고치면 그 세트를 쓰는 메뉴 전체에 자동 반영돼요.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_set_id")
    private IngredientSet ingredientSet;
}