package com.seonggong.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpsertAdminAccountRequest {
    private String phone;
    private String address;
    private String bankName;
    private String accountNumber;
    private String accountHolder;
    private String businessName;
    private String businessNumber;
    private String representativeName;
    private String businessType;
    private String businessCategory;
}