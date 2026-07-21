package com.seonggong.dto;

import com.seonggong.entity.NearbySpot;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NearbySpotResponse {
    private Long id;
    private String name;
    private String description;
    private String icon;
    private String imageUrl;

    public static NearbySpotResponse from(NearbySpot spot) {
        return new NearbySpotResponse(
                spot.getId(),
                spot.getName(),
                spot.getDescription(),
                spot.getIcon(),
                spot.getImageUrl());
    }
}