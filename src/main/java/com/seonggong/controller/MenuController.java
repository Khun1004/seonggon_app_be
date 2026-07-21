package com.seonggong.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seonggong.service.MenuService;

import lombok.RequiredArgsConstructor;

// 손님용 — 숨겨지지 않은 메뉴 목록만 보여줍니다. 로그인 필요 없음.
@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @GetMapping
    public ResponseEntity<?> getActiveMenus() {
        return ResponseEntity.ok(menuService.getActiveMenus());
    }
}