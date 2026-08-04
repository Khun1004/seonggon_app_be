package com.seonggong.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seonggong.dto.ChangeAdminPasswordRequest;
import com.seonggong.dto.UpsertAdminAccountRequest;
import com.seonggong.service.AdminAccountService;
import com.seonggong.service.AdminAuthService;

import lombok.RequiredArgsConstructor;

// 사장님 본인의 연락처·주소·정산 계좌·사업자 정보 + 비밀번호 변경 — 관리자
// 전용, 손님은 볼 수 없어요.
@RestController
@RequestMapping("/api/admin/account")
@RequiredArgsConstructor
public class AdminAccountController {

    private final AdminAuthService adminAuthService;
    private final AdminAccountService service;

    @GetMapping
    public ResponseEntity<?> getAccount(@RequestHeader("X-Admin-Password") String adminPassword) {
        try {
            adminAuthService.requireAdmin(adminPassword);
            return ResponseEntity.ok(service.getAccount());
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping
    public ResponseEntity<?> updateAccount(
            @RequestHeader("X-Admin-Password") String adminPassword,
            @RequestBody UpsertAdminAccountRequest request) {
        try {
            adminAuthService.requireAdmin(adminPassword);
            return ResponseEntity.ok(service.updateAccount(request));
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
        }
    }

    // 비밀번호 변경 — 현재 비밀번호가 맞아야만 바뀝니다.
    @PutMapping("/password")
    public ResponseEntity<?> changePassword(
            @RequestHeader("X-Admin-Password") String adminPassword,
            @RequestBody ChangeAdminPasswordRequest request) {
        try {
            adminAuthService.requireAdmin(adminPassword);
            service.changePassword(request);
            return ResponseEntity.ok(Map.of("message", "비밀번호가 변경되었습니다."));
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(400).body(Map.of("message", e.getMessage()));
        }
    }
}