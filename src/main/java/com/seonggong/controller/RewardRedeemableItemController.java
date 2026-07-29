package com.seonggong.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seonggong.dto.RewardRedeemableItemResponse;
import com.seonggong.service.RewardRedeemableItemService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reward-redeemable-items")
@RequiredArgsConstructor
public class RewardRedeemableItemController {

    private final RewardRedeemableItemService service;

    @GetMapping
    public ResponseEntity<List<RewardRedeemableItemResponse>> getItems() {
        return ResponseEntity.ok(service.getActiveItems());
    }
}