package com.seonggong.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.seonggong.dto.ClaimVisitRewardRequest;
import com.seonggong.service.VisitRewardService;

import lombok.RequiredArgsConstructor;

// 손님용 — 방문 도장(5회 방문 혜택) 현황 조회 및 사용 처리. 로그인 필요.
@RestController
@RequestMapping("/api/rewards/visit-stamp")
@RequiredArgsConstructor
public class VisitRewardController {

    private final VisitRewardService visitRewardService;

    @GetMapping
    public ResponseEntity<?> getStatus(
            @RequestParam("phone") String phone,
            @RequestParam(value = "loginId", required = false) String loginId) {
        return ResponseEntity.ok(visitRewardService.getStatus(phone, loginId));
    }

    @PostMapping("/claim")
    public ResponseEntity<?> claim(@RequestBody ClaimVisitRewardRequest request) {
        try {
            return ResponseEntity.ok(
                    visitRewardService.claim(request.getPhone(), request.getLoginId()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(400).body(Map.of("message", e.getMessage()));
        }
    }
}