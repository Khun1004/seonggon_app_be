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

import com.seonggong.dto.UpsertRewardRedeemableItemRequest;
import com.seonggong.service.AdminAuthService;
import com.seonggong.service.RewardRedeemableItemService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/reward-redeemable-items")
@RequiredArgsConstructor
public class AdminRewardRedeemableItemController {

    private final AdminAuthService adminAuthService;
    private final RewardRedeemableItemService service;

    @GetMapping
    public ResponseEntity<?> getAll(@RequestHeader("X-Admin-Password") String adminPassword) {
        try {
            adminAuthService.requireAdmin(adminPassword);
            return ResponseEntity.ok(service.getAllItems());
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> create(
            @RequestHeader("X-Admin-Password") String adminPassword,
            @RequestBody UpsertRewardRedeemableItemRequest request) {
        try {
            adminAuthService.requireAdmin(adminPassword);
            return ResponseEntity.ok(service.createItem(request));
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable("id") Long id,
            @RequestHeader("X-Admin-Password") String adminPassword,
            @RequestBody UpsertRewardRedeemableItemRequest request) {
        try {
            adminAuthService.requireAdmin(adminPassword);
            return ResponseEntity.ok(service.updateItem(id, request));
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
            service.deleteItem(id);
            return ResponseEntity.ok(Map.of("message", "삭제되었습니다."));
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        }
    }

    // 사진 업로드 — base64 이미지를 서버 파일로 저장하고 그 경로를 돌려줍니다.
    // body: { "imageBase64": "..." }
    @PostMapping("/upload-photo")
    public ResponseEntity<?> uploadPhoto(
            @RequestHeader("X-Admin-Password") String adminPassword,
            @RequestBody Map<String, String> body) {
        try {
            adminAuthService.requireAdmin(adminPassword);
            String url = service.uploadPhoto(body.get("imageBase64"));
            return ResponseEntity.ok(Map.of("url", url));
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}