package com.seonggong.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seonggong.dto.UpsertVisitStampSettingsRequest;
import com.seonggong.dto.VisitStampSettingsResponse;
import com.seonggong.entity.VisitStampSettings;
import com.seonggong.repository.VisitStampSettingsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VisitStampSettingsService {

    private final VisitStampSettingsRepository repository;

    @Transactional
    public VisitStampSettings getOrCreate() {
        return repository.findById(1L).orElseGet(() -> repository.save(new VisitStampSettings()));
    }

    @Transactional
    public VisitStampSettingsResponse getSettings() {
        VisitStampSettings s = getOrCreate();
        return new VisitStampSettingsResponse(s.getRequiredVisits(), s.getRewardName());
    }

    @Transactional
    public VisitStampSettingsResponse updateSettings(UpsertVisitStampSettingsRequest request) {
        VisitStampSettings s = getOrCreate();
        if (request.getRequiredVisits() > 0) {
            s.setRequiredVisits(request.getRequiredVisits());
        }
        if (request.getRewardName() != null && !request.getRewardName().isBlank()) {
            s.setRewardName(request.getRewardName());
        }
        repository.save(s);
        return new VisitStampSettingsResponse(s.getRequiredVisits(), s.getRewardName());
    }
}