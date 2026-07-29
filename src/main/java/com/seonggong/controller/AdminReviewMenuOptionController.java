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

import com.seonggong.dto.UpsertReviewMenuOptionRequest;
import com.seonggong.service.AdminAuthService;
import com.seonggong.service.ReviewMenuOptionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/review-menu-options")
@RequiredArgsConstructor
public class AdminReviewMenuOptionController {

    private final AdminAuthService adminAuthService;
    private final ReviewMenuOptionService service;

    @GetMapping
    public ResponseEntity<?> getAll(@RequestHeader("X-Admin-Password") String adminPassword) {
        try {
            adminAuthService.requireAdmin(adminPassword);
            return ResponseEntity.ok(service.getAllOptions());
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> create(
            @RequestHeader("X-Admin-Password") String adminPassword,
            @RequestBody UpsertReviewMenuOptionRequest request) {
        try {
            adminAuthService.requireAdmin(adminPassword);
            return ResponseEntity.ok(service.createOption(request));
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable("id") Long id,
            @RequestHeader("X-Admin-Password") String adminPassword,
            @RequestBody UpsertReviewMenuOptionRequest request) {
        try {
            adminAuthService.requireAdmin(adminPassword);
            return ResponseEntity.ok(service.updateOption(id, request));
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable("id") Long id,
            @RequestHeader("X-Admin-Password") String adminPassword) {
        try {
            adminAuthService.requireAdmin(adminPassword);
            service.deleteOption(id);
            return ResponseEntity.ok(Map.of("message", "삭제되었습니다."));
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        }
    }
}