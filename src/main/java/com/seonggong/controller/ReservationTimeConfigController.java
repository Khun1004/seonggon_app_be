package com.seonggong.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seonggong.service.ReservationTimeConfigService;

import lombok.RequiredArgsConstructor;

// 손님용 — 예약 가능 시간 목록. type은 "dine-in" 또는 "takeout"이에요.
@RestController
@RequestMapping("/api/reservation-times")
@RequiredArgsConstructor
public class ReservationTimeConfigController {

    private final ReservationTimeConfigService service;

    @GetMapping("/{type}")
    public ResponseEntity<?> getConfig(@PathVariable("type") String type) {
        String normalized = normalize(type);
        return ResponseEntity.ok(service.getConfig(normalized));
    }

    private String normalize(String type) {
        return "takeout".equalsIgnoreCase(type) ? "TAKEOUT" : "DINE_IN";
    }
}