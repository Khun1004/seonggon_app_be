package com.seonggong.service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seonggong.dto.ClosedDateResponse;
import com.seonggong.dto.CreateClosedDateRequest;
import com.seonggong.dto.StoreProfileResponse;
import com.seonggong.dto.UpsertStoreProfileRequest;
import com.seonggong.entity.ClosedDate;
import com.seonggong.entity.Review;
import com.seonggong.entity.StoreProfile;
import com.seonggong.repository.ClosedDateRepository;
import com.seonggong.repository.ReviewRepository;
import com.seonggong.repository.StoreProfileRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreProfileService {

    private final StoreProfileRepository storeProfileRepository;
    private final ClosedDateRepository closedDateRepository;
    private final ReviewRepository reviewRepository;

    // 딱 한 줄(id=1)만 쓰는 설정 테이블이라, 없으면 기본값으로 하나 만들어서 돌려줍니다.
    @Transactional
    public StoreProfile getOrCreateProfile() {
        return storeProfileRepository.findById(1L)
                .orElseGet(() -> storeProfileRepository.save(new StoreProfile()));
    }

    public StoreProfileResponse getProfile() {
        return StoreProfileResponse.from(getOrCreateProfile());
    }

    @Transactional
    public StoreProfileResponse updateProfile(UpsertStoreProfileRequest request) {
        StoreProfile profile = getOrCreateProfile();
        profile.setAddress(request.getAddress());
        profile.setPhone(request.getPhone());
        profile.setOpenTime(request.getOpenTime());
        profile.setCloseTime(request.getCloseTime());
        profile.setLastOrderTime(request.getLastOrderTime());
        profile.setNaverRating(request.getNaverRating());
        profile.setNaverReviewCount(request.getNaverReviewCount());
        profile.setBlogReviewCount(request.getBlogReviewCount());
        return StoreProfileResponse.from(profile);
    }

    // 손님 화면 — 오늘부터 앞으로의 휴무일만 (지난 휴무일은 안 보여줘도 되니까)
    public List<ClosedDateResponse> getUpcomingClosedDates() {
        return closedDateRepository.findByDateGreaterThanEqualOrderByDateAsc(LocalDate.now())
                .stream()
                .map(ClosedDateResponse::from)
                .toList();
    }

    // 관리자 화면 — 지난 휴무일도 포함해서 전체
    public List<ClosedDateResponse> getAllClosedDatesForAdmin() {
        return closedDateRepository.findAllByOrderByDateAsc()
                .stream()
                .map(ClosedDateResponse::from)
                .toList();
    }

    @Transactional
    public ClosedDateResponse addClosedDate(CreateClosedDateRequest request) {
        LocalDate date = LocalDate.parse(request.getDate());
        if (closedDateRepository.existsByDate(date)) {
            throw new IllegalArgumentException("이미 휴무일로 등록된 날짜예요.");
        }
        ClosedDate closedDate = new ClosedDate();
        closedDate.setDate(date);
        closedDate.setReason(request.getReason());
        return ClosedDateResponse.from(closedDateRepository.save(closedDate));
    }

    @Transactional
    public void removeClosedDate(Long id) {
        if (!closedDateRepository.existsById(id)) {
            throw new IllegalArgumentException("휴무일을 찾을 수 없습니다.");
        }
        closedDateRepository.deleteById(id);
    }

    // "리뷰 및 별점" 요약 — 실제 리뷰 데이터에서 평균/개수/평점 높은 리뷰 2개를 뽑아줍니다.
    public Map<String, Object> getReviewStats() {
        List<Review> reviews = reviewRepository.findAllByOrderByCreatedAtDesc();

        double average = reviews.isEmpty()
                ? 0
                : reviews.stream().mapToDouble(Review::getRating).average().orElse(0);

        List<String> highlightQuotes = reviews.stream()
                .filter(r -> r.getRating() >= 4 && r.getText() != null && !r.getText().isBlank())
                .sorted(Comparator.comparing(Review::getCreatedAt).reversed())
                .limit(2)
                .map(Review::getText)
                .collect(Collectors.toList());

        return Map.of(
                "averageRating", Math.round(average * 100) / 100.0,
                "totalCount", reviews.size(),
                "highlightQuotes", highlightQuotes);
    }
}