package com.seonggong.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seonggong.dto.ReviewMenuOptionResponse;
import com.seonggong.service.ReviewMenuOptionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/review-menu-options")
@RequiredArgsConstructor
public class ReviewMenuOptionController {

    private final ReviewMenuOptionService service;

    @GetMapping
    public ResponseEntity<List<ReviewMenuOptionResponse>> getOptions() {
        return ResponseEntity.ok(service.getActiveOptions());
    }
}