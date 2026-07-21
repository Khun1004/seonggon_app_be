package com.seonggong.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RedeemRewardRequest {
    private String loginId;
    private String itemName;
    private int amount;
}