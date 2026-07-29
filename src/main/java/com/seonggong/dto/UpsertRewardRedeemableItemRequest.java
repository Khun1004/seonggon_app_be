package com.seonggong.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpsertRewardRedeemableItemRequest {
    private String name;
    private int price;
    private String imageUrl;
    private int displayOrder;
    private boolean active;
}