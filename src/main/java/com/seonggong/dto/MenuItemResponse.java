package com.seonggong.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.seonggong.entity.MenuItem;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MenuItemResponse {
    private Long id;
    private String category;
    private String name;
    private String description;
    private String price;
    private int priceVal;

    // "isHot" 처럼 이미 is로 시작하는 boolean 필드는, Lombok이 만드는 getter
    // 이름(isHot())을 Jackson이 JSON으로 바꿀 때 "is"를 떼어내서 "hot"으로
    // 내보내는 흔한 함정이 있어요. @JsonProperty로 JSON 키를 "isHot"으로
    // 못박아서, 프론트엔드가 기대하는 이름과 정확히 맞춰줍니다.
    @JsonProperty("isHot")
    private boolean isHot;

    private String imageUrl;
    private int displayOrder;
    private boolean active;

    // 손님 화면(MenuDetail.tsx "종류" 탭)이 보는 최종 재료 목록이에요.
    // 재료 세트를 쓰고 있으면 "세트 재료 + 이 메뉴만의 추가 재료"를 합친
    // 값이고, 세트를 안 쓰면 이 메뉴에 직접 등록된 재료 그대로예요.
    private List<MenuIngredientDto> ingredients;

    // 관리자 화면 전용 — 세트를 쓰는 중이면, "세트에 없는 이 메뉴만의 추가
    // 재료"만 따로 담아서 돌려줍니다. (세트 자체의 재료는 세트 관리 화면에서
    // 따로 조회해요.) 세트를 안 쓰는 메뉴는 ingredients와 같은 값이에요.
    private List<MenuIngredientDto> extraIngredients;

    // 관리자 화면에서 "지금 재료 세트를 쓰고 있는지" 표시하기 위한 정보예요.
    private Long ingredientSetId;
    private String ingredientSetName;

    public static MenuItemResponse from(MenuItem m) {
        List<MenuIngredientDto> extraIngredients = m.getIngredients() == null
                ? List.of()
                : m.getIngredients().stream()
                        .map(i -> new MenuIngredientDto(i.getName(), i.getImageUrl()))
                        .collect(Collectors.toList());

        List<MenuIngredientDto> ingredients;
        Long ingredientSetId = null;
        String ingredientSetName = null;

        if (m.getIngredientSet() != null) {
            List<MenuIngredientDto> setIngredients = m.getIngredientSet().getIngredients().stream()
                    .map(i -> new MenuIngredientDto(i.getName(), i.getImageUrl()))
                    .collect(Collectors.toList());
            ingredients = new ArrayList<>();
            ingredients.addAll(setIngredients);
            ingredients.addAll(extraIngredients);
            ingredientSetId = m.getIngredientSet().getId();
            ingredientSetName = m.getIngredientSet().getName();
        } else {
            ingredients = extraIngredients;
        }

        return new MenuItemResponse(
                m.getId(),
                m.getCategory(),
                m.getName(),
                m.getDescription(),
                m.getPrice(),
                m.getPriceVal(),
                m.isHot(),
                m.getImageUrl(),
                m.getDisplayOrder(),
                m.isActive(),
                ingredients,
                extraIngredients,
                ingredientSetId,
                ingredientSetName);
    }
}