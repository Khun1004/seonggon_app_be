package com.seonggong.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.seonggong.dto.CreateReviewRequest;
import com.seonggong.dto.ReviewResponse;
import com.seonggong.service.ReviewService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // 방문자 리뷰 탭 전체 목록
    @GetMapping
    public ResponseEntity<List<ReviewResponse>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    // 마이페이지 "나의 작성 리뷰" 목록
    @GetMapping("/me")
    public ResponseEntity<List<ReviewResponse>> getMyReviews(@RequestParam("loginId") String loginId) {
        return ResponseEntity.ok(reviewService.getMyReviews(loginId));
    }

    // 예약 시 입력한 전화번호로 가입된 회원이 리뷰를 하나라도 썼는지 확인
    @GetMapping("/has-reviewed")
    public ResponseEntity<Map<String, Boolean>> hasReviewed(@RequestParam("phone") String phone) {
        return ResponseEntity.ok(Map.of("hasReviewed", reviewService.hasReviewedByPhone(phone)));
    }

    @PostMapping
    public ResponseEntity<?> createReview(@RequestBody CreateReviewRequest request) {
        try {
            ReviewResponse response = reviewService.createReview(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // 리뷰에 첨부할 사진 업로드 — 서버 파일로 저장하고 그 경로를 돌려줍니다.
    // body: { "imageBase64": "..." }
    @PostMapping("/upload-photo")
    public ResponseEntity<?> uploadPhoto(@RequestBody Map<String, String> body) {
        try {
            String url = reviewService.uploadPhoto(body.get("imageBase64"));
            return ResponseEntity.ok(Map.of("url", url));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(
            @PathVariable("id") Long id,
            @RequestParam("loginId") String loginId) {
        try {
            reviewService.deleteReview(id, loginId);
            return ResponseEntity.ok(Map.of("message", "리뷰가 삭제되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(403).body(Map.of("message", e.getMessage()));
        }
    }
}