package com.seonggong.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seonggong.dto.PhoneVerifyRequest;
import com.seonggong.service.PhoneVerificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth/phone")
@RequiredArgsConstructor
public class PhoneVerificationController {

    private final PhoneVerificationService phoneVerificationService;

    /**
     * POST /api/auth/phone/send
     * body: { "phone": "010-1234-5678" }
     * 응답에 인증번호를 그대로 포함시켜서 반환합니다 (가짜 인증 방식).
     */
    @PostMapping("/send")
    public ResponseEntity<?> sendCode(@RequestBody Map<String, String> body) {
        String phone = body.get("phone");
        if (phone == null || phone.isBlank()) {
            return ResponseEntity.badRequest().body("전화번호를 입력해 주세요.");
        }
        String code = phoneVerificationService.generateCode(phone);
        // 실제 서비스라면 이 code를 SMS로 발송하고 응답에는 포함하지 않아야 합니다.
        return ResponseEntity.ok(Map.of(
                "message", "인증번호가 발급되었습니다.",
                "code", code // 데모용: 화면에 바로 표시하기 위해 포함
        ));
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyCode(@RequestBody PhoneVerifyRequest request) {
        boolean ok = phoneVerificationService.verifyCode(request.getPhone(), request.getCode());
        if (ok) {
            return ResponseEntity.ok(Map.of("verified", true));
        } else {
            return ResponseEntity.status(400).body(Map.of("verified", false, "message", "인증번호가 일치하지 않습니다."));
        }
    }
}