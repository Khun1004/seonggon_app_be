package com.seonggong.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpsertMenuItemRequest {
    private String category;
    private String name;
    private String description;
    private String price;
    private int priceVal;

    // 응답 DTO와 같은 이유로, 요청을 받을 때도 JSON 키를 "isHot"으로 고정합니다.
    @JsonProperty("isHot")
    private boolean isHot;

    private String imageUrl; // 업로드된 사진의 서버 경로 — 안 바꾸면 기존 값 그대로 보냅니다.
    private int displayOrder;
    private boolean active;

    // 재료 세트를 골랐으면 그 id를 보내주세요. 세트를 쓰면 아래 ingredients는
    // 무시되고, 세트를 안 쓰면(null) ingredients를 이 메뉴 전용으로 저장해요.
    private Long ingredientSetId;
    private List<MenuIngredientDto> ingredients; // null이면 재료 목록은 그대로 둡니다.
}