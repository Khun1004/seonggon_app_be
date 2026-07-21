package com.seonggong.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.seonggong.entity.MenuItem;
import com.seonggong.repository.MenuItemRepository;

import lombok.RequiredArgsConstructor;

// 서버가 켜질 때, menu_items 테이블이 비어있으면 지금까지 앱 코드에 고정값으로
// 있던 메뉴들을 그대로 옮겨 넣습니다. 이미 데이터가 있으면(사장님이 앱에서
// 직접 수정/추가했으면) 아무것도 하지 않습니다.
// 사진(imageUrl)은 비워뒀어요 — 이 메뉴들은 원래 앱 안에 내장된 사진을 쓰고
// 있었어서, 서버에 올려진 실제 파일이 없기 때문입니다. 관리자 화면에서 사진을
// 새로 업로드하면 그때부터 그 사진이 우선으로 보여요.
@Component
@RequiredArgsConstructor
public class MenuItemSeeder implements CommandLineRunner {

    private final MenuItemRepository menuItemRepository;

    @Override
    public void run(String... args) {
        if (menuItemRepository.count() > 0) {
            return;
        }

        // 백숙
        save("백숙", "능이오리백숙",
                "48시간 정성 우려낸 진국에 능이와 오리를 담은 품격 있는 보양 백숙",
                "69,000원", 69000, true, 1);
        save("백숙", "능이닭백숙",
                "48시간 정성으로 우린 육수에 능이와 토종닭을 넣어 끓여낸 깊은 맛의 진국 백숙",
                "65,000원", 65000, false, 2);
        save("백숙", "닭백숙",
                "24시간 비법육수와 백숙 본연의 국물을 섞어 또 24시간 끓여 만든 진국백숙",
                "55,000원", 55000, false, 3);
        save("백숙", "오리백숙",
                "24시간 비법육수와 백숙 본연의 국물을 섞어 또 24시간 끓여 만든 진국백숙",
                "60,000원", 60000, false, 4);

        // 고기
        save("고기", "산더미 오리간장불고기 3-4인",
                "성공식당 특제 간장소스가 더해진 유황오리와 신선 채소의 산더미 별미 메뉴",
                "54,000원", 54000, true, 1);
        save("고기", "산더미 오리간장불고기 2인",
                "유황오리를 급냉하여 얇게 썰어 신선한 야채와 특제소스로 버무린 메뉴",
                "42,000원", 42000, false, 2);
        save("고기", "유황오리생불고기",
                "생오리에 특제 숙성양념을 더하여 만든 양념 오리불고기",
                "49,000원", 49000, false, 3);
        save("고기", "유황오리로스구이",
                "생오리에 들기름, 마늘을 넣어 고소하고 담백한 로스구이",
                "47,000원", 47000, false, 4);

        // 사이드
        save("사이드", "해물파전",
                "오징어와 각종 야채를 넣어 바삭하게 튀긴 해물파전",
                "15,000원", 15000, false, 1);
        save("사이드", "도토리묵",
                "탱글탱글한 수제 도토리묵",
                "9,000원", 9000, false, 2);
        save("사이드", "찹쌀동동주",
                "산지에서 직접 공수한 고소하고 달콤한 찹쌀동동주",
                "8,000원", 8000, false, 3);
    }

    private void save(
            String category, String name, String description,
            String price, int priceVal, boolean isHot, int order) {
        MenuItem item = new MenuItem();
        item.setCategory(category);
        item.setName(name);
        item.setDescription(description);
        item.setPrice(price);
        item.setPriceVal(priceVal);
        item.setHot(isHot);
        item.setDisplayOrder(order);
        item.setActive(true);
        menuItemRepository.save(item);
    }
}