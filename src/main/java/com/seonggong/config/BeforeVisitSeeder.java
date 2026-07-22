package com.seonggong.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.seonggong.entity.StoreInfoSection;
import com.seonggong.repository.StoreInfoSectionRepository;

import lombok.RequiredArgsConstructor;

// 서버가 켜질 때, "before_visit"(방문 전 알아두세요) 그룹에 등록된 안내
// 문구가 하나도 없으면, 예전에 홈 화면(components/Home/BeforeYouVisit.tsx)에
// 고정값으로 있던 7가지 안내를 그대로 DB에 옮겨 넣습니다.
// StoreInfoSeeder와 별도로 만든 이유: store_info_sections 테이블 자체는
// 이미 소개/맛/좌석/오시는 길 데이터로 차 있어서, "테이블이 비었는지"가
// 아니라 "이 그룹이 비었는지"로 따로 확인해야 하기 때문입니다.
@Component
@RequiredArgsConstructor
public class BeforeVisitSeeder implements CommandLineRunner {

    private final StoreInfoSectionRepository storeInfoSectionRepository;

    @Override
    public void run(String... args) {
        boolean hasBeforeVisit = storeInfoSectionRepository.findAllByOrderByGroupAscDisplayOrderAsc()
                .stream()
                .anyMatch(s -> "before_visit".equals(s.getGroup()));
        if (hasBeforeVisit) {
            return;
        }

        save("영업시간", "매일 11:00 ~ 21:00", "time-outline", 1);
        save("테이블 이용 시간",
                "1회 예약은 1시간 이용이 기본이에요. 더 길게 이용하고 싶으시면 전화로 미리 문의해 주세요. "
                        + "이용 시간을 초과하시면 추가 요금이 발생할 수 있어요.",
                "hourglass-outline", 2);
        save("조리 시간 안내",
                "백숙류 메뉴는 정성껏 끓여내다 보니 조리에 40~60분 정도 걸려요. "
                        + "예약하고 오시면 도착 시간에 맞춰 미리 준비해드려요.",
                "restaurant-outline", 3);
        save("주말·공휴일 예약",
                "주말과 공휴일은 온라인 예약이 어려워요. 전화로 문의해 주시면 자리를 확인해드릴게요.",
                "calendar-outline", 4);
        save("주차",
                "매장 앞에 약 30대까지 무료로 주차하실 수 있어요. 만차 시 인근 길가에 주차 가능해요.",
                "car-outline", 5);
        save("반려동물 동반",
                "신발을 벗고 들어가는 룸 좌석에서만 반려동물과 함께하실 수 있어요.",
                "paw-outline", 6);
        save("단체 예약",
                "10인 이상 단체 예약은 앱이 아니라 전화로 문의해 주세요.",
                "people-outline", 7);
    }

    private void save(String title, String content, String icon, int order) {
        StoreInfoSection section = new StoreInfoSection();
        section.setGroup("before_visit");
        section.setTitle(title);
        section.setContent(content);
        section.setIcon(icon);
        section.setDisplayOrder(order);
        section.setActive(true);
        storeInfoSectionRepository.save(section);
    }
}