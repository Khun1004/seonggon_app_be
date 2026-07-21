package com.seonggong.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

// 사장님 전용 관리자 화면 접근용 비밀번호 — application-local.properties에서
// ADMIN_PASSWORD로 실제 값을 넣어주세요. 안 넣으면 기본값(admin1234)으로 동작합니다.
@Component
@Getter
public class AdminProperties {

    @Value("${admin.password}")
    private String password;
}