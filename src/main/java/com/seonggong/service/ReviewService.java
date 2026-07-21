package com.seonggong.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Random;
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

    private static final Random RANDOM = new Random();

    // 관리자 화면이 아직 없어서, 리뷰가 등록되면 사장님 답변을 자동으로 붙여드려요.
    // 평점에 따라 톤을 다르게 하고, 메뉴 이름이 있으면 자연스럽게 언급하도록 몇 가지
    // 문구 중 하나를 무작위로 골라서 매번 조금씩 다르게 느껴지도록 했습니다.
    private static final String[] REPLY_TEMPLATES_HIGH = {
            "따뜻한 후기 정말 감사합니다! 다음에 오실 때도 정성껏 준비해드릴게요 :)",
            "좋게 봐주셔서 감사드려요! 앞으로도 한결같은 맛으로 보답하겠습니다.",
            "소중한 시간 내주셔서 리뷰 남겨주신 점 진심으로 감사합니다. 또 뵙겠습니다!",
            "맛있게 드셨다니 저희도 정말 기쁘네요! 다음 방문도 기다리고 있을게요.",
    };
    private static final String[] REPLY_TEMPLATES_LOW = {
            "소중한 의견 감사합니다. 말씀해 주신 부분 참고해서 더 나은 모습으로 보답하겠습니다.",
            "방문해 주시고 솔직한 후기 남겨주셔서 감사해요. 더 신경 써서 준비하겠습니다.",
    };

    private String generateOwnerReply(Review review) {
        String[] templates = review.getRating() >= 4 ? REPLY_TEMPLATES_HIGH : REPLY_TEMPLATES_LOW;
        String base = templates[RANDOM.nextInt(templates.length)];

        if (review.getMenuName() != null && !review.getMenuName().isBlank()) {
            return String.format(
                    "%s님, %s 맛있게 드셔주셔서 감사해요! %s",
                    review.getDisplayName(), review.getMenuName(), base);
        }
        return String.format("%s님, %s", review.getDisplayName(), base);
    }

    @Transactional
    public ReviewResponse createReview(CreateReviewRequest request) {
        Review review = new Review();
        review.setLoginId(request.getLoginId());
        review.setDisplayName(request.getDisplayName());
        review.setRating(request.getRating());
        review.setText(request.getText());
        review.setMenuName(request.getMenuName());
        review.setRewardEligible(request.isRewardEligible());

        if (request.getKeywords() != null) {
            review.setKeywords(request.getKeywords());
        }
        if (request.getPhotos() != null) {
            review.setPhotos(request.getPhotos());
        }

        // 사장님 전용 관리 화면이 없어서, 등록과 동시에 감사 인사를 자동으로 달아드려요.
        review.setOwnerReply(generateOwnerReply(review));
        review.setOwnerReplyAt(LocalDateTime.now());

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
    // 등록 시 자동으로 붙는 감사 인사 대신, 사장님이 직접 답변을 남기고 싶을 때
    // 이 메서드로 덮어씁니다.
    @Transactional
    public void setOwnerReply(Long id, String reply) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));
        review.setOwnerReply(reply);
        review.setOwnerReplyAt(LocalDateTime.now());
    }

    // 예약 시 입력한 전화번호로 가입된 회원이, "리뷰 작성 (1,500원 적립)" 버튼을 통해
    // 적립 대상 리뷰를 하나라도 썼는지 확인합니다. 일반 리뷰 작성은 포함하지 않습니다.
    public boolean hasReviewedByPhone(String phone) {
        return userRepository.findByPhone(phone)
                .map(user -> reviewRepository.existsByLoginIdAndRewardEligibleTrue(user.getLoginId()))
                .orElse(false);
    }
}