package com.seonggong.dto;

import java.util.List;
import java.util.stream.Collectors;

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
    private boolean isHot;
    private String imageUrl;
    private int displayOrder;
    private boolean active;
    private List<MenuIngredientDto> ingredients;

    public static MenuItemResponse from(MenuItem m) {
        List<MenuIngredientDto> ingredients = m.getIngredients() == null
                ? List.of()
                : m.getIngredients().stream()
                        .map(i -> new MenuIngredientDto(i.getName(), i.getImageUrl()))
                        .collect(Collectors.toList());
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
                ingredients);
    }
}