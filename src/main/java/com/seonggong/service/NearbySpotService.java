package com.seonggong.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.seonggong.dto.NearbySpotResponse;
import com.seonggong.entity.NearbySpot;
import com.seonggong.repository.NearbySpotRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NearbySpotService {

    private final NearbySpotRepository nearbySpotRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${naver.client.id:}")
    private String naverClientId;

    @Value("${naver.client.secret:}")
    private String naverClientSecret;

    // 명소별로 네이버에서 가져온 사진 URL을 잠깐 기억해둡니다(서버 재시작 전까지).
    // 홈 화면을 열 때마다 네이버 API를 다시 부르지 않도록 하기 위한 간단한 캐시예요.
    private final Map<Long, String> imageCache = new ConcurrentHashMap<>();

    public List<NearbySpotResponse> getAll() {
        List<NearbySpot> spots = nearbySpotRepository.findAllByOrderBySortOrderAsc();

        return spots.stream()
                .map(spot -> {
                    // DB에 사장님이 직접 넣어둔 사진이 있으면 그걸 최우선으로 사용
                    String imageUrl = spot.getImageUrl();
                    if (imageUrl == null || imageUrl.isBlank()) {
                        imageUrl = fetchNaverImage(spot);
                    }
                    return new NearbySpotResponse(
                            spot.getId(), spot.getName(), spot.getDescription(),
                            spot.getIcon(), imageUrl);
                })
                .collect(Collectors.toList());
    }

    // 다른 사람들이 올린 실제 게시물의 사진을, 네이버 이미지 검색으로 가져옵니다.
    @SuppressWarnings("unchecked")
    private String fetchNaverImage(NearbySpot spot) {
        if (imageCache.containsKey(spot.getId())) {
            return imageCache.get(spot.getId());
        }
        if (naverClientId.isBlank() || naverClientSecret.isBlank()) {
            return null;
        }

        try {
            String url = UriComponentsBuilder
                    .fromHttpUrl("https://openapi.naver.com/v1/search/image")
                    .queryParam("query", "팔공산 " + spot.getName())
                    .queryParam("display", 1)
                    .queryParam("sort", "sim")
                    .build()
                    .toUriString();

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Naver-Client-Id", naverClientId);
            headers.set("X-Naver-Client-Secret", naverClientSecret);

            HttpEntity<Void> entity = new HttpEntity<>(headers);
            Map<String, Object> body = restTemplate.exchange(
                    url, HttpMethod.GET, entity, Map.class).getBody();

            List<Map<String, Object>> items = body == null
                    ? null
                    : (List<Map<String, Object>>) body.get("items");

            String imageUrl = (items == null || items.isEmpty())
                    ? null
                    : (String) items.get(0).get("link");

            imageCache.put(spot.getId(), imageUrl);
            return imageUrl;

        } catch (Exception e) {
            log.warn("[NearbySpot] '{}' 이미지 검색 실패: {}", spot.getName(), e.getMessage());
            return null;
        }
    }

    // ── 관리자 전용 ──────────────────────────────────────────────

    public java.util.List<NearbySpotResponse> getAllForAdmin() {
        return nearbySpotRepository.findAllByOrderBySortOrderAsc()
                .stream()
                .map(NearbySpotResponse::from)
                .collect(Collectors.toList());
    }

    @org.springframework.transaction.annotation.Transactional
    public NearbySpotResponse createSpot(String name, String description, String icon, String imageUrl, int sortOrder) {
        NearbySpot spot = new NearbySpot();
        spot.setName(name);
        spot.setDescription(description);
        spot.setIcon(icon);
        spot.setImageUrl(imageUrl);
        spot.setSortOrder(sortOrder);
        return NearbySpotResponse.from(nearbySpotRepository.save(spot));
    }

    @org.springframework.transaction.annotation.Transactional
    public NearbySpotResponse updateSpot(Long id, String name, String description, String icon, String imageUrl,
            int sortOrder) {
        NearbySpot spot = nearbySpotRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("명소를 찾을 수 없습니다."));
        spot.setName(name);
        spot.setDescription(description);
        spot.setIcon(icon);
        spot.setImageUrl(imageUrl);
        spot.setSortOrder(sortOrder);
        imageCache.remove(id);
        return NearbySpotResponse.from(spot);
    }

    @org.springframework.transaction.annotation.Transactional
    public void deleteSpot(Long id) {
        if (!nearbySpotRepository.existsById(id)) {
            throw new IllegalArgumentException("명소를 찾을 수 없습니다.");
        }
        nearbySpotRepository.deleteById(id);
        imageCache.remove(id);
    }

    // 리뷰/메뉴 사진과 같은 방식 — base64로 받아서 서버 파일로 저장합니다.
    public String uploadPhoto(String imageBase64) {
        try {
            String pureBase64 = imageBase64.contains(",")
                    ? imageBase64.substring(imageBase64.indexOf(",") + 1)
                    : imageBase64;
            byte[] imageBytes = java.util.Base64.getDecoder().decode(pureBase64);

            java.nio.file.Path dir = java.nio.file.Paths.get("uploads", "spots");
            java.nio.file.Files.createDirectories(dir);

            String fileName = java.util.UUID.randomUUID() + ".jpg";
            java.nio.file.Path filePath = dir.resolve(fileName);
            java.nio.file.Files.write(filePath, imageBytes);

            return "/uploads/spots/" + fileName;
        } catch (java.io.IOException e) {
            throw new IllegalArgumentException("사진 저장에 실패했습니다.");
        }
    }
}