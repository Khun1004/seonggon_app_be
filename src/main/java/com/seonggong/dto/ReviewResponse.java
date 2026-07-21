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
    private double rating;
    private String text;
    private String menuName;
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
        return new ReviewResponse(
                review.getId(),
                review.getDisplayName(),
                avatarUrl,
                review.getRating(),
                review.getText(),
                review.getMenuName(),
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