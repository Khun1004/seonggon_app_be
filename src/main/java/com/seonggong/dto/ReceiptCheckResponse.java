package com.seonggong.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptCheckResponse {

    // Jackson이 boolean 필드명을 "is" 접두사 기준으로 잘못 매핑하는 문제를 막기 위해
    // JSON 키 이름을 명시적으로 고정합니다.
    @JsonProperty("isReceipt")
    private boolean isReceipt;

    @JsonProperty("matchesStore")
    private boolean matchesStore;

    @JsonProperty("detectedStoreName")
    private String detectedStoreName;
}