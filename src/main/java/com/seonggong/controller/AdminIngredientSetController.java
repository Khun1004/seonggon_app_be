package com.seonggong.controller;

import java.util.List;
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

import com.seonggong.dto.IngredientSetResponse;
import com.seonggong.dto.UpsertIngredientSetRequest;
import com.seonggong.service.AdminAuthService;
import com.seonggong.service.IngredientSetService;

import lombok.RequiredArgsConstructor;

// 사장님 전용 — 여러 메뉴가 함께 쓰는 "재료 세트"를 관리합니다.
@RestController
@RequestMapping("/api/admin/ingredient-sets")
@RequiredArgsConstructor
public class AdminIngredientSetController {

    private final AdminAuthService adminAuthService;
    private final IngredientSetService ingredientSetService;

    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestHeader("X-Admin-Password") String adminPassword) {
        try {
            adminAuthService.requireAdmin(adminPassword);
            List<IngredientSetResponse> sets = ingredientSetService.getAll();
            return ResponseEntity.ok(sets);
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> create(
            @RequestHeader("X-Admin-Password") String adminPassword,
            @RequestBody UpsertIngredientSetRequest request) {
        try {
            adminAuthService.requireAdmin(adminPassword);
            return ResponseEntity.ok(ingredientSetService.create(request));
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable("id") Long id,
            @RequestHeader("X-Admin-Password") String adminPassword,
            @RequestBody UpsertIngredientSetRequest request) {
        try {
            adminAuthService.requireAdmin(adminPassword);
            return ResponseEntity.ok(ingredientSetService.update(id, request));
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
            ingredientSetService.delete(id);
            return ResponseEntity.ok(Map.of("message", "재료 세트가 삭제되었습니다."));
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(Map.of("message", e.getMessage()));
        }
    }
}