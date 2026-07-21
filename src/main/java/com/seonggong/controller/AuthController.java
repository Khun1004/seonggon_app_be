package com.seonggong.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.seonggong.dto.LoginRequest;
import com.seonggong.dto.SignupRequest;
import com.seonggong.entity.User;
import com.seonggong.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/check-id")
    public ResponseEntity<?> checkLoginId(@RequestParam("loginId") String loginId) {
        boolean available = userService.isLoginIdAvailable(loginId);
        return ResponseEntity.ok(Map.of("available", available));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        try {
            User user = userService.signup(request);
            return ResponseEntity.ok(Map.of(
                    "message", "회원가입이 완료되었습니다.",
                    "nickname", user.getNickname()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            User user = userService.login(request);
            return ResponseEntity.ok(Map.of(
                    "message", "로그인 성공",
                    "nickname", user.getNickname(),
                    "loginId", user.getLoginId(),
                    "phone", user.getPhone() == null ? "" : user.getPhone(),
                    "avatarUrl", user.getAvatarUrl() == null ? "" : user.getAvatarUrl()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * 아이디 찾기 — 전화번호 인증(가짜 인증)이 먼저 끝난 후, 그 전화번호로 가입된 아이디를 반환합니다.
     * body: { "phone": "010-1234-5678" }
     */
    @PostMapping("/find-id")
    public ResponseEntity<?> findId(@RequestBody Map<String, String> body) {
        String phone = body.get("phone");
        try {
            String loginId = userService.findLoginIdByPhone(phone);
            return ResponseEntity.ok(Map.of("loginId", loginId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * 비밀번호 재설정 — 아이디 + 전화번호로 본인 확인 후 새 비밀번호로 변경합니다.
     * body: { "loginId": "...", "phone": "...", "newPassword": "..." }
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
        String loginId = body.get("loginId");
        String phone = body.get("phone");
        String newPassword = body.get("newPassword");
        try {
            userService.resetPassword(loginId, phone, newPassword);
            return ResponseEntity.ok(Map.of("message", "비밀번호가 변경되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * 닉네임 변경
     * body: { "loginId": "...", "nickname": "..." }
     */
    @PatchMapping("/nickname")
    public ResponseEntity<?> updateNickname(@RequestBody Map<String, String> body) {
        String loginId = body.get("loginId");
        String nickname = body.get("nickname");
        try {
            User user = userService.updateNickname(loginId, nickname);
            return ResponseEntity.ok(Map.of(
                    "message", "닉네임이 변경되었습니다.",
                    "nickname", user.getNickname()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * 비밀번호 변경 (로그인된 상태에서) — 현재 비밀번호 확인 후 새 비밀번호로 변경
     * body: { "loginId": "...", "currentPassword": "...", "newPassword": "..." }
     */
    @PatchMapping("/password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> body) {
        String loginId = body.get("loginId");
        String currentPassword = body.get("currentPassword");
        String newPassword = body.get("newPassword");
        try {
            userService.changePassword(loginId, currentPassword, newPassword);
            return ResponseEntity.ok(Map.of("message", "비밀번호가 변경되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * 프로필 사진 업로드 — 서버 파일로 저장하고, 그 경로를 회원 정보에 저장합니다.
     * body: { "loginId": "...", "imageBase64": "..." }
     */
    @PostMapping("/avatar")
    public ResponseEntity<?> updateAvatar(@RequestBody Map<String, String> body) {
        String loginId = body.get("loginId");
        String imageBase64 = body.get("imageBase64");
        try {
            String avatarUrl = userService.updateAvatar(loginId, imageBase64);
            return ResponseEntity.ok(Map.of(
                    "message", "프로필 사진이 변경되었습니다.",
                    "avatarUrl", avatarUrl));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}