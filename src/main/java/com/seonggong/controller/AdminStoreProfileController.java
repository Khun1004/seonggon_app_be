package com.seonggong.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seonggong.dto.CreateClosedDateRequest;
import com.seonggong.dto.UpsertStoreProfileRequest;
import com.seonggong.service.AdminAuthService;
import com.seonggong.service.StoreProfileService;

import lombok.RequiredArgsConstructor;

// 사장님 전용 — 주소·전화·영업시간 수정, 휴무일 추가·삭제.
@RestController
@RequestMapping("/api/admin/store-profile")
@RequiredArgsConstructor
public class AdminStoreProfileController {

    private final AdminAuthService adminAuthService;
    private final StoreProfileService storeProfileService;

    @GetMapping
    public ResponseEntity<?> getProfile(
            @RequestHeader("X-Admin-Password") String adminPassword) {
        try {
            adminAuthService.requireAdmin(adminPassword);
            return ResponseEntity.ok(storeProfileService.getProfile());
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping
    public ResponseEntity<?> updateProfile(
            @RequestHeader("X-Admin-Password") String adminPassword,
            @RequestBody UpsertStoreProfileRequest request) {
        try {
            adminAuthService.requireAdmin(adminPassword);
            return ResponseEntity.ok(storeProfileService.updateProfile(request));
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/closed-dates")
    public ResponseEntity<?> getAllClosedDates(
            @RequestHeader("X-Admin-Password") String adminPassword) {
        try {
            adminAuthService.requireAdmin(adminPassword);
            return ResponseEntity.ok(storeProfileService.getAllClosedDatesForAdmin());
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/closed-dates")
    public ResponseEntity<?> addClosedDate(
            @RequestHeader("X-Admin-Password") String adminPassword,
            @RequestBody CreateClosedDateRequest request) {
        try {
            adminAuthService.requireAdmin(adminPassword);
            return ResponseEntity.ok(storeProfileService.addClosedDate(request));
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/closed-dates/{id}")
    public ResponseEntity<?> removeClosedDate(
            @PathVariable("id") Long id,
            @RequestHeader("X-Admin-Password") String adminPassword) {
        try {
            adminAuthService.requireAdmin(adminPassword);
            storeProfileService.removeClosedDate(id);
            return ResponseEntity.ok(Map.of("message", "휴무일이 삭제되었습니다."));
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        }
    }
}