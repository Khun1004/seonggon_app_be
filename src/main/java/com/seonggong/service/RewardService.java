package com.seonggong.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seonggong.dto.RewardSummaryResponse;
import com.seonggong.entity.RewardRedemption;
import com.seonggong.repository.ReviewRepository;
import com.seonggong.repository.RewardRedemptionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RewardService {

    private static final int REWARD_PER_REVIEW = 1500;

    private final ReviewRepository reviewRepository;
    private final RewardRedemptionRepository redemptionRepository;

    public RewardSummaryResponse getSummary(String loginId) {
        long reviewCount = reviewRepository.countByLoginIdAndRewardEligibleTrue(loginId);
        int earned = (int) (reviewCount * REWARD_PER_REVIEW);

        int spent = redemptionRepository.findByLoginIdOrderByRedeemedAtDesc(loginId).stream()
                .mapToInt(RewardRedemption::getAmount)
                .sum();

        return new RewardSummaryResponse(earned, spent, earned - spent);
    }

    // 화면에 표시된 잔액을 믿지 않고, 사용 처리 직전에 서버가 다시 계산해서 확인합니다.
    // (두 번 연속으로 빠르게 눌러서 잔액 이상으로 사용되는 것을 막기 위함)
    @Transactional
    public RewardSummaryResponse redeem(String loginId, String itemName, int amount) {
        RewardSummaryResponse current = getSummary(loginId);
        if (current.getBalance() < amount) {
            throw new IllegalStateException("적립금 잔액이 부족합니다.");
        }

        RewardRedemption redemption = new RewardRedemption();
        redemption.setLoginId(loginId);
        redemption.setItemName(itemName);
        redemption.setAmount(amount);
        redemptionRepository.save(redemption);

        return getSummary(loginId);
    }
}