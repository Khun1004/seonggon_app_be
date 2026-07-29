package com.seonggong.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seonggong.service.CouponNoticeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/coupon-notice")
@RequiredArgsConstructor
public class CouponNoticeController {

    private final CouponNoticeService service;

    @GetMapping
    public ResponseEntity<?> getNotice() {
        return ResponseEntity.ok(service.getNotice());
    }
}