package com.seonggong.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seonggong.service.StoreInfoService;

import lombok.RequiredArgsConstructor;

// 손님용 — 숨겨지지 않은 안내 문구만 보여줍니다. 로그인 필요 없음.
@RestController
@RequestMapping("/api/store-info")
@RequiredArgsConstructor
public class StoreInfoController {

    private final StoreInfoService storeInfoService;

    @GetMapping
    public ResponseEntity<?> getActiveSections() {
        return ResponseEntity.ok(storeInfoService.getActiveSections());
    }
}