package com.seonggong.controller;

import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

@RestController
@RequestMapping("/api/auth/google")
public class GoogleAuthController {

    @Value("${google.client.id}")
    private String googleClientId;

    /**
     * POST /api/auth/google/verify
     * body: { "idToken": "구글에서 받은 ID 토큰" }
     * 구글 ID 토큰을 검증하고, 검증되면 이메일을 반환합니다.
     */
    @PostMapping("/verify")
    public ResponseEntity<?> verifyGoogleToken(@RequestBody Map<String, String> body) {
        String idTokenString = body.get("idToken");
        if (idTokenString == null || idTokenString.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "idToken이 없습니다."));
        }

        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(googleClientId)).build();

            GoogleIdToken idToken = verifier.verify(idTokenString);

            if (idToken == null) {
                return ResponseEntity.status(401).body(Map.of("message", "유효하지 않은 Google 토큰입니다."));
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            Boolean emailVerified = payload.getEmailVerified();

            if (email == null || !Boolean.TRUE.equals(emailVerified)) {
                return ResponseEntity.status(401).body(Map.of("message", "이메일이 확인되지 않았습니다."));
            }

            return ResponseEntity.ok(Map.of(
                    "verified", true,
                    "email", email));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Google 토큰 검증 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
}