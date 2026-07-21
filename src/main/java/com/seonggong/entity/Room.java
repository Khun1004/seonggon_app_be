package com.seonggong.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

// 좌석/룸 하나(1층 홀 좌석, 프라이빗 룸, 2층 단체 룸 등)를 나타냅니다.
// 예전에는 앱 코드 안에 고정값으로 박혀 있던 데이터였는데, 사장님이 앱에서
// 직접 인원수/설명/사진을 바꾸실 수 있도록 DB로 옮겼습니다.
@Entity
@Table(name = "rooms")
@Getter
@Setter
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 예약(Reservation.roomId)에 저장되는 고유 문자열 키 — 예: "hall-1", "small-15".
    // 예약 데이터가 이 값으로 좌석을 참조하고 있어서, 기존 좌석은 절대 바뀌면 안 돼요.
    @Column(nullable = false, unique = true, length = 50)
    private String roomKey;

    // 화면에 보여줄 번호 (예: "1", "15")
    @Column(nullable = false, length = 10)
    private String number;

    @Column(nullable = false)
    private int floor;

    // "hall" / "small" / "medium" / "large" / "group_room" / "group_large" /
    // "group_hall"
    @Column(nullable = false, length = 20)
    private String category;

    // 화면에 보여줄 카테고리 이름 (예: "일반 홀 좌석")
    @Column(nullable = false, length = 30)
    private String categoryLabel;

    // 화면에 그대로 보여줄 인원수 문자열 (예: "4명 ~ 8명")
    @Column(nullable = false, length = 20)
    private String capacity;

    // true면 독립된 룸(신발 벗고 들어가는 등), false면 오픈된 홀 좌석
    @Column(nullable = false)
    private boolean isRoom;

    @Column(length = 200)
    private String note;

    // 사장님이 사진을 새로 업로드하면 여기 서버 경로가 들어갑니다.
    // 아직 업로드 안 한 예전 좌석은 null이고, 이 경우 앱이 기존 기본 사진을 보여줍니다.
    @Column(length = 300)
    private String imageUrl;

    // 같은 카테고리 안에서 보여줄 순서
    @Column(nullable = false)
    private int displayOrder = 0;

    // 삭제 대신 숨김 처리 — 예약 등 다른 곳에서 이 좌석 키를 참조하고 있을 수 있어서,
    // 진짜로 지우지 않고 예약 화면 목록에서만 안 보이게 합니다.
    @Column(nullable = false)
    private boolean active = true;
}