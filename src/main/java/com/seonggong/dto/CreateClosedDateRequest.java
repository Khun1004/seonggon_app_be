package com.seonggong.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateClosedDateRequest {
    private String date;
    private String reason;
}