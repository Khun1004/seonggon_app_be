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

import com.seonggong.service.AdminAuthService;
import com.seonggong.service.NearbySpotService;

import lombok.RequiredArgsConstructor;

// 사장님 전용 — "팔공산 근처 가볼만한 곳" 명소를 추가/수정/삭제합니다.
@RestController
@RequestMapping("/api/admin/nearby-spots")
@RequiredArgsConstructor
public class AdminNearbySpotController {

    private final AdminAuthService adminAuthService;
    private final NearbySpotService nearbySpotService;

    @GetMapping
    public ResponseEntity<?> getAll(@RequestHeader("X-Admin-Password") String adminPassword) {
        try {
            adminAuthService.requireAdmin(adminPassword);
            return ResponseEntity.ok(nearbySpotService.getAllForAdmin());
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> create(
            @RequestHeader("X-Admin-Password") String adminPassword,
            @RequestBody Map<String, Object> body) {
        try {
            adminAuthService.requireAdmin(adminPassword);
            return ResponseEntity.ok(nearbySpotService.createSpot(
                    (String) body.get("name"),
                    (String) body.get("description"),
                    (String) body.get("icon"),
                    (String) body.get("imageUrl"),
                    body.get("sortOrder") == null ? 0 : ((Number) body.get("sortOrder")).intValue()));
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable("id") Long id,
            @RequestHeader("X-Admin-Password") String adminPassword,
            @RequestBody Map<String, Object> body) {
        try {
            adminAuthService.requireAdmin(adminPassword);
            return ResponseEntity.ok(nearbySpotService.updateSpot(
                    id,
                    (String) body.get("name"),
                    (String) body.get("description"),
                    (String) body.get("icon"),
                    (String) body.get("imageUrl"),
                    body.get("sortOrder") == null ? 0 : ((Number) body.get("sortOrder")).intValue()));
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
            nearbySpotService.deleteSpot(id);
            return ResponseEntity.ok(Map.of("message", "삭제되었습니다."));
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/upload-photo")
    public ResponseEntity<?> uploadPhoto(
            @RequestHeader("X-Admin-Password") String adminPassword,
            @RequestBody Map<String, String> body) {
        try {
            adminAuthService.requireAdmin(adminPassword);
            String url = nearbySpotService.uploadPhoto(body.get("imageBase64"));
            return ResponseEntity.ok(Map.of("url", url));
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(Map.of("message", e.getMessage()));
        }
    }
}