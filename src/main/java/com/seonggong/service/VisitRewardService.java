package com.seonggong.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seonggong.dto.VisitRewardClaimResponse;
import com.seonggong.dto.VisitStampStatusResponse;
import com.seonggong.entity.Reservation;
import com.seonggong.entity.VisitRewardClaim;
import com.seonggong.repository.ReservationRepository;
import com.seonggong.repository.VisitRewardClaimRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VisitRewardService {

    private static final String CONFIRMED = "CONFIRMED";
    private static final String TAKEOUT = "TAKEOUT";

    private final ReservationRepository reservationRepository;
    private final VisitRewardClaimRepository visitRewardClaimRepository;
    private final VisitStampSettingsService visitStampSettingsService;

    @Transactional(readOnly = true)
    public VisitStampStatusResponse getStatus(String phone, String loginId) {
        int totalStamps = visitStampSettingsService.getOrCreate().getRequiredVisits();
        int count = countEligibleVisits(phone, loginId);
        LocalDateTime lastClaimedAt = getLastClaimedAt(loginId);

        int visitCount = Math.min(count, totalStamps);
        return new VisitStampStatusResponse(
                visitCount,
                totalStamps,
                visitCount >= totalStamps,
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

    // 지난 사용 내역을 시간 순으로 조회하면서, 각 회차마다 "그때 어떤 방문들이
    // 도장으로 쓰였는지"를 같이 계산해줍니다. 예를 들어 2번째로 혜택을 받았다면,
    // 1번째 혜택을 받은 시점 ~ 2번째 혜택을 받은 시점 사이의 방문 중 앞의
    // (그 당시 설정된 개수)가 그 회차의 도장이었던 거예요.
    @Transactional(readOnly = true)
    public List<VisitRewardClaimResponse> getHistory(String phone, String loginId) {
        if (loginId == null || loginId.isBlank() || phone == null || phone.isBlank()) {
            return List.of();
        }

        int totalStamps = visitStampSettingsService.getOrCreate().getRequiredVisits();

        List<VisitRewardClaim> claimsDesc = visitRewardClaimRepository.findAllByLoginIdOrderByClaimedAtDesc(loginId);
        if (claimsDesc.isEmpty()) {
            return List.of();
        }

        List<Reservation> confirmedDineInAsc = reservationRepository.findByPhoneOrderByCreatedAtDesc(phone)
                .stream()
                .filter(r -> CONFIRMED.equals(r.getStatus()))
                .filter(r -> !TAKEOUT.equals(r.getType()))
                .sorted(Comparator.comparing(Reservation::getCreatedAt))
                .toList();

        List<VisitRewardClaim> claimsAsc = new ArrayList<>(claimsDesc);
        Collections.reverse(claimsAsc);

        List<VisitRewardClaimResponse> resultAsc = new ArrayList<>();
        LocalDateTime windowStart = null;
        for (VisitRewardClaim claim : claimsAsc) {
            LocalDateTime windowEnd = claim.getClaimedAt();
            LocalDateTime finalStart = windowStart;
            List<String> visitDates = confirmedDineInAsc.stream()
                    .filter(r -> finalStart == null || r.getCreatedAt().isAfter(finalStart))
                    .filter(r -> !r.getCreatedAt().isAfter(windowEnd))
                    .limit(totalStamps)
                    .map(Reservation::getDate)
                    .map(Object::toString)
                    .toList();
            resultAsc.add(new VisitRewardClaimResponse(
                    claim.getId(),
                    claim.getClaimedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    visitDates));
            windowStart = windowEnd;
        }

        Collections.reverse(resultAsc);
        return resultAsc;
    }

    @Transactional
    public VisitStampStatusResponse claim(String phone, String loginId) {
        if (loginId == null || loginId.isBlank()) {
            throw new IllegalStateException("로그인 후 이용해 주세요.");
        }
        int totalStamps = visitStampSettingsService.getOrCreate().getRequiredVisits();
        int count = countEligibleVisits(phone, loginId);
        if (count < totalStamps) {
            throw new IllegalStateException("아직 방문 도장이 다 채워지지 않았어요.");
        }

        VisitRewardClaim claim = new VisitRewardClaim();
        claim.setLoginId(loginId);
        claim.setClaimedAt(LocalDateTime.now());
        visitRewardClaimRepository.save(claim);

        return getStatus(phone, loginId);
    }
}