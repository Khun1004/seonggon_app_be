package com.seonggong.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.seonggong.dto.CreateReservationRequest;
import com.seonggong.dto.PayReservationRequest;
import com.seonggong.dto.ReservationResponse;
import com.seonggong.dto.TakenSlotResponse;
import com.seonggong.service.ReservationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    // 예약 화면에서 날짜를 고르면, 이미 찬 (자리, 시간) 조합을 받아와 버튼을 회색 처리합니다.
    @GetMapping("/availability")
    public ResponseEntity<List<TakenSlotResponse>> getAvailability(
            @RequestParam("date") String date) {
        return ResponseEntity.ok(reservationService.getAvailability(LocalDate.parse(date)));
    }

    // 마이페이지 / 예약 확인 화면 — 전화번호로 내 예약 목록 조회
    @GetMapping("/mine")
    public ResponseEntity<List<ReservationResponse>> getMyReservations(
            @RequestParam("phone") String phone) {
        return ResponseEntity.ok(reservationService.getMyReservations(phone));
    }

    @PostMapping
    public ResponseEntity<?> createReservation(@RequestBody CreateReservationRequest request) {
        try {
            return ResponseEntity.ok(reservationService.createReservation(request));
        } catch (IllegalStateException e) {
            // 자리/시간이 이미 찬 경우 — 409 Conflict
            return ResponseEntity.status(409).body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateReservation(
            @PathVariable("id") Long id, @RequestBody CreateReservationRequest request) {
        try {
            return ResponseEntity.ok(reservationService.updateReservation(id, request));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        }
    }

    // 모의 결제 — 실제로 돈이 빠지지 않고, "결제 완료" 상태만 기록합니다.
    @PostMapping("/{id}/pay")
    public ResponseEntity<?> pay(
            @PathVariable("id") Long id, @RequestBody PayReservationRequest request) {
        try {
            return ResponseEntity.ok(reservationService.pay(id, request));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelReservation(@PathVariable("id") Long id) {
        try {
            reservationService.cancelReservation(id);
            return ResponseEntity.ok(Map.of("message", "예약이 취소되었습니다."));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        }
    }
}