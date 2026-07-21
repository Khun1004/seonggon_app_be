package com.seonggong.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "toss")
public class TossProperties {

    // 공개돼도 괜찮은 키 — 앱(프론트)에도 그대로 노출됩니다.
    private String clientKey;

    // 절대 노출되면 안 되는 키 — 서버(이 클래스)에서만 사용합니다.
    private String secretKey;
}