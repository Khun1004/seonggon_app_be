package com.seonggong.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.seonggong.entity.StoreInfoSection;
import com.seonggong.repository.StoreInfoSectionRepository;

import lombok.RequiredArgsConstructor;

// 서버가 켜질 때, store_info_sections 테이블이 비어있으면 지금까지 "정보" 화면에
// 고정 텍스트로 있던 안내 문구들을 그대로 옮겨 넣습니다. 이미 데이터가 있으면
// (사장님이 앱에서 직접 수정/추가했으면) 아무것도 하지 않습니다.
@Component
@RequiredArgsConstructor
public class StoreInfoSeeder implements CommandLineRunner {

    private final StoreInfoSectionRepository storeInfoSectionRepository;

    @Override
    public void run(String... args) {
        if (storeInfoSectionRepository.count() > 0) {
            return;
        }

        // 소개
        save("intro", "소개",
                "팔공산 자락 아래에서 30년 동안 한자리를 지켜온 성공식당은 3대째 이어져 내려오는 전통 있는 맛집입니다.\n\n"
                        + "오랜 세월 지역 주민은 물론 팔공산을 찾는 등산객들의 꾸준한 사랑을 받아왔으며, 가족모임이나 단체모임 장소로도 자주 선택되어 온 곳입니다.\n\n"
                        + "화려함보다 정직한 재료와 변함없는 손맛으로 식당의 명맥을 이어오고 있으며, 오늘도 정성껏 준비합니다.",
                "ribbon-outline", 1);

        // 성공식당의 맛
        save("taste", "맑고 깊은 [능이오리백숙]",
                "약재 향이 강한 한방백숙과는 결이 다릅니다. 능이버섯 고유의 은은한 향을 살려 남녀노소 누구나 편안하게 즐길 수 있도록 완성했습니다.\n\n"
                        + "48시간 이상 정성껏 우려낸 육수는 맑고 깔끔하면서도 깊은 감칠맛을 지녀, 가족 외식이나 점심 식사로도 부담 없이 선택하실 수 있습니다.",
                "restaurant-outline", 1);
        save("taste", "정성을 담은 [밑반찬]",
                "모든 밑반찬은 지역에서 공수한 우수한 재료로 직접 담급니다. 대한민국 국산김치 인증을 받은 김치를 비롯해, 반찬 하나까지 허투루 하지 않는 손맛은 우리의 자랑입니다.",
                null, 2);

        // 좌석 및 공간 — 1층/2층 안내
        save("seats", "1층 안내",
                "일반 홀 좌석 6개(2~8명)와 신발을 벗고 들어가는 프라이빗 룸(소형·중형·대형)을 갖추고 있어요. "
                        + "가족 단위 방문이나 소규모 모임에 편하게 이용하실 수 있어요.",
                "layers-outline", 1);
        save("seats", "2층 안내",
                "단체 손님을 위한 룸과 대형 홀을 갖추고 있어요. 최대 80명까지 수용 가능해서 "
                        + "가족 행사, 계모임, 단체 회식 장소로 자주 이용해 주세요.",
                "layers-outline", 2);

        // 오시는 길 및 주차
        save("directions", "주차 안내",
                "매장 앞 약 30대 무료 주차 가능. 만차 시 인근 길가 주차 가능.",
                "car-outline", 1);
        save("directions", "자차 이용",
                "네비 '팔공산 성공식당' 또는 '팔공산로 199길 12(39-9)' 검색",
                "navigate-outline", 2);
        save("directions", "대중교통",
                "급행 1번 버스 → 동화사 입구 하차 후 분수대 방향 50m",
                "bus-outline", 3);
    }

    private void save(String group, String title, String content, String icon, int order) {
        StoreInfoSection section = new StoreInfoSection();
        section.setGroup(group);
        section.setTitle(title);
        section.setContent(content);
        section.setIcon(icon);
        section.setDisplayOrder(order);
        section.setActive(true);
        storeInfoSectionRepository.save(section);
    }
}