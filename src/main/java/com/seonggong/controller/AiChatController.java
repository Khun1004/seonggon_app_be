package com.seonggong.controller;

import java.util.ArrayList;
import java.util.HashMap;
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

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/ai")
public class AiChatController {

    @Value("${google.gemini.api.key:}")
    private String geminiApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public record ChatRequest(List<Map<String, String>> messages) {
    }

    // 성공식당에 대한 정보를 시스템 프롬프트로 미리 알려줘서, 매장 관련 질문에 정확히 답하게 합니다.
    private static final String SYSTEM_PROMPT = """
            당신은 '팔공산 성공식당'의 AI 상담원입니다. 다음 정보를 바탕으로 친절하고 간결하게 답변해 주세요.

            - 영업시간: 매일 11:00 ~ 21:00, 라스트오더 19:30
            - 위치: 대구 동구 팔공산로199길 12
            - 전화번호: 0507-1410-7634
            - 주차: 매장 앞 약 30대 무료 주차 가능
            - 대표 메뉴: 능이오리백숙, 산더미 오리간장불고기, 해물파전
            - 좌석: 룸(2~80명), 단체석(8~80명), 홀 입식 좌석
            - 예약: 앱 내 '예약' 메뉴에서 가능, 주말/공휴일 단체는 전화 예약 권장
            - 리뷰 작성 시 음료/사이드 쿠폰 제공
            - 안심식당 인증 매장, 지역화폐/제로페이 결제 가능

            답변은 2~3문장 이내로 짧고 친근하게 해주세요. 모르는 정보는 추측하지 말고
            "정확한 안내를 위해 매장으로 전화 문의해 주세요 (0507-1410-7634)"라고 안내해 주세요.
            """;

    @PostMapping("/chat")
    public ResponseEntity<?> chat(@RequestBody ChatRequest req) {

        String key = geminiApiKey == null ? "" : geminiApiKey.trim();
        if (key.isBlank()) {
            return ResponseEntity.status(503)
                    .body(Map.of("message", "Gemini API 키가 설정되지 않았습니다."));
        }

        try {
            // ── 대화 내역을 Gemini contents 형식으로 변환 ──────────
            List<Map<String, Object>> contents = new ArrayList<>();

            // 시스템 프롬프트를 첫 user/model 턴으로 삽입
            contents.add(Map.of(
                    "role", "user",
                    "parts", List.of(Map.of("text", SYSTEM_PROMPT))));
            contents.add(Map.of(
                    "role", "model",
                    "parts", List.of(Map.of("text",
                            "네, 알겠습니다! 성공식당 AI 상담원으로서 친절하게 안내해 드릴게요."))));

            if (req.messages() != null) {
                for (Map<String, String> msg : req.messages()) {
                    String role = msg.get("role");
                    String content = msg.get("content");
                    if (content == null || content.isBlank())
                        continue;

                    // Gemini는 user / model 만 허용
                    String geminiRole = "assistant".equals(role) ? "model" : "user";
                    contents.add(Map.of(
                            "role", geminiRole,
                            "parts", List.of(Map.of("text", content))));
                }
            }

            // 마지막이 user 턴이어야 함
            if (contents.isEmpty() ||
                    !"user".equals(contents.get(contents.size() - 1).get("role"))) {
                contents.add(Map.of(
                        "role", "user",
                        "parts", List.of(Map.of("text", "안녕하세요"))));
            }

            // ── Gemini API 호출 ────────────────────────────────────
            String url = "https://generativelanguage.googleapis.com/v1beta/models/" +
                    "gemini-2.5-flash:generateContent?key=" + key;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = new HashMap<>();
            body.put("contents", contents);
            body.put("generationConfig", Map.of(
                    "maxOutputTokens", 300,
                    "temperature", 0.7));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            // ── 응답 파싱 ──────────────────────────────────────────
            String replyText = "";
            Map<String, Object> respBody = response.getBody();
            if (respBody != null) {
                List<?> candidates = (List<?>) respBody.get("candidates");
                if (candidates != null && !candidates.isEmpty()) {
                    Map<?, ?> first = (Map<?, ?>) candidates.get(0);
                    Map<?, ?> contentMap = (Map<?, ?>) first.get("content");
                    if (contentMap != null) {
                        List<?> parts = (List<?>) contentMap.get("parts");
                        if (parts != null && !parts.isEmpty()) {
                            Map<?, ?> part = (Map<?, ?>) parts.get(0);
                            replyText = String.valueOf(part.get("text"));
                        }
                    }
                }
            }

            log.info("[AiChat] Gemini 응답 완료");
            return ResponseEntity.ok(Map.of("reply", replyText));

        } catch (org.springframework.web.client.HttpClientErrorException e) {
            log.error("[AiChat] Gemini HTTP 오류: {}", e.getStatusCode());
            log.error("[AiChat] 응답: {}", e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode())
                    .body(Map.of("message", "AI 응답 처리 중 오류가 발생했습니다."));
        } catch (Exception e) {
            log.error("[AiChat] 예외: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(Map.of("message", "AI 응답을 받지 못했습니다. 잠시 후 다시 시도해 주세요."));
        }
    }
}