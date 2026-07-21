package com.seonggong.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.seonggong.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // 마이페이지 "나의 작성 리뷰" 목록 — 최신순
    List<Review> findByLoginIdOrderByCreatedAtDesc(String loginId);

    // 리뷰 탭의 "방문자 리뷰" 전체 목록 — 최신순
    List<Review> findAllByOrderByCreatedAtDesc();

    // 이 회원이 "적립 대상" 리뷰를 하나라도 썼는지 (예약 내역 화면의 리뷰 유도 버튼 노출 여부에 사용)
    boolean existsByLoginIdAndRewardEligibleTrue(String loginId);

    // 적립금 계산용 — 이 회원의 적립 대상 리뷰 개수
    long countByLoginIdAndRewardEligibleTrue(String loginId);
}