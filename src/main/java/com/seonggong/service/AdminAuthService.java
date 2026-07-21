package com.seonggong.service;

import org.springframework.stereotype.Service;

import com.seonggong.config.AdminProperties;

import lombok.RequiredArgsConstructor;

// 관리자 전용 API를 보호하는 아주 단순한 비밀번호 검증 — 사장님 한 분만 쓰는
// 화면이라 복잡한 로그인 세션 없이, 요청마다 비밀번호를 같이 보내서 확인합니다.
@Service
@RequiredArgsConstructor
public class AdminAuthService {

    private final AdminProperties adminProperties;

    public boolean isValidPassword(String password) {
        return adminProperties.getPassword().equals(password);
    }

    // 관리자 API 컨트롤러에서 이 메서드 하나만 맨 앞에서 호출하면 보호가 됩니다.
    public void requireAdmin(String password) {
        if (!isValidPassword(password)) {
            throw new SecurityException("관리자 인증이 필요합니다.");
        }
    }
}