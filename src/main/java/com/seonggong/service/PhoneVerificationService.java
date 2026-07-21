package com.seonggong.service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class PhoneVerificationService {

    // 전화번호별 발급된 인증번호를 임시로 저장 (서버 메모리, 재시작하면 사라짐)
    private final Map<String, String> codeStore = new ConcurrentHashMap<>();

    /**
     * 인증번호 생성 — 실제 SMS는 보내지 않고, 생성한 번호를 그대로 반환합니다.
     * 프론트엔드가 이 값을 화면에 보여줍니다 (개발/테스트용 방식).
     */
    public String generateCode(String phone) {
        String code = String.format("%06d", new Random().nextInt(1_000_000));
        codeStore.put(phone, code);
        return code;
    }

    public boolean verifyCode(String phone, String inputCode) {
        String savedCode = codeStore.get(phone);
        if (savedCode == null)
            return false;
        boolean matched = savedCode.equals(inputCode);
        if (matched) {
            codeStore.remove(phone); // 인증 성공하면 1회용으로 제거
        }
        return matched;
    }
}