package com.seonggong.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seonggong.dto.CreateReviewRequest;
import com.seonggong.dto.ReviewResponse;
import com.seonggong.entity.Review;
import com.seonggong.entity.User;
import com.seonggong.repository.ReviewRepository;
import com.seonggong.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public ReviewResponse createReview(CreateReviewRequest request) {
        Review review = new Review();
        review.setLoginId(request.getLoginId());
        review.setDisplayName(request.getDisplayName());
        review.setRating(request.getRating());
        review.setText(request.getText());
        review.setMenuName(request.getMenuName());
        review.setReservationId(request.getReservationId());
        review.setRewardEligible(request.isRewardEligible());

        if (request.getKeywords() != null) {
            review.setKeywords(request.getKeywords());
        }
        if (request.getPhotos() != null) {
            review.setPhotos(request.getPhotos());
        }

        // 사장님이 관리자 화면에서 직접 답변을 남기기 전까지는 답변 없이 둡니다.
        Review saved = reviewRepository.save(review);

        notificationService.create(
                saved.getLoginId(),
                "REVIEW_CREATED",
                "리뷰 작성이 완료되었어요",
                saved.isRewardEligible()
                        ? "소중한 리뷰 감사해요! 1,500원이 적립되었습니다."
                        : "소중한 리뷰 감사해요!",
                "/my-review");

        String avatarUrl = userRepository.findByLoginId(saved.getLoginId())
                .map(u -> u.getAvatarUrl())
                .orElse(null);
        return ReviewResponse.from(saved, avatarUrl);
    }

    // 리뷰 사진 업로드 — base64 이미지를 uploads/reviews/ 폴더에 파일로 저장하고,
    // 그 경로(URL)를 돌려줍니다. 프론트는 이 URL을 모아서 리뷰 등록 때 photos로 보냅니다.
    public String uploadPhoto(String imageBase64) {
        try {
            String pureBase64 = imageBase64.contains(",")
                    ? imageBase64.substring(imageBase64.indexOf(",") + 1)
                    : imageBase64;
            byte[] imageBytes = Base64.getDecoder().decode(pureBase64);

            Path dir = Paths.get("uploads", "reviews");
            Files.createDirectories(dir);

            String fileName = UUID.randomUUID() + ".jpg";
            Path filePath = dir.resolve(fileName);
            Files.write(filePath, imageBytes);

            return "/uploads/reviews/" + fileName;
        } catch (IOException e) {
            throw new IllegalArgumentException("사진 저장에 실패했습니다.");
        }
    }

    public List<ReviewResponse> getAllReviews() {
        List<Review> reviews = reviewRepository.findAllByOrderByCreatedAtDesc();
        Map<String, String> avatarByLoginId = avatarUrlsFor(reviews);
        return reviews.stream()
                .map(r -> ReviewResponse.from(r, avatarByLoginId.get(r.getLoginId())))
                .toList();
    }

    public List<ReviewResponse> getMyReviews(String loginId) {
        List<Review> reviews = reviewRepository.findByLoginIdOrderByCreatedAtDesc(loginId);
        Map<String, String> avatarByLoginId = avatarUrlsFor(reviews);
        return reviews.stream()
                .map(r -> ReviewResponse.from(r, avatarByLoginId.get(r.getLoginId())))
                .toList();
    }

    // 리뷰 여러 개를 한 번에 보여줄 때, 작성자마다 한 번씩 회원 조회하지 않고
    // 한 번에 모아서 조회합니다 (loginId -> avatarUrl 매핑).
    private Map<String, String> avatarUrlsFor(List<Review> reviews) {
        List<String> loginIds = reviews.stream()
                .map(Review::getLoginId)
                .distinct()
                .toList();
        return userRepository.findByLoginIdIn(loginIds).stream()
                .collect(Collectors.toMap(User::getLoginId, u -> u.getAvatarUrl() == null ? "" : u.getAvatarUrl()));
    }

    @Transactional
    public void deleteReview(Long id, String loginId) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));

        if (!review.getLoginId().equals(loginId)) {
            throw new IllegalArgumentException("본인이 작성한 리뷰만 삭제할 수 있습니다.");
        }

        reviewRepository.delete(review);
    }

    // ── 관리자 전용 ──────────────────────────────────────────────
    // 사장님이 관리자 화면에서 직접 답변을 남기거나 수정할 때 이 메서드로 저장합니다.
    @Transactional
    public void setOwnerReply(Long id, String reply) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));
        review.setOwnerReply(reply);
        review.setOwnerReplyAt(LocalDateTime.now());

        // 리뷰를 남긴 손님에게 사장님이 답변을 남겼다고 알려줍니다.
        // 비회원(로그인 없이 작성한) 리뷰는 loginId가 없어서 알림을 보낼 대상이 없어요.
        if (review.getLoginId() != null && !review.getLoginId().isBlank()) {
            notificationService.create(
                    review.getLoginId(),
                    "REVIEW_REPLY",
                    "사장님이 답변을 남겼어요",
                    reply.length() > 60 ? reply.substring(0, 60) + "..." : reply,
                    "/(tabs)/reviews");
        }
    }

    // 예약 시 입력한 전화번호로 가입된 회원이, "리뷰 작성 (1,500원 적립)" 버튼을 통해
    // 적립 대상 리뷰를 하나라도 썼는지 확인합니다. 일반 리뷰 작성은 포함하지 않습니다.
    // (예약별로 정확히 확인하고 싶으면 /api/reviews/me 응답의 reservationId를 프론트에서
    // 직접 비교하세요 — 이 함수는 "예전 방식"과의 호환을 위해 남겨둔 전체 여부 확인이에요.)
    public boolean hasReviewedByPhone(String phone) {
        return userRepository.findByPhone(phone)
                .map(user -> reviewRepository.existsByLoginIdAndRewardEligibleTrue(user.getLoginId()))
                .orElse(false);
    }
}