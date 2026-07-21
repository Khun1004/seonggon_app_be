package com.seonggong.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.seonggong.dto.GenerateReviewRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/review-ai")
@RequiredArgsConstructor
public class ReviewAiController {

    @Value("${google.gemini.api.key:}")
    private String geminiApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String GEMINI_MODEL = "gemini-2.5-flash";

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

    // ── AI 리뷰 문장 생성 ──────────────────────────────────
    @PostMapping("/generate-review")
    public ResponseEntity<?> generateReview(@RequestBody GenerateReviewRequest request) {
        String key = geminiApiKey == null ? "" : geminiApiKey.trim();
        if (key.isBlank()) {
            return ResponseEntity.status(503).body(Map.of("message", "AI 키가 설정되지 않았습니다."));
        }

        try {
            StringBuilder context = new StringBuilder();
            context.append("별점: ").append(request.getRating()).append("점/5점\n");
            if (request.getMenuName() != null && !request.getMenuName().isBlank()) {
                context.append("먹은 메뉴: ").append(request.getMenuName()).append("\n");
            }
            if (request.getGoodPoints() != null && !request.getGoodPoints().isEmpty()) {
                context.append("좋았던 점: ")
                        .append(String.join(", ", request.getGoodPoints()))
                        .append("\n");
            }
            if (request.getTaste() != null && !request.getTaste().isBlank()) {
                context.append("맛: ").append(request.getTaste()).append("\n");
            }
            if (request.getMood() != null && !request.getMood().isBlank()) {
                context.append("분위기: ").append(request.getMood()).append("\n");
            }
            if (request.getService() != null && !request.getService().isBlank()) {
                context.append("서비스: ").append(request.getService()).append("\n");
            }

            String prompt = """
                    당신은 음식점 방문 후기를 작성하는 손님입니다. 아래 정보를 바탕으로
                    자연스럽고 진짜 사람이 쓴 것 같은 방문자 리뷰를 작성해 주세요.

                    매장: 팔공산 성공식당
                    %s

                    조건:
                    - 2~4문장 정도의 자연스러운 한국어 후기
                    - 과장되지 않고 솔직한 어투
                    - 별점이 높으면 긍정적으로, 낮으면 아쉬운 점도 담아서
                    - 이모지는 사용하지 않음
                    - 리뷰 본문만 출력하고 다른 설명은 절대 추가하지 마세요
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
                            "temperature", 0.8,
                            "maxOutputTokens", 300));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            String reviewText = extractGeminiText(response.getBody()).trim();

            return ResponseEntity.ok(Map.of("reviewText", reviewText));

        } catch (Exception e) {
            log.error("[ReviewAi] 리뷰 생성 오류: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of(
                    "message", "AI 리뷰 생성에 실패했습니다. 다시 시도해 주세요."));
        }
    }
}