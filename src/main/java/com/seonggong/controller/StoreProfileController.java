package com.seonggong.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seonggong.service.StoreProfileService;

import lombok.RequiredArgsConstructor;

// 손님용 — 주소/전화/영업시간, 휴무일, 리뷰 통계. 로그인 필요 없음.
@RestController
@RequestMapping("/api/store-profile")
@RequiredArgsConstructor
public class StoreProfileController {

    private final StoreProfileService storeProfileService;

    @GetMapping
    public ResponseEntity<?> getProfile() {
        return ResponseEntity.ok(storeProfileService.getProfile());
    }

    @GetMapping("/closed-dates")
    public ResponseEntity<?> getUpcomingClosedDates() {
        return ResponseEntity.ok(storeProfileService.getUpcomingClosedDates());
    }

    @GetMapping("/review-stats")
    public ResponseEntity<?> getReviewStats() {
        return ResponseEntity.ok(storeProfileService.getReviewStats());
    }
}