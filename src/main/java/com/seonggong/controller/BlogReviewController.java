package com.seonggong.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/blog-reviews")
public class BlogReviewController {

    @Value("${naver.client.id:}")
    private String naverClientId;

    @Value("${naver.client.secret:}")
    private String naverClientSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String DEFAULT_QUERY = "팔공산 성공식당";

    /**
     * 네이버 블로그 검색 API를 호출해 "팔공산 성공식당" 관련 블로그 글을 가져옵니다.
     * GET /api/blog-reviews?display=10
     */
    @GetMapping
    public ResponseEntity<?> getBlogReviews(
            @RequestParam(name = "display", defaultValue = "10") int display) {

        if (naverClientId.isBlank() || naverClientSecret.isBlank()) {
            return ResponseEntity.status(503).body(Map.of(
                    "message", "네이버 API 키가 설정되지 않았습니다."));
        }

        try {
            String url = UriComponentsBuilder
                    .fromHttpUrl("https://openapi.naver.com/v1/search/blog.json")
                    .queryParam("query", DEFAULT_QUERY)
                    .queryParam("display", Math.min(display, 30))
                    .queryParam("sort", "sim") // 정확도순
                    .build()
                    .toUriString();

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Naver-Client-Id", naverClientId);
            headers.set("X-Naver-Client-Secret", naverClientSecret);

            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.exchange(
                    url, org.springframework.http.HttpMethod.GET, entity, Map.class);

            List<Map<String, Object>> items = (List<Map<String, Object>>) response.getBody().get("items");

            List<Map<String, Object>> result = new ArrayList<>();
            if (items != null) {
                for (Map<String, Object> item : items) {
                    result.add(Map.of(
                            // 네이버 검색 결과의 <b> 태그(강조 표시)를 제거해서 순수 텍스트로 변환
                            "title", stripHtml((String) item.get("title")),
                            "snippet", stripHtml((String) item.get("description")),
                            "blogName", (String) item.get("bloggername"),
                            "link", (String) item.get("link"),
                            "postDate", formatDate((String) item.get("postdate"))));
                }
            }

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("[BlogReview] 네이버 검색 오류: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of(
                    "message", "블로그 리뷰를 가져오지 못했습니다."));
        }
    }

    private String stripHtml(String text) {
        if (text == null)
            return "";
        return text.replaceAll("<[^>]*>", "");
    }

    // 네이버 API는 postdate를 "20260615" 형태로 줍니다 -> "2026.06.15"로 변환
    private String formatDate(String postdate) {
        if (postdate == null || postdate.length() != 8)
            return "";
        return postdate.substring(0, 4) + "." + postdate.substring(4, 6) + "." + postdate.substring(6, 8);
    }
}