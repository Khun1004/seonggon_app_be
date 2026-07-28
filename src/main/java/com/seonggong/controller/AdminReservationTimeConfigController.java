package com.seonggong.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seonggong.dto.UpsertReservationTimeConfigRequest;
import com.seonggong.service.AdminAuthService;
import com.seonggong.service.ReservationTimeConfigService;

import lombok.RequiredArgsConstructor;

// 사장님 전용 — 예약(매장 식사)/포장 각각의 시작~종료 시간과 간격을 설정합니다.
@RestController
@RequestMapping("/api/admin/reservation-times")
@RequiredArgsConstructor
public class AdminReservationTimeConfigController {

    private final AdminAuthService adminAuthService;
    private final ReservationTimeConfigService service;

    @GetMapping("/{type}")
    public ResponseEntity<?> getConfig(
            @PathVariable("type") String type,
            @RequestHeader("X-Admin-Password") String adminPassword) {
        try {
            adminAuthService.requireAdmin(adminPassword);
            return ResponseEntity.ok(service.getConfig(type));
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{type}")
    public ResponseEntity<?> updateConfig(
            @PathVariable("type") String type,
            @RequestHeader("X-Admin-Password") String adminPassword,
            @RequestBody UpsertReservationTimeConfigRequest request) {
        try {
            adminAuthService.requireAdmin(adminPassword);
            return ResponseEntity.ok(service.updateConfig(type, request));
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
        }
    }
}