package com.seonggong.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.seonggong.dto.RedeemRewardRequest;
import com.seonggong.dto.RewardSummaryResponse;
import com.seonggong.service.RewardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/rewards")
@RequiredArgsConstructor
public class RewardController {

    private final RewardService rewardService;

    @GetMapping("/summary")
    public ResponseEntity<RewardSummaryResponse> getSummary(@RequestParam("loginId") String loginId) {
        return ResponseEntity.ok(rewardService.getSummary(loginId));
    }

    @PostMapping("/redeem")
    public ResponseEntity<?> redeem(@RequestBody RedeemRewardRequest request) {
        try {
            RewardSummaryResponse summary = rewardService.redeem(
                    request.getLoginId(), request.getItemName(), request.getAmount());
            return ResponseEntity.ok(summary);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(Map.of("message", e.getMessage()));
        }
    }
}