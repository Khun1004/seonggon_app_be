package com.seonggong.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seonggong.dto.UpsertVisitStampSettingsRequest;
import com.seonggong.service.AdminAuthService;
import com.seonggong.service.VisitStampSettingsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/visit-stamp-settings")
@RequiredArgsConstructor
public class AdminVisitStampSettingsController {

    private final AdminAuthService adminAuthService;
    private final VisitStampSettingsService service;

    @GetMapping
    public ResponseEntity<?> getSettings(@RequestHeader("X-Admin-Password") String adminPassword) {
        try {
            adminAuthService.requireAdmin(adminPassword);
            return ResponseEntity.ok(service.getSettings());
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping
    public ResponseEntity<?> updateSettings(
            @RequestHeader("X-Admin-Password") String adminPassword,
            @RequestBody UpsertVisitStampSettingsRequest request) {
        try {
            adminAuthService.requireAdmin(adminPassword);
            return ResponseEntity.ok(service.updateSettings(request));
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
        }
    }
}