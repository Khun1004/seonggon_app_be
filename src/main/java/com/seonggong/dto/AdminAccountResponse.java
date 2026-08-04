package com.seonggong.dto;

import com.seonggong.entity.AdminAccount;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminAccountResponse {
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

    public static AdminAccountResponse from(AdminAccount a) {
        return new AdminAccountResponse(
                a.getPhone(), a.getAddress(), a.getBankName(),
                a.getAccountNumber(), a.getAccountHolder(),
                a.getBusinessName(), a.getBusinessNumber(),
                a.getRepresentativeName(), a.getBusinessType(),
                a.getBusinessCategory());
    }
}