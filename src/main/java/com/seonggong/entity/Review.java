package com.seonggong.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 리뷰를 작성한 회원의 아이디 (User.loginId 참조 — 비회원 작성을 막기 위해 필수)
    @Column(nullable = false, length = 50)
    private String loginId;

    // 마이페이지/리뷰 목록에 표시되는 마스킹된 이름 (예: "홍*동")
    @Column(nullable = false, length = 30)
    private String displayName;

    @Column(nullable = false)
    private double rating;

    @Column(nullable = false, length = 2000)
    private String text;

    // 먹은 메뉴 (선택)
    @Column(length = 50)
    private String menuName;

    // 이 리뷰가 어느 예약(방문)에서 작성됐는지 — 예약 내역 화면에서 "이 예약에는
    // 이미 리뷰를 썼는지"를 정확히 확인하는 데 씁니다. 예약과 상관없이 작성된
    // 리뷰(예: 리뷰 탭에서 바로 작성)는 null일 수 있어요.
    private Long reservationId;

    // "이런 점이 좋았어요" 선택 키워드들 (예: "음식이 맛있어요", "친절해요")
    @ElementCollection
    @CollectionTable(name = "review_keywords", joinColumns = @JoinColumn(name = "review_id"))
    @Column(name = "keyword")
    private List<String> keywords = new ArrayList<>();

    // 첨부한 음식 사진들의 서버 저장 경로 또는 URL
    @ElementCollection
    @CollectionTable(name = "review_photos", joinColumns = @JoinColumn(name = "review_id"))
    @Column(name = "photo_url", length = 500)
    private List<String> photos = new ArrayList<>();

    @Column(nullable = false)
    private int likes = 0;

    // 예약 내역 화면의 "리뷰 작성 (1,500원 적립)" 버튼을 통해 작성된 리뷰인지 여부.
    // 이 값이 true인 리뷰만 리뷰 적립금 계산에 포함됩니다 (일반 리뷰 작성과는 구분).
    @Column(nullable = false)
    private boolean rewardEligible = false;

    // 사장님 답변 — 지금은 앱에서 직접 입력하는 화면이 없어서, DB에서 직접
    // UPDATE reviews SET owner_reply = '...', owner_reply_at = NOW() WHERE id = ...
    // 로 입력합니다. 값이 있으면 리뷰 아래에 답변으로 보여줍니다.
    @Column(length = 1000)
    private String ownerReply;

    private LocalDateTime ownerReplyAt;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}