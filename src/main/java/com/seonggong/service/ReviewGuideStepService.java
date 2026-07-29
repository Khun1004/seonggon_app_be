package com.seonggong.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seonggong.dto.ReviewGuideStepResponse;
import com.seonggong.dto.UpsertReviewGuideStepRequest;
import com.seonggong.entity.ReviewGuideStep;
import com.seonggong.repository.ReviewGuideStepRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewGuideStepService {

    private final ReviewGuideStepRepository repository;

    public List<ReviewGuideStepResponse> getActiveSteps() {
        return repository.findAllByActiveTrueOrderByDisplayOrderAsc().stream()
                .map(ReviewGuideStepResponse::from)
                .toList();
    }

    public List<ReviewGuideStepResponse> getAllSteps() {
        return repository.findAllByOrderByDisplayOrderAsc().stream()
                .map(ReviewGuideStepResponse::from)
                .toList();
    }

    @Transactional
    public ReviewGuideStepResponse createStep(UpsertReviewGuideStepRequest request) {
        ReviewGuideStep step = new ReviewGuideStep();
        applyRequest(step, request);
        return ReviewGuideStepResponse.from(repository.save(step));
    }

    @Transactional
    public ReviewGuideStepResponse updateStep(Long id, UpsertReviewGuideStepRequest request) {
        ReviewGuideStep step = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("단계를 찾을 수 없습니다."));
        applyRequest(step, request);
        return ReviewGuideStepResponse.from(step);
    }

    @Transactional
    public void deleteStep(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("단계를 찾을 수 없습니다.");
        }
        repository.deleteById(id);
    }

    private void applyRequest(ReviewGuideStep step, UpsertReviewGuideStepRequest request) {
        step.setTitle(request.getTitle());
        step.setDescription(request.getDescription());
        step.setImageUrl(request.getImageUrl());
        step.setDisplayOrder(request.getDisplayOrder());
        step.setActive(request.isActive());
    }
}