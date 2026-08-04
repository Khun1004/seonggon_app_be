package com.seonggong.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seonggong.service.ReservationService;

import lombok.RequiredArgsConstructor;

// 손님용 — 홈 화면의 "인기 메뉴 순위" 차트에 쓰여요. 결제까지 완료된
// 예약·포장 주문에서 실제로 고른 메뉴 수량만 집계합니다 (관리자용과 같은
// 로직이에요, 로그인/비밀번호 없이 볼 수 있게 공개 엔드포인트로 뺐어요).
@RestController
@RequestMapping("/api/menu-popularity")
@RequiredArgsConstructor
public class MenuPopularityController {

    private final ReservationService reservationService;

    @GetMapping
    public ResponseEntity<?> getPaidMenuPopularity() {
        return ResponseEntity.ok(reservationService.getPaidMenuPopularity());
    }
}