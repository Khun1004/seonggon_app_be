package com.seonggong.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seonggong.dto.StoreInfoSectionResponse;
import com.seonggong.dto.UpsertStoreInfoSectionRequest;
import com.seonggong.entity.StoreInfoSection;
import com.seonggong.repository.StoreInfoSectionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreInfoService {

    private final StoreInfoSectionRepository storeInfoSectionRepository;

    // 손님용 — 숨겨지지 않은 안내 문구만 그룹·순서대로
    public List<StoreInfoSectionResponse> getActiveSections() {
        return storeInfoSectionRepository.findByActiveTrueOrderByGroupAscDisplayOrderAsc()
                .stream()
                .map(StoreInfoSectionResponse::from)
                .collect(Collectors.toList());
    }

    // 관리자용 — 숨겨진 것도 포함해서 전체
    public List<StoreInfoSectionResponse> getAllForAdmin() {
        return storeInfoSectionRepository.findAllByOrderByGroupAscDisplayOrderAsc()
                .stream()
                .map(StoreInfoSectionResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public StoreInfoSectionResponse create(UpsertStoreInfoSectionRequest request) {
        StoreInfoSection section = new StoreInfoSection();
        applyRequest(section, request);
        return StoreInfoSectionResponse.from(storeInfoSectionRepository.save(section));
    }

    @Transactional
    public StoreInfoSectionResponse update(Long id, UpsertStoreInfoSectionRequest request) {
        StoreInfoSection section = storeInfoSectionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("안내 문구를 찾을 수 없습니다."));
        applyRequest(section, request);
        return StoreInfoSectionResponse.from(section);
    }

    @Transactional
    public void delete(Long id) {
        if (!storeInfoSectionRepository.existsById(id)) {
            throw new IllegalArgumentException("안내 문구를 찾을 수 없습니다.");
        }
        storeInfoSectionRepository.deleteById(id);
    }

    private void applyRequest(StoreInfoSection section, UpsertStoreInfoSectionRequest request) {
        section.setGroup(request.getGroup());
        section.setTitle(request.getTitle());
        section.setContent(request.getContent());
        section.setIcon(request.getIcon());
        if (request.getImageUrl() != null) {
            section.setImageUrl(request.getImageUrl());
        }
        section.setDisplayOrder(request.getDisplayOrder());
        section.setActive(request.isActive());
    }

    // 메뉴/좌석 사진과 똑같은 방식 — base64로 받아서 서버 파일로 저장하고 경로를 돌려줍니다.
    public String uploadPhoto(String imageBase64) {
        try {
            String pureBase64 = imageBase64.contains(",")
                    ? imageBase64.substring(imageBase64.indexOf(",") + 1)
                    : imageBase64;
            byte[] imageBytes = Base64.getDecoder().decode(pureBase64);

            Path dir = Paths.get("uploads", "store-info");
            Files.createDirectories(dir);

            String fileName = UUID.randomUUID() + ".jpg";
            Path filePath = dir.resolve(fileName);
            Files.write(filePath, imageBytes);

            return "/uploads/store-info/" + fileName;
        } catch (IOException e) {
            throw new IllegalArgumentException("사진 저장에 실패했습니다.");
        }
    }
}