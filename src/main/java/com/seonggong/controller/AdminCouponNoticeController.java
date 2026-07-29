package com.seonggong.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seonggong.dto.UpsertCouponNoticeRequest;
import com.seonggong.service.AdminAuthService;
import com.seonggong.service.CouponNoticeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/coupon-notice")
@RequiredArgsConstructor
public class AdminCouponNoticeController {

    private final AdminAuthService adminAuthService;
    private final CouponNoticeService service;

    @GetMapping
    public ResponseEntity<?> getNotice(@RequestHeader("X-Admin-Password") String adminPassword) {
        try {
            adminAuthService.requireAdmin(adminPassword);
            return ResponseEntity.ok(service.getNotice());
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping
    public ResponseEntity<?> updateNotice(
            @RequestHeader("X-Admin-Password") String adminPassword,
            @RequestBody UpsertCouponNoticeRequest request) {
        try {
            adminAuthService.requireAdmin(adminPassword);
            return ResponseEntity.ok(service.updateNotice(request));
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
        }
    }
}