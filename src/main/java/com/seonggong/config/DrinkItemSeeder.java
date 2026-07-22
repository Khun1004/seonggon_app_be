package com.seonggong.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.seonggong.entity.MenuItem;
import com.seonggong.repository.MenuItemRepository;

import lombok.RequiredArgsConstructor;

// 서버가 켜질 때, "음료"/"주류" 카테고리에 등록된 메뉴가 하나도 없으면
// 예전에 손님 화면(components/Drinks/Drinks.tsx)에 고정값으로 있던
// 음료·주류 목록을 그대로 DB에 옮겨 넣습니다. 이미 하나라도 등록돼 있으면
// (사장님이 직접 추가/수정하셨으면) 아무것도 하지 않아요.
// MenuItemSeeder와 별도로 만든 이유: menu_items 테이블 자체는 이미 백숙/고기/
// 사이드 데이터로 차 있어서, "테이블이 비었는지"가 아니라 "이 카테고리가
// 비었는지"로 따로 확인해야 하기 때문입니다.
@Component
@RequiredArgsConstructor
public class DrinkItemSeeder implements CommandLineRunner {

    private final MenuItemRepository menuItemRepository;

    @Override
    public void run(String... args) {
        boolean hasDrinks = menuItemRepository.findAllByOrderByCategoryAscDisplayOrderAsc()
                .stream()
                .anyMatch(m -> "음료".equals(m.getCategory()) || "주류".equals(m.getCategory()));
        if (hasDrinks) {
            return;
        }

        // 음료
        save("음료", "Tams (탐스)", "", "2,000원", 2000, 1);
        save("음료", "Pepsi Cola", "", "2,000원", 2000, 2);
        save("음료", "Pepsi Cola Zero", "", "2,000원", 2000, 3);
        save("음료", "Chilsung Cider", "", "2,000원", 2000, 4);
        save("음료", "Chilsung Cider Zero", "", "2,000원", 2000, 5);

        // 주류
        save("주류", "참이슬 (Chamisul)", "", "5,000원", 5000, 1);
        save("주류", "참 (Cham)", "", "5,000원", 5000, 2);
        save("주류", "제로 (Zero)", "", "5,000원", 5000, 3);
        save("주류", "Cass (카스)", "", "5,000원", 5000, 4);
        save("주류", "Cass Zero", "", "5,000원", 5000, 5);
        save("주류", "복분자 (Bokbunjaju)", "", "5,000원", 5000, 6);
        save("주류", "동동주 (Dongdongju)", "", "5,000원", 5000, 7);
        save("주류", "막걸리 (Makgeolli)", "", "5,000원", 5000, 8);
    }

    private void save(
            String category, String name, String description,
            String price, int priceVal, int order) {
        MenuItem item = new MenuItem();
        item.setCategory(category);
        item.setName(name);
        item.setDescription(description);
        item.setPrice(price);
        item.setPriceVal(priceVal);
        item.setHot(false);
        item.setDisplayOrder(order);
        item.setActive(true);
        menuItemRepository.save(item);
    }
}