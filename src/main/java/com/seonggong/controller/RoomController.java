package com.seonggong.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seonggong.service.RoomService;

import lombok.RequiredArgsConstructor;

// 손님용 — 숨겨지지 않은 좌석 목록만 보여줍니다. 로그인 필요 없음.
@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    public ResponseEntity<?> getActiveRooms() {
        return ResponseEntity.ok(roomService.getActiveRooms());
    }
}