package com.seonggong.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seonggong.dto.UpsertMenuItemRequest;
import com.seonggong.service.AdminAuthService;
import com.seonggong.service.MenuService;

import lombok.RequiredArgsConstructor;

// 사장님 전용 — 메뉴 추가/수정/숨김/사진 업로드. 전부 X-Admin-Password 헤더로 보호됩니다.
@RestController
@RequestMapping("/api/admin/menu")
@RequiredArgsConstructor
public class AdminMenuController {

    private final AdminAuthService adminAuthService;
    private final MenuService menuService;

    @GetMapping
    public ResponseEntity<?> getAllMenus(
            @RequestHeader("X-Admin-Password") String adminPassword) {
        try {
            adminAuthService.requireAdmin(adminPassword);
            return ResponseEntity.ok(menuService.getAllMenusForAdmin());
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createMenu(
            @RequestHeader("X-Admin-Password") String adminPassword,
            @RequestBody UpsertMenuItemRequest request) {
        try {
            adminAuthService.requireAdmin(adminPassword);
            return ResponseEntity.ok(menuService.createMenu(request));
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMenu(
            @PathVariable("id") Long id,
            @RequestHeader("X-Admin-Password") String adminPassword,
            @RequestBody UpsertMenuItemRequest request) {
        try {
            adminAuthService.requireAdmin(adminPassword);
            return ResponseEntity.ok(menuService.updateMenu(id, request));
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> hideMenu(
            @PathVariable("id") Long id,
            @RequestHeader("X-Admin-Password") String adminPassword) {
        try {
            adminAuthService.requireAdmin(adminPassword);
            menuService.hideMenu(id);
            return ResponseEntity.ok(Map.of("message", "메뉴가 숨겨졌습니다."));
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/restore")
    public ResponseEntity<?> restoreMenu(
            @PathVariable("id") Long id,
            @RequestHeader("X-Admin-Password") String adminPassword) {
        try {
            adminAuthService.requireAdmin(adminPassword);
            menuService.restoreMenu(id);
            return ResponseEntity.ok(Map.of("message", "메뉴가 다시 보이게 되었습니다."));
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<?> deleteMenuPermanently(
            @PathVariable("id") Long id,
            @RequestHeader("X-Admin-Password") String adminPassword) {
        try {
            adminAuthService.requireAdmin(adminPassword);
            menuService.deleteMenu(id);
            return ResponseEntity.ok(Map.of("message", "메뉴가 삭제되었습니다."));
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
            String url = menuService.uploadPhoto(body.get("imageBase64"));
            return ResponseEntity.ok(Map.of("url", url));
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(Map.of("message", e.getMessage()));
        }
    }
}