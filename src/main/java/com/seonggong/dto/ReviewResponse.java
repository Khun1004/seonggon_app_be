package com.seonggong.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.seonggong.entity.Review;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private Long id;
    private String displayName;
    private String avatarUrl;
    // 메뉴별 별점의 평균 — 카드 맨 위 "전체 별점"으로 그대로 보여주면 돼요.
    private double rating;
    private String text;
    // 메뉴 하나당 별점 하나씩 — 손님이 여러 메뉴를 골랐으면 여러 개 들어있어요.
    private List<MenuRatingItem> menuRatings;
    private Long reservationId;
    private List<String> keywords;
    private List<String> photos;
    private int likes;
    private boolean rewardEligible;
    private String ownerReply;
    private Long ownerReplyAt;
    private LocalDateTime createdAt;

    // avatarUrl은 Review 엔티티에 없는 값이라(작성자 User 계정에 있는 값),
    // 서비스 쪽에서 조회해서 별도로 넘겨줍니다.
    public static ReviewResponse from(Review review, String avatarUrl) {
        List<MenuRatingItem> items = review.getMenuRatings().stream()
                .map(m -> new MenuRatingItem(m.getMenuName(), m.getRating()))
                .toList();
        return new ReviewResponse(
                review.getId(),
                review.getDisplayName(),
                avatarUrl,
                review.getRating(),
                review.getText(),
                items,
                review.getReservationId(),
                review.getKeywords(),
                review.getPhotos(),
                review.getLikes(),
                review.isRewardEligible(),
                review.getOwnerReply(),
                review.getOwnerReplyAt() == null
                        ? null
                        : review.getOwnerReplyAt()
                                .atZone(java.time.ZoneId.systemDefault())
                                .toInstant()
                                .toEpochMilli(),
                review.getCreatedAt());
    }
}