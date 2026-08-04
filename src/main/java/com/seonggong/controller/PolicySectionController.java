package com.seonggong.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seonggong.dto.PolicySectionResponse;
import com.seonggong.service.PolicySectionService;

import lombok.RequiredArgsConstructor;

// 손님용 — type은 "terms" 또는 "privacy"
@RestController
@RequestMapping("/api/policy-sections")
@RequiredArgsConstructor
public class PolicySectionController {

    private final PolicySectionService service;

    @GetMapping("/{type}")
    public ResponseEntity<List<PolicySectionResponse>> getSections(
            @PathVariable("type") String type) {
        String normalized = "privacy".equalsIgnoreCase(type) ? "PRIVACY" : "TERMS";
        return ResponseEntity.ok(service.getActiveSections(normalized));
    }
}