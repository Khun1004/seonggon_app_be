package com.seonggong.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeAdminPasswordRequest {
    private String currentPassword;
    private String newPassword;
}