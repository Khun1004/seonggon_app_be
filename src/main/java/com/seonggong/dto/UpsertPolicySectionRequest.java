package com.seonggong.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpsertPolicySectionRequest {
    private String policyType;
    private String heading;
    private String body;
    private int displayOrder;
    private boolean active;
}