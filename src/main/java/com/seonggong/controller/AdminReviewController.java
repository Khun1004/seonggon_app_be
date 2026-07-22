package com.seonggong.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.seonggong.dto.ReviewResponse;
import com.seonggong.entity.Review;
import com.seonggong.repository.ReviewRepository;
import com.seonggong.service.AdminAuthService;
import com.seonggong.service.ReviewService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// 사장님 전용 — 리뷰에 직접 답변을 남기거나 자동 생성된 답변을 바꿉니다.
@Slf4j
@RestController
@RequestMapping("/api/admin/reviews")
@RequiredArgsConstructor
public class AdminReviewController {

    private final AdminAuthService adminAuthService;
    private final ReviewService reviewService;
    private final ReviewRepository reviewRepository;

    @Value("${google.gemini.api.key:}")
    private String geminiApiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String GEMINI_MODEL = "gemini-2.5-flash";

    @GetMapping
    public ResponseEntity<?> getAllReviews(
            @RequestHeader("X-Admin-Password") String adminPassword) {
        try {
            adminAuthService.requireAdmin(adminPassword);
            List<ReviewResponse> reviews = reviewService.getAllReviews();
            return ResponseEntity.ok(reviews);
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/reply")
    public ResponseEntity<?> setOwnerReply(
            @PathVariable("id") Long id,
            @RequestHeader("X-Admin-Password") String adminPassword,
            @RequestBody Map<String, String> body) {
        try {
            adminAuthService.requireAdmin(adminPassword);
            reviewService.setOwnerReply(id, body.get("reply"));
            return ResponseEntity.ok(Map.of("message", "답변이 저장되었습니다."));
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        }
    }

    // AI(Gemini)로 이 리뷰에 어울리는 답변 초안을 만들어줍니다. 저장은 안 하고
    // 초안 문구만 돌려줘요 — 사장님이 확인하고 고쳐서 저장하시면 됩니다.
    @PostMapping("/{id}/generate-reply")
    public ResponseEntity<?> generateReply(
            @PathVariable("id") Long id,
            @RequestHeader("X-Admin-Password") String adminPassword) {
        try {
            adminAuthService.requireAdmin(adminPassword);
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
        }

        String key = geminiApiKey == null ? "" : geminiApiKey.trim();
        if (key.isBlank()) {
            return ResponseEntity.status(503).body(Map.of("message", "AI 키가 설정되지 않았습니다."));
        }

        Review review = reviewRepository.findById(id).orElse(null);
        if (review == null) {
            return ResponseEntity.status(404).body(Map.of("message", "리뷰를 찾을 수 없습니다."));
        }

        try {
            StringBuilder context = new StringBuilder();
            context.append("손님 이름: ").append(review.getDisplayName()).append("\n");
            context.append("별점: ").append(review.getRating()).append("점/5점\n");
            if (review.getMenuName() != null && !review.getMenuName().isBlank()) {
                context.append("주문 메뉴: ").append(review.getMenuName()).append("\n");
            }
            context.append("리뷰 내용: ").append(review.getText()).append("\n");

            String prompt = """
                    당신은 "팔공산 성공식당"의 사장님입니다. 아래 손님 리뷰를 읽고,
                    손님에게 남길 답글을 작성해 주세요.

                    %s

                    조건:
                    - 2~3문장 정도의 따뜻하고 진심 어린 한국어 답글
                    - 별점이 높으면 감사 인사 위주로, 낮으면 죄송함과 개선 의지를 담아서
                    - 리뷰에서 언급된 구체적인 내용(메뉴, 서비스 등)이 있으면 자연스럽게 언급
                    - 손님 이름을 답글 시작에 자연스럽게 넣어주세요 (예: "OOO님, ")
                    - 과도한 이모지는 사용하지 않음
                    - 답글 본문만 출력하고 다른 설명은 절대 추가하지 마세요
                    """
                    .formatted(context.toString());

            String url = "https://generativelanguage.googleapis.com/v1beta/models/"
                    + GEMINI_MODEL + ":generateContent?key=" + key;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = Map.of(
                    "contents", List.of(Map.of(
                            "role", "user",
                            "parts", List.of(Map.of("text", prompt)))),
                    "generationConfig", Map.of(
                            "temperature", 0.7,
                            "maxOutputTokens", 300));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            String replyText = extractGeminiText(response.getBody()).trim();
            return ResponseEntity.ok(Map.of("reply", replyText));
        } catch (Exception e) {
            log.error("[AdminReviewAi] 답변 생성 오류: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of(
                    "message", "AI 답변 생성에 실패했습니다. 다시 시도해 주세요."));
        }
    }

    @SuppressWarnings("unchecked")
    private String extractGeminiText(Map<String, Object> respBody) {
        if (respBody == null)
            return "";
        List<?> candidates = (List<?>) respBody.get("candidates");
        if (candidates == null || candidates.isEmpty())
            return "";
        Map<?, ?> first = (Map<?, ?>) candidates.get(0);
        Map<?, ?> contentMap = (Map<?, ?>) first.get("content");
        if (contentMap == null)
            return "";
        List<?> parts = (List<?>) contentMap.get("parts");
        if (parts == null || parts.isEmpty())
            return "";
        Map<?, ?> part = (Map<?, ?>) parts.get(0);
        return String.valueOf(part.get("text"));
    }
}