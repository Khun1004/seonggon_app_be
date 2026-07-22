package com.seonggong.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.seonggong.entity.MenuIngredient;
import com.seonggong.entity.MenuItem;
import com.seonggong.repository.MenuItemRepository;

import lombok.RequiredArgsConstructor;

// 서버가 켜질 때, 백숙류 메뉴에 재료(버섯 종류) 목록이 하나도 없으면
// 예전에 앱 코드에 고정값으로 있던 11종 버섯을 그대로 채워 넣습니다.
// 메뉴 자체(MenuItemSeeder)와 별도로 만든 이유: 메뉴는 이미 있고, 재료만
// 나중에 추가된 필드라서 "이 메뉴에 재료가 있는지"로 따로 확인해야 하기 때문입니다.
@Component
@RequiredArgsConstructor
public class MenuIngredientSeeder implements CommandLineRunner {

    private final MenuItemRepository menuItemRepository;

    private static final List<String> MUSHROOM_NAMES = List.of(
            "에노타리버섯", "만가닥버섯", "표고버섯", "양송이버섯", "건백목이버섯",
            "건흑목이버섯", "새송이버섯", "황금팽이버섯", "팽이버섯", "백만송이버섯", "초고버섯");

    private static final List<String> BAEKSUK_NAMES = List.of(
            "능이오리백숙", "능이닭백숙", "닭백숙", "오리백숙");

    // ingredients는 지연 로딩(LAZY) 컬렉션이라서, 세션(트랜잭션)이 열려있는
    // 동안에만 접근할 수 있어요. @Transactional 없이 CommandLineRunner에서
    // 바로 접근하면 "no Session" 에러가 납니다.
    @Override
    @Transactional
    public void run(String... args) {
        for (MenuItem item : menuItemRepository.findAll()) {
            if (!BAEKSUK_NAMES.contains(item.getName()))
                continue;
            if (!item.getIngredients().isEmpty())
                continue;

            for (String name : MUSHROOM_NAMES) {
                MenuIngredient ingredient = new MenuIngredient();
                ingredient.setName(name);
                item.getIngredients().add(ingredient);
            }
            menuItemRepository.save(item);
        }
    }
}