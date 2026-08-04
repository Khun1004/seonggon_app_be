package com.seonggong.dto;

import com.seonggong.entity.PolicySection;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PolicySectionResponse {
    private Long id;
    private String policyType;
    private String heading;
    private String body;
    private int displayOrder;
    private boolean active;

    public static PolicySectionResponse from(PolicySection p) {
        return new PolicySectionResponse(
                p.getId(), p.getPolicyType(), p.getHeading(), p.getBody(),
                p.getDisplayOrder(), p.isActive());
    }
}