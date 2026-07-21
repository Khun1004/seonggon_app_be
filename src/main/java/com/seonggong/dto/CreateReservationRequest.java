package com.seonggong.dto;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateReservationRequest {
    private String roomId;
    private String roomLabel;
    private String type; // "DINE_IN" (기본) / "TAKEOUT"
    private String date; // "YYYY-MM-DD"
    private String time; // "18:30"
    private String name;
    private String loginId;
    private String phone;
    private int peopleCount;
    private String message;
    private boolean hasPet;
    private boolean wantsTakeout;
    private Map<String, Integer> menus;
    private Map<String, Integer> takeoutMenus;
}