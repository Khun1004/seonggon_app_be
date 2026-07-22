package com.seonggong.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

// "정보" 화면에 나오는 안내 문구 하나 (소개, 1층 안내, 2층 안내, 주차 안내,
// 오시는 길, 대중교통 등)를 나타냅니다. 예전에는 앱 코드 안에 고정 텍스트로
// 있던 내용인데, 사장님이 앱에서 직접 자유롭게 추가/수정할 수 있도록
// "그룹 + 제목 + 내용" 형태의 범용 블록으로 만들었어요.
@Entity
@Table(name = "store_info_sections")
@Getter
@Setter
public class StoreInfoSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 화면의 어느 큰 섹션에 속하는지 — "intro"(소개) / "taste"(맛) /
    // "seats"(좌석 및 공간, 1층·2층 안내) / "directions"(오시는 길 및 주차)
    // 컬럼 이름은 "group"이 아니라 "section_group"으로 지정합니다 — GROUP은
    // SQL 예약어라서 그대로 쓰면 테이블 생성이 조용히 실패할 수 있어요.
    @Column(name = "section_group", nullable = false, length = 30)
    private String group;

    @Column(nullable = false, length = 50)
    private String title;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // Ionicons 이름 (선택) — 소제목 옆에 작은 아이콘으로 보여줄 때 사용
    @Column(length = 50)
    private String icon;

    // 사장님이 사진을 올리면 여기 서버 경로가 들어갑니다 (예: 주차장 사진).
    // 필수는 아니라서 사진 없는 안내 문구도 그대로 잘 동작해요.
    @Column(length = 300)
    private String imageUrl;

    @Column(nullable = false)
    private int displayOrder = 0;

    @Column(nullable = false)
    private boolean active = true;
}