package com.seonggong.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenerateReviewRequest {
    private int rating;
    private List<String> goodPoints;
    private String menuName;
    private String taste;
    private String mood;
    private String service;
}