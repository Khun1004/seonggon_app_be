package com.seonggong.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.seonggong.entity.VisitRewardClaim;

public interface VisitRewardClaimRepository extends JpaRepository<VisitRewardClaim, Long> {

    Optional<VisitRewardClaim> findTopByLoginIdOrderByClaimedAtDesc(String loginId);
}