package com.seonggong.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seonggong.dto.ReviewMenuOptionResponse;
import com.seonggong.dto.UpsertReviewMenuOptionRequest;
import com.seonggong.entity.ReviewMenuOption;
import com.seonggong.repository.ReviewMenuOptionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewMenuOptionService {

    private final ReviewMenuOptionRepository repository;

    public List<ReviewMenuOptionResponse> getActiveOptions() {
        return repository.findAllByActiveTrueOrderByDisplayOrderAsc().stream()
                .map(ReviewMenuOptionResponse::from)
                .toList();
    }

    public List<ReviewMenuOptionResponse> getAllOptions() {
        return repository.findAllByOrderByDisplayOrderAsc().stream()
                .map(ReviewMenuOptionResponse::from)
                .toList();
    }

    @Transactional
    public ReviewMenuOptionResponse createOption(UpsertReviewMenuOptionRequest request) {
        ReviewMenuOption option = new ReviewMenuOption();
        applyRequest(option, request);
        return ReviewMenuOptionResponse.from(repository.save(option));
    }

    @Transactional
    public ReviewMenuOptionResponse updateOption(Long id, UpsertReviewMenuOptionRequest request) {
        ReviewMenuOption option = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("항목을 찾을 수 없습니다."));
        applyRequest(option, request);
        return ReviewMenuOptionResponse.from(option);
    }

    @Transactional
    public void deleteOption(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("항목을 찾을 수 없습니다.");
        }
        repository.deleteById(id);
    }

    private void applyRequest(ReviewMenuOption option, UpsertReviewMenuOptionRequest request) {
        option.setName(request.getName());
        option.setDisplayOrder(request.getDisplayOrder());
        option.setActive(request.isActive());
    }
}