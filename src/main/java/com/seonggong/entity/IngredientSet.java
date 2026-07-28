package com.seonggong.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

// 여러 메뉴가 똑같은 재료 목록을 쓸 때(예: 백숙류가 다 같은 버섯 11종을 쓰는
// 경우), 재료를 메뉴마다 따로 등록하지 않고 "세트"로 한 번만 만들어서
// 여러 메뉴가 공유하게 해줍니다. 세트를 수정하면, 그 세트를 쓰는 모든
// 메뉴에 자동으로 반영돼요.
@Entity
@Table(name = "ingredient_sets")
@Getter
@Setter
public class IngredientSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 예: "백숙 기본 버섯 11종", "능이버섯 포함 세트"
    @Column(nullable = false, length = 100)
    private String name;

    @ElementCollection
    @CollectionTable(name = "ingredient_set_items", joinColumns = @JoinColumn(name = "ingredient_set_id"))
    @OrderColumn(name = "display_order")
    private List<MenuIngredient> ingredients = new ArrayList<>();
}