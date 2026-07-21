package com.seonggong.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seonggong.dto.NearbySpotResponse;
import com.seonggong.service.NearbySpotService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/nearby-spots")
@RequiredArgsConstructor
public class NearbySpotController {

    private final NearbySpotService nearbySpotService;

    @GetMapping
    public ResponseEntity<List<NearbySpotResponse>> getAll() {
        return ResponseEntity.ok(nearbySpotService.getAll());
    }
}