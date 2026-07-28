package com.seonggong.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.seonggong.entity.IngredientSet;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class IngredientSetResponse {
    private Long id;
    private String name;
    private List<MenuIngredientDto> ingredients;
    private int usedByCount; // 이 세트를 쓰고 있는 메뉴 개수 (관리자 화면 참고용)

    public static IngredientSetResponse from(IngredientSet s, int usedByCount) {
        List<MenuIngredientDto> ingredients = s.getIngredients().stream()
                .map(i -> new MenuIngredientDto(i.getName(), i.getImageUrl()))
                .collect(Collectors.toList());
        return new IngredientSetResponse(s.getId(), s.getName(), ingredients, usedByCount);
    }
}