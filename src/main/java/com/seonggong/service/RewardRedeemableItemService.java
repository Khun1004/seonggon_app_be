package com.seonggong.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seonggong.dto.RewardRedeemableItemResponse;
import com.seonggong.dto.UpsertRewardRedeemableItemRequest;
import com.seonggong.entity.RewardRedeemableItem;
import com.seonggong.repository.RewardRedeemableItemRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RewardRedeemableItemService {

    private final RewardRedeemableItemRepository repository;

    public List<RewardRedeemableItemResponse> getActiveItems() {
        return repository.findAllByActiveTrueOrderByDisplayOrderAsc().stream()
                .map(RewardRedeemableItemResponse::from)
                .toList();
    }

    public List<RewardRedeemableItemResponse> getAllItems() {
        return repository.findAllByOrderByDisplayOrderAsc().stream()
                .map(RewardRedeemableItemResponse::from)
                .toList();
    }

    @Transactional
    public RewardRedeemableItemResponse createItem(UpsertRewardRedeemableItemRequest request) {
        RewardRedeemableItem item = new RewardRedeemableItem();
        applyRequest(item, request);
        return RewardRedeemableItemResponse.from(repository.save(item));
    }

    @Transactional
    public RewardRedeemableItemResponse updateItem(Long id, UpsertRewardRedeemableItemRequest request) {
        RewardRedeemableItem item = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("항목을 찾을 수 없습니다."));
        applyRequest(item, request);
        return RewardRedeemableItemResponse.from(item);
    }

    @Transactional
    public void deleteItem(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("항목을 찾을 수 없습니다.");
        }
        repository.deleteById(id);
    }

    private void applyRequest(RewardRedeemableItem item, UpsertRewardRedeemableItemRequest request) {
        item.setName(request.getName());
        item.setPrice(request.getPrice());
        item.setImageUrl(request.getImageUrl());
        item.setDisplayOrder(request.getDisplayOrder());
        item.setActive(request.isActive());
    }

    // base64 이미지를 uploads/reward-items/ 폴더에 파일로 저장하고, 그
    // 경로(URL)를 돌려줍니다. 리뷰 사진 업로드와 같은 방식이에요.
    public String uploadPhoto(String imageBase64) {
        try {
            String pureBase64 = imageBase64.contains(",")
                    ? imageBase64.substring(imageBase64.indexOf(",") + 1)
                    : imageBase64;
            byte[] imageBytes = Base64.getDecoder().decode(pureBase64);

            Path dir = Paths.get("uploads", "reward-items");
            Files.createDirectories(dir);

            String fileName = UUID.randomUUID() + ".jpg";
            Path filePath = dir.resolve(fileName);
            Files.write(filePath, imageBytes);

            return "/uploads/reward-items/" + fileName;
        } catch (IOException e) {
            throw new IllegalArgumentException("사진 저장에 실패했습니다.");
        }
    }
}