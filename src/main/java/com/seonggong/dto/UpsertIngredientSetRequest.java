package com.seonggong.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpsertIngredientSetRequest {
    private String name;
    private List<MenuIngredientDto> ingredients;
}