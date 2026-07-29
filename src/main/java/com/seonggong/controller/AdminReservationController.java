package com.seonggong.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seonggong.dto.CreateReservationRequest;
import com.seonggong.dto.PayReservationRequest;
import com.seonggong.service.AdminAuthService;
import com.seonggong.service.ReservationService;

import lombok.RequiredArgsConstructor;

// 사장님 전용 — 손님 전화번호와 상관없이 전체 예약을 보고 관리합니다.
// 모든 메서드는 X-Admin-Password 헤더를 확인한 다음에만 동작해요.
@RestController
@RequestMapping("/api/admin/reservations")
@RequiredArgsConstructor
public class AdminReservationController {

    private final AdminAuthService adminAuthService;
    private final ReservationService reservationService;

    @GetMapping
    public ResponseEntity<?> getAllReservations(
            @RequestHeader("X-Admin-Password") String adminPassword) {
        try {
            adminAuthService.requireAdmin(adminPassword);
            return ResponseEntity.ok(reservationService.getAllReservationsForAdmin());
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
        }
    }

    // 손님이 앱 없이 전화로 부탁하거나 그냥 방문하는 경우 등, 사장님이 직접
    // 예약을 등록할 때 씁니다. 손님이 앱에서 예약할 때와 완전히 똑같은 로직
    // (이중예약 확인, 휴무일 확인)을 그대로 타기 때문에, 이렇게 등록한 자리·
    // 시간은 손님 화면의 예약 가능 여부 확인에서도 자동으로 "이미 찬 자리"로
    // 나타나요.
    @PostMapping
    public ResponseEntity<?> createReservation(
            @RequestHeader("X-Admin-Password") String adminPassword,
            @RequestBody CreateReservationRequest request) {
        try {
            adminAuthService.requireAdmin(adminPassword);
            return ResponseEntity.ok(reservationService.createReservation(request));
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(400).body(Map.of("message", e.getMessage()));
        }
    }

    // 손님이 전화로 "시간(또는 자리, 인원)을 바꿔달라"고 요청하는 경우 등,
    // 이미 만들어진 예약을 사장님이 직접 수정할 때 씁니다. 손님이 앱에서
    // 자기 예약을 수정할 때와 똑같은 로직(이중예약 확인, 휴무일 확인)을 타요.
    @PutMapping("/{id}")
    public ResponseEntity<?> updateReservation(
            @PathVariable("id") Long id,
            @RequestHeader("X-Admin-Password") String adminPassword,
            @RequestBody CreateReservationRequest request) {
        try {
            adminAuthService.requireAdmin(adminPassword);
            return ResponseEntity.ok(reservationService.updateReservation(id, request));
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(400).body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelReservation(
            @PathVariable("id") Long id,
            @RequestHeader("X-Admin-Password") String adminPassword) {
        try {
            adminAuthService.requireAdmin(adminPassword);
            reservationService.cancelReservationAsAdmin(id);
            return ResponseEntity.ok(Map.of("message", "예약이 취소되었습니다."));
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        }
    }

    // 매장에서 손님이 현금/카드로 직접 결제한 경우 등, 앱을 거치지 않은 결제를
    // 사장님이 수동으로 "결제완료"로 기록할 때 씁니다. 실제 결제망을 타지 않고
    // 상태만 기록한다는 점은 손님용 모의 결제와 같아요.
    @PostMapping("/{id}/mark-paid")
    public ResponseEntity<?> markPaid(
            @PathVariable("id") Long id,
            @RequestHeader("X-Admin-Password") String adminPassword,
            @RequestBody PayReservationRequest request) {
        try {
            adminAuthService.requireAdmin(adminPassword);
            return ResponseEntity.ok(reservationService.pay(id, request));
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(400).body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/menu-popularity")
    public ResponseEntity<?> getMenuPopularity(
            @RequestHeader("X-Admin-Password") String adminPassword) {
        try {
            adminAuthService.requireAdmin(adminPassword);
            return ResponseEntity.ok(reservationService.getMenuPopularity());
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
        }
    }
}