package com.seonggong.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seonggong.service.AdminAuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String businessNumber = body.get("businessNumber");
        String phone = body.get("phone");
        String password = body.get("password");

        if (adminAuthService.isValidLogin(businessNumber, phone, password)) {
            return ResponseEntity.ok(Map.of("success", true));
        }
        return ResponseEntity.status(401)
                .body(Map.of("message", "사업자등록번호, 전화번호, 비밀번호를 다시 확인해 주세요."));
    }
}