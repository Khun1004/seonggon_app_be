package com.seonggong.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seonggong.dto.FaqItemResponse;
import com.seonggong.dto.UpsertFaqItemRequest;
import com.seonggong.entity.FaqItem;
import com.seonggong.repository.FaqItemRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FaqItemService {

    private final FaqItemRepository repository;

    public List<FaqItemResponse> getActiveItems() {
        return repository.findAllByActiveTrueOrderByDisplayOrderAsc().stream()
                .map(FaqItemResponse::from)
                .toList();
    }

    public List<FaqItemResponse> getAllItems() {
        return repository.findAllByOrderByDisplayOrderAsc().stream()
                .map(FaqItemResponse::from)
                .toList();
    }

    @Transactional
    public FaqItemResponse createItem(UpsertFaqItemRequest request) {
        FaqItem item = new FaqItem();
        applyRequest(item, request);
        return FaqItemResponse.from(repository.save(item));
    }

    @Transactional
    public FaqItemResponse updateItem(Long id, UpsertFaqItemRequest request) {
        FaqItem item = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("항목을 찾을 수 없습니다."));
        applyRequest(item, request);
        return FaqItemResponse.from(item);
    }

    @Transactional
    public void deleteItem(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("항목을 찾을 수 없습니다.");
        }
        repository.deleteById(id);
    }

    private void applyRequest(FaqItem item, UpsertFaqItemRequest request) {
        item.setCategory(request.getCategory());
        item.setCategoryIcon(
                request.getCategoryIcon() == null || request.getCategoryIcon().isBlank()
                        ? "help-circle-outline"
                        : request.getCategoryIcon());
        item.setQuestion(request.getQuestion());
        item.setAnswer(request.getAnswer());
        item.setDisplayOrder(request.getDisplayOrder());
        item.setActive(request.isActive());
    }
}