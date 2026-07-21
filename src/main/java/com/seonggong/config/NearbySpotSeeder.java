package com.seonggong.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.seonggong.entity.NearbySpot;
import com.seonggong.repository.NearbySpotRepository;

import lombok.RequiredArgsConstructor;

// 서버가 켜질 때, nearby_spots 테이블이 비어있으면 팔공산 대표 명소 5곳을 자동으로 채워 넣습니다.
// 이미 데이터가 있으면(사장님이 직접 수정/추가했으면) 아무것도 하지 않습니다.
@Component
@RequiredArgsConstructor
public class NearbySpotSeeder implements CommandLineRunner {

    private final NearbySpotRepository nearbySpotRepository;

    @Override
    public void run(String... args) {
        if (nearbySpotRepository.count() > 0) {
            return;
        }

        nearbySpotRepository.save(spot(
                "동화사", "팔공산을 대표하는 천년 고찰. 통일대불로 유명해요.",
                "flower-outline", 1));
        nearbySpotRepository.save(spot(
                "팔공산 케이블카 · 하늘정원", "케이블카로 7분이면 정상 전망대까지 오를 수 있어요.",
                "trail-sign-outline", 2));
        nearbySpotRepository.save(spot(
                "갓바위 (관봉 석조여래좌상)", "한 가지 소원은 꼭 들어준다는 팔공산의 명소예요.",
                "sparkles-outline", 3));
        nearbySpotRepository.save(spot(
                "파계사", "조선 영조와 인연이 깊은 고요한 산사예요.",
                "leaf-outline", 4));
        nearbySpotRepository.save(spot(
                "수태골", "맑은 계곡물로 여름 피서지로도 인기가 많아요.",
                "water-outline", 5));
    }

    private NearbySpot spot(String name, String description, String icon, int sortOrder) {
        NearbySpot spot = new NearbySpot();
        spot.setName(name);
        spot.setDescription(description);
        spot.setIcon(icon);
        // 사진은 비워둡니다 — 앱이 요청할 때마다 네이버 이미지 검색으로 실제 사진을
        // 자동으로 가져옵니다(NearbySpotService 참고). 특정 사진을 고정하고 싶으면
        // 이 값에 직접 URL을 넣으면 그게 우선 사용됩니다.
        spot.setImageUrl(null);
        spot.setSortOrder(sortOrder);
        return spot;
    }
}