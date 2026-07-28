package com.seonggong.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seonggong.dto.VisitStampStatusResponse;
import com.seonggong.entity.Reservation;
import com.seonggong.entity.VisitRewardClaim;
import com.seonggong.repository.ReservationRepository;
import com.seonggong.repository.VisitRewardClaimRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VisitRewardService {

    private static final int TOTAL_STAMPS = 5;
    private static final String CONFIRMED = "CONFIRMED";
    private static final String TAKEOUT = "TAKEOUT";

    private final ReservationRepository reservationRepository;
    private final VisitRewardClaimRepository visitRewardClaimRepository;

    @Transactional(readOnly = true)
    public VisitStampStatusResponse getStatus(String phone, String loginId) {
        int count = countEligibleVisits(phone, loginId);
        LocalDateTime lastClaimedAt = getLastClaimedAt(loginId);

        int visitCount = Math.min(count, TOTAL_STAMPS);
        return new VisitStampStatusResponse(
                visitCount,
                TOTAL_STAMPS,
                visitCount >= TOTAL_STAMPS,
                lastClaimedAt == null ? null : lastClaimedAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    private LocalDateTime getLastClaimedAt(String loginId) {
        if (loginId == null || loginId.isBlank()) {
            return null;
        }
        return visitRewardClaimRepository.findTopByLoginIdOrderByClaimedAtDesc(loginId)
                .map(VisitRewardClaim::getClaimedAt)
                .orElse(null);
    }

    // "확정된 방문(포장 제외) 예약" 중, 마지막으로 혜택을 받은 시점 이후에
    // 만들어진 예약만 셉니다. 혜택을 한 번 받으면 그 이전 방문들은 이번
    // 회차 도장판에서 빠지게 돼요.
    private int countEligibleVisits(String phone, String loginId) {
        List<Reservation> reservations = reservationRepository.findByPhoneOrderByCreatedAtDesc(phone);
        LocalDateTime lastClaimedAt = getLastClaimedAt(loginId);

        return (int) reservations.stream()
                .filter(r -> CONFIRMED.equals(r.getStatus()))
                .filter(r -> !TAKEOUT.equals(r.getType()))
                .filter(r -> lastClaimedAt == null || r.getCreatedAt().isAfter(lastClaimedAt))
                .count();
    }

    @Transactional
    public VisitStampStatusResponse claim(String phone, String loginId) {
        if (loginId == null || loginId.isBlank()) {
            throw new IllegalStateException("로그인 후 이용해 주세요.");
        }
        int count = countEligibleVisits(phone, loginId);
        if (count < TOTAL_STAMPS) {
            throw new IllegalStateException("아직 방문 도장이 다 채워지지 않았어요.");
        }

        VisitRewardClaim claim = new VisitRewardClaim();
        claim.setLoginId(loginId);
        claim.setClaimedAt(LocalDateTime.now());
        visitRewardClaimRepository.save(claim);

        return getStatus(phone, loginId);
    }
}