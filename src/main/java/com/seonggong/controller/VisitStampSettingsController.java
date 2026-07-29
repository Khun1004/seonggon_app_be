package com.seonggong.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seonggong.service.VisitStampSettingsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/visit-stamp-settings")
@RequiredArgsConstructor
public class VisitStampSettingsController {

    private final VisitStampSettingsService service;

    @GetMapping
    public ResponseEntity<?> getSettings() {
        return ResponseEntity.ok(service.getSettings());
    }
}