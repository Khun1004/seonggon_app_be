package com.seonggong.service;

import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seonggong.config.TossProperties;
import com.seonggong.dto.PayReservationRequest;
import com.seonggong.dto.ReservationResponse;
import com.seonggong.entity.Reservation;
import com.seonggong.repository.ReservationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TossPaymentService {

    private final TossProperties tossProperties;
    private final ReservationRepository reservationRepository;
    private final ReservationService reservationService;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // orderId는 토스에 "무작위이고 예측 불가능한 값"이어야 해서, 뒤에 랜덤 문자열을 붙입니다.
    // 나중에 성공 콜백에서 이 orderId를 보고 어떤 예약(Reservation)인지 다시 찾아내야 해서
    // 앞부분에 "reservation-{id}-" 형식으로 예약 id를 심어둡니다.
    public String buildOrderId(Long reservationId) {
        return "reservation-" + reservationId + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private Long parseReservationId(String orderId) {
        try {
            String[] parts = orderId.split("-");
            return Long.parseLong(parts[1]);
        } catch (Exception e) {
            throw new IllegalArgumentException("잘못된 주문번호입니다: " + orderId);
        }
    }

    // 토스 결제 승인(confirm) API를 호출하고, 성공하면 그 예약을 "결제완료"로 표시합니다.
    @Transactional
    public ReservationResponse confirmPayment(String paymentKey, String orderId, int amount) {
        Long reservationId = parseReservationId(orderId);

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다."));

        String secretKey = tossProperties.getSecretKey();
        String url = "https://api.tosspayments.com/v1/payments/confirm";
        String encodedAuth = Base64.getEncoder().encodeToString((secretKey + ":").getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Basic " + encodedAuth);

        Map<String, Object> body = new HashMap<>();
        body.put("paymentKey", paymentKey);
        body.put("orderId", orderId);
        body.put("amount", amount);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        JsonNode tossResponse;
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            tossResponse = objectMapper.readTree(response.getBody());
        } catch (HttpClientErrorException e) {
            log.warn("[Toss] 결제 승인 실패: {}", e.getResponseBodyAsString());
            throw new IllegalStateException("결제 승인에 실패했습니다: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new IllegalStateException("결제 승인 처리 중 오류가 발생했습니다.");
        }

        String method = tossResponse.path("method").asText("토스페이먼츠");

        PayReservationRequest payRequest = new PayReservationRequest();
        payRequest.setPaymentMethod(method);
        payRequest.setAmount(amount);

        return reservationService.pay(reservationId, payRequest);
    }
}