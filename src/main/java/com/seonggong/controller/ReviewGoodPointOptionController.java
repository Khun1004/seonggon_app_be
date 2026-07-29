package com.seonggong.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seonggong.dto.ReviewGoodPointOptionResponse;
import com.seonggong.service.ReviewGoodPointOptionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/review-good-points")
@RequiredArgsConstructor
public class ReviewGoodPointOptionController {

    private final ReviewGoodPointOptionService service;

    @GetMapping
    public ResponseEntity<List<ReviewGoodPointOptionResponse>> getOptions() {
        return ResponseEntity.ok(service.getActiveOptions());
    }
}