package com.seonggong.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seonggong.dto.ReviewResponse;
import com.seonggong.service.AdminAuthService;
import com.seonggong.service.ReviewService;

import lombok.RequiredArgsConstructor;

// 사장님 전용 — 리뷰에 직접 답변을 남기거나 자동 생성된 답변을 바꿉니다.
@RestController
@RequestMapping("/api/admin/reviews")
@RequiredArgsConstructor
public class AdminReviewController {

    private final AdminAuthService adminAuthService;
    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<?> getAllReviews(
            @RequestHeader("X-Admin-Password") String adminPassword) {
        try {
            adminAuthService.requireAdmin(adminPassword);
            List<ReviewResponse> reviews = reviewService.getAllReviews();
            return ResponseEntity.ok(reviews);
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/reply")
    public ResponseEntity<?> setOwnerReply(
            @PathVariable("id") Long id,
            @RequestHeader("X-Admin-Password") String adminPassword,
            @RequestBody Map<String, String> body) {
        try {
            adminAuthService.requireAdmin(adminPassword);
            reviewService.setOwnerReply(id, body.get("reply"));
            return ResponseEntity.ok(Map.of("message", "답변이 저장되었습니다."));
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        }
    }
}