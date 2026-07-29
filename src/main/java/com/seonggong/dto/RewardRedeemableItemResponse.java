package com.seonggong.dto;

import com.seonggong.entity.RewardRedeemableItem;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RewardRedeemableItemResponse {
    private Long id;
    private String name;
    private int price;
    private String imageUrl;
    private int displayOrder;
    private boolean active;

    public static RewardRedeemableItemResponse from(RewardRedeemableItem i) {
        return new RewardRedeemableItemResponse(
                i.getId(), i.getName(), i.getPrice(), i.getImageUrl(),
                i.getDisplayOrder(), i.isActive());
    }
}