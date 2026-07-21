package com.seonggong.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "reservations")
@Getter
@Setter
@NoArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // constants/rooms-data.ts의 room id (예: "large-7", "2f-6"). 포장(TAKEOUT)은 방이 없어서
    // "takeout" 고정값을 씁니다.
    @Column(nullable = false, length = 30)
    private String roomId;

    // 화면에 보여줄 자리 이름 (예: "프라이빗 룸 (대형) 7번"). 포장이면 "포장 주문".
    @Column(nullable = false, length = 100)
    private String roomLabel;

    // DINE_IN(매장 방문) / TAKEOUT(포장) — 포장은 자리를 안 쓰기 때문에
    // 같은 시간에 여러 명이 겹쳐도 되고, 좌석 중복 확인 대상에서 제외됩니다.
    @Column(nullable = false, length = 20)
    private String type = "DINE_IN";

    @Column(nullable = false)
    private LocalDate date;

    // 고정 슬롯 문자열 (예: "18:30")
    @Column(nullable = false, length = 10)
    private String time;

    @Column(nullable = false, length = 30)
    private String name;

    // 예약을 만든 회원의 로그인 아이디 — 알림을 보낼 대상을 찾을 때 씁니다.
    // 예약은 이제 로그인해야만 할 수 있어서 항상 값이 채워지지만, 혹시 모를
    // 예전 데이터(로그인 없이 만들어졌던 예약)를 위해 nullable로 둡니다.
    @Column(length = 50)
    private String loginId;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(nullable = false)
    private int peopleCount;

    // 사장님께 전달하고 싶은 말 (선택)
    @Column(length = 500)
    private String message;

    // 반려동물 동반 여부 — true면 반드시 룸(개별 공간)으로만 예약 가능
    @Column(nullable = false)
    private boolean hasPet = false;

    // 나가실 때 포장(테이크아웃) 요청 여부 — 사장님이 미리 준비하실 수 있도록 안내용
    @Column(nullable = false)
    private boolean wantsTakeout = false;

    // 예약과 함께 미리 골라둔 메뉴 (메뉴명 -> 수량, 선택)
    @ElementCollection
    @CollectionTable(name = "reservation_menus", joinColumns = @JoinColumn(name = "reservation_id"))
    @MapKeyColumn(name = "menu_name", length = 100)
    @Column(name = "quantity")
    private Map<String, Integer> menus = new HashMap<>();

    // "나가실 때 포장해서 가시나요?"에 "네"라고 답했을 때, 어떤 메뉴를 포장해 갈지
    // (메뉴명 -> 수량, 선택) — 위 menus(매장에서 먹을 메뉴)와는 별개입니다.
    @ElementCollection
    @CollectionTable(name = "reservation_takeout_menus", joinColumns = @JoinColumn(name = "reservation_id"))
    @MapKeyColumn(name = "menu_name", length = 100)
    @Column(name = "quantity")
    private Map<String, Integer> takeoutMenus = new HashMap<>();

    // CONFIRMED / CANCELLED — 취소해도 기록은 남기되, 같은 자리/시간 재예약은 가능해야 하므로
    // 가용 여부 확인 시에는 항상 CONFIRMED인 예약만 셉니다.
    @Column(nullable = false, length = 20)
    private String status = "CONFIRMED";

    // ── 결제 (모의 결제 — 실제로 돈이 빠지지는 않습니다) ──────────────
    // UNPAID / PAID
    @Column(nullable = false, length = 20)
    private String paymentStatus = "UNPAID";

    // 카카오페이 / 네이버페이 / 신용카드 / 휴대폰 결제 등, 화면에서 고른 결제 수단
    @Column(length = 30)
    private String paymentMethod;

    @Column(nullable = false)
    private int paidAmount = 0;

    private LocalDateTime paidAt;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}