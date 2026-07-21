package com.seonggong.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PhoneVerifyRequest {
    private String phone;
    private String code; // 사용자가 입력한 인증번호
}