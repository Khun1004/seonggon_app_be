package com.seonggong.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PayReservationRequest {
    private String paymentMethod; // "카카오페이" / "네이버페이" / "신용카드" / "휴대폰 결제"
    private int amount;
}