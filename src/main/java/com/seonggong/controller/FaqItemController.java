package com.seonggong.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seonggong.dto.FaqItemResponse;
import com.seonggong.service.FaqItemService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/faq")
@RequiredArgsConstructor
public class FaqItemController {

    private final FaqItemService service;

    @GetMapping
    public ResponseEntity<List<FaqItemResponse>> getItems() {
        return ResponseEntity.ok(service.getActiveItems());
    }
}