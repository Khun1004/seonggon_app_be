package com.seonggong.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seonggong.dto.ReviewGuideStepResponse;
import com.seonggong.service.ReviewGuideStepService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/review-guide")
@RequiredArgsConstructor
public class ReviewGuideStepController {

    private final ReviewGuideStepService service;

    @GetMapping
    public ResponseEntity<List<ReviewGuideStepResponse>> getSteps() {
        return ResponseEntity.ok(service.getActiveSteps());
    }
}