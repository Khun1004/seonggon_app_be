package com.seonggong.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {
    private String loginId;
    private String password;
    private String nickname;
    private String phone;
    private String email; // 구글 로그인 시 자동 입력됨
    private String googleId; // 구글 로그인 시 자동 입력됨
}