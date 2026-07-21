package com.seonggong.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String loginId;

    @Column(nullable = false)
    private String password; // BCrypt로 암호화 저장

    @Column(nullable = false, length = 30)
    private String nickname;

    @Column(unique = true, length = 20)
    private String phone;

    @Column(unique = true)
    private String email; // 구글 로그인 이메일

    @Column(unique = true)
    private String googleId; // 구글 계정 고유 sub 값

    // 프로필 사진 — 서버 uploads 폴더에 저장된 경로 (예: /uploads/avatars/xxx.jpg)
    // 로그인만 하면 어느 기기에서든 같은 사진이 보이도록 서버에 저장합니다.
    @Column(length = 300)
    private String avatarUrl;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}