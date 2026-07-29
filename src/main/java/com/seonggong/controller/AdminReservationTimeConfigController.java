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
            return ResponseEntity.ok(service.getConfig(normalize(type)));
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
            return ResponseEntity.ok(service.updateConfig(normalize(type), request));
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
        }
    }

    // 손님용 컨트롤러(ReservationTimeConfigController)와 완전히 똑같은 방식으로
    // "dine-in"/"takeout"을 "DINE_IN"/"TAKEOUT"으로 맞춰줍니다. 이게 서로
    // 달랐던 게 바로 "관리자가 시간을 바꿔도 손님 화면엔 안 보이던" 원인이었어요 —
    // 두 화면이 서로 다른 이름으로 각각 따로 저장/조회하고 있었던 거예요.
    private String normalize(String type) {
        return "takeout".equalsIgnoreCase(type) ? "TAKEOUT" : "DINE_IN";
    }
}