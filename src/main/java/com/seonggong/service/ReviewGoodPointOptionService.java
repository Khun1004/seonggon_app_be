package com.seonggong.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seonggong.dto.ReviewGoodPointOptionResponse;
import com.seonggong.dto.UpsertReviewGoodPointOptionRequest;
import com.seonggong.entity.ReviewGoodPointOption;
import com.seonggong.repository.ReviewGoodPointOptionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewGoodPointOptionService {

    private final ReviewGoodPointOptionRepository repository;

    public List<ReviewGoodPointOptionResponse> getActiveOptions() {
        return repository.findAllByActiveTrueOrderByDisplayOrderAsc().stream()
                .map(ReviewGoodPointOptionResponse::from)
                .toList();
    }

    public List<ReviewGoodPointOptionResponse> getAllOptions() {
        return repository.findAllByOrderByDisplayOrderAsc().stream()
                .map(ReviewGoodPointOptionResponse::from)
                .toList();
    }

    @Transactional
    public ReviewGoodPointOptionResponse createOption(UpsertReviewGoodPointOptionRequest request) {
        ReviewGoodPointOption option = new ReviewGoodPointOption();
        applyRequest(option, request);
        return ReviewGoodPointOptionResponse.from(repository.save(option));
    }

    @Transactional
    public ReviewGoodPointOptionResponse updateOption(Long id, UpsertReviewGoodPointOptionRequest request) {
        ReviewGoodPointOption option = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("항목을 찾을 수 없습니다."));
        applyRequest(option, request);
        return ReviewGoodPointOptionResponse.from(option);
    }

    @Transactional
    public void deleteOption(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("항목을 찾을 수 없습니다.");
        }
        repository.deleteById(id);
    }

    private void applyRequest(ReviewGoodPointOption option, UpsertReviewGoodPointOptionRequest request) {
        option.setEmoji(request.getEmoji());
        option.setLabel(request.getLabel());
        option.setDisplayOrder(request.getDisplayOrder());
        option.setActive(request.isActive());
    }
}