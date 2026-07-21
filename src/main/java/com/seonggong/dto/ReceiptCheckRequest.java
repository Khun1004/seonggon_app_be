package com.seonggong.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReceiptCheckRequest {
    private String imageBase64;
    private String mimeType;
}