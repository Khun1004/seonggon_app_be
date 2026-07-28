package com.seonggong.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seonggong.dto.IngredientSetResponse;
import com.seonggong.dto.MenuIngredientDto;
import com.seonggong.dto.UpsertIngredientSetRequest;
import com.seonggong.entity.IngredientSet;
import com.seonggong.entity.MenuIngredient;
import com.seonggong.repository.IngredientSetRepository;
import com.seonggong.repository.MenuItemRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IngredientSetService {

    private final IngredientSetRepository ingredientSetRepository;
    private final MenuItemRepository menuItemRepository;

    @Transactional(readOnly = true)
    public List<IngredientSetResponse> getAll() {
        return ingredientSetRepository.findAllByOrderByNameAsc().stream()
                .map(s -> IngredientSetResponse.from(s, countUsage(s.getId())))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    protected int countUsage(Long setId) {
        return (int) menuItemRepository.findAll().stream()
                .filter(m -> m.getIngredientSet() != null
                        && m.getIngredientSet().getId().equals(setId))
                .count();
    }

    @Transactional
    public IngredientSetResponse create(UpsertIngredientSetRequest request) {
        IngredientSet set = new IngredientSet();
        applyRequest(set, request);
        IngredientSet saved = ingredientSetRepository.save(set);
        return IngredientSetResponse.from(saved, 0);
    }

    @Transactional
    public IngredientSetResponse update(Long id, UpsertIngredientSetRequest request) {
        IngredientSet set = ingredientSetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("재료 세트를 찾을 수 없습니다."));
        applyRequest(set, request);
        return IngredientSetResponse.from(set, countUsage(id));
    }

    @Transactional
    public void delete(Long id) {
        if (!ingredientSetRepository.existsById(id)) {
            throw new IllegalArgumentException("재료 세트를 찾을 수 없습니다.");
        }
        if (countUsage(id) > 0) {
            throw new IllegalArgumentException(
                    "이 세트를 쓰고 있는 메뉴가 있어서 삭제할 수 없어요. 먼저 그 메뉴들의 재료 세트를 다른 걸로 바꿔주세요.");
        }
        ingredientSetRepository.deleteById(id);
    }

    private void applyRequest(IngredientSet set, UpsertIngredientSetRequest request) {
        set.setName(request.getName());
        set.getIngredients().clear();
        if (request.getIngredients() != null) {
            for (MenuIngredientDto dto : request.getIngredients()) {
                MenuIngredient ingredient = new MenuIngredient();
                ingredient.setName(dto.getName());
                ingredient.setImageUrl(dto.getImageUrl());
                set.getIngredients().add(ingredient);
            }
        }
    }
}