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

import com.seonggong.dto.MenuItemResponse;
import com.seonggong.dto.UpsertMenuItemRequest;
import com.seonggong.entity.MenuItem;
import com.seonggong.repository.MenuItemRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuItemRepository menuItemRepository;

    // 손님용 — 숨겨지지 않은 메뉴만 카테고리·순서대로
    public List<MenuItemResponse> getActiveMenus() {
        return menuItemRepository.findByActiveTrueOrderByCategoryAscDisplayOrderAsc()
                .stream()
                .map(MenuItemResponse::from)
                .collect(Collectors.toList());
    }

    // 관리자용 — 숨겨진 메뉴도 포함해서 전체
    public List<MenuItemResponse> getAllMenusForAdmin() {
        return menuItemRepository.findAllByOrderByCategoryAscDisplayOrderAsc()
                .stream()
                .map(MenuItemResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public MenuItemResponse createMenu(UpsertMenuItemRequest request) {
        MenuItem item = new MenuItem();
        applyRequest(item, request);
        return MenuItemResponse.from(menuItemRepository.save(item));
    }

    @Transactional
    public MenuItemResponse updateMenu(Long id, UpsertMenuItemRequest request) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("메뉴를 찾을 수 없습니다."));
        applyRequest(item, request);
        return MenuItemResponse.from(item);
    }

    // 실제로 지우면 예전 리뷰/장바구니 등에서 참조하던 메뉴 id가 깨질 수 있어서,
    // 삭제 대신 active=false로 숨김 처리합니다.
    @Transactional
    public void hideMenu(Long id) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("메뉴를 찾을 수 없습니다."));
        item.setActive(false);
    }

    @Transactional
    public void restoreMenu(Long id) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("메뉴를 찾을 수 없습니다."));
        item.setActive(true);
    }

    // 진짜 삭제 — 숨기기(active=false)와 달리 DB에서 완전히 지웁니다.
    // 예전 예약/리뷰가 이 메뉴 id를 참조하고 있었다면, 거기서 메뉴 이름이
    // 빈 값으로 보일 수 있다는 점을 관리자 화면에서 미리 안내해요.
    @Transactional
    public void deleteMenu(Long id) {
        if (!menuItemRepository.existsById(id)) {
            throw new IllegalArgumentException("메뉴를 찾을 수 없습니다.");
        }
        menuItemRepository.deleteById(id);
    }

    private void applyRequest(MenuItem item, UpsertMenuItemRequest request) {
        item.setCategory(request.getCategory());
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setPrice(request.getPrice());
        item.setPriceVal(request.getPriceVal());
        item.setHot(request.isHot());
        if (request.getImageUrl() != null) {
            item.setImageUrl(request.getImageUrl());
        }
        item.setDisplayOrder(request.getDisplayOrder());
        item.setActive(request.isActive());

        // null이면 재료 목록은 그대로 두고, 값이 오면 통째로 교체합니다.
        if (request.getIngredients() != null) {
            item.getIngredients().clear();
            for (com.seonggong.dto.MenuIngredientDto dto : request.getIngredients()) {
                com.seonggong.entity.MenuIngredient ingredient = new com.seonggong.entity.MenuIngredient();
                ingredient.setName(dto.getName());
                ingredient.setImageUrl(dto.getImageUrl());
                item.getIngredients().add(ingredient);
            }
        }
    }

    // 리뷰 사진 업로드와 똑같은 방식 — base64로 받아서 서버 파일로 저장하고 경로를 돌려줍니다.
    public String uploadPhoto(String imageBase64) {
        try {
            String pureBase64 = imageBase64.contains(",")
                    ? imageBase64.substring(imageBase64.indexOf(",") + 1)
                    : imageBase64;
            byte[] imageBytes = Base64.getDecoder().decode(pureBase64);

            Path dir = Paths.get("uploads", "menu");
            Files.createDirectories(dir);

            String fileName = UUID.randomUUID() + ".jpg";
            Path filePath = dir.resolve(fileName);
            Files.write(filePath, imageBytes);

            return "/uploads/menu/" + fileName;
        } catch (IOException e) {
            throw new IllegalArgumentException("사진 저장에 실패했습니다.");
        }
    }
}