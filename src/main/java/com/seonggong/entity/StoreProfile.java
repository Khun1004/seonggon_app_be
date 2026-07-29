package com.seonggong.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

// 가게 기본 정보 — 주소, 전화번호, 영업시간. 딱 한 줄(id=1)만 있는
// "설정값" 성격의 테이블이에요. 사장님이 관리자 화면에서 이 값을 바꾸면
// 손님 화면(성공식당의 상세)에 그대로 반영됩니다.
@Entity
@Table(name = "store_profile")
@Getter
@Setter
public class StoreProfile {

    @Id
    private Long id = 1L;

    @Column(nullable = false, length = 200)
    private String address = "대구 동구 팔공산로199길 12";

    @Column(nullable = false, length = 30)
    private String phone = "0507-1410-7634";

    // "11:00" 같은 형식
    @Column(nullable = false, length = 10)
    private String openTime = "11:00";

    @Column(nullable = false, length = 10)
    private String closeTime = "21:00";

    @Column(nullable = false, length = 10)
    private String lastOrderTime = "19:30";

    // 네이버 플레이스 평점/리뷰수 — 네이버가 공식 API를 안 줘서, 사장님이
    // 직접 입력해서 손님 화면에 참고용으로 보여주는 값이에요. 자동 갱신은 안 돼요.
    private Double naverRating;

    private Integer naverReviewCount;

    // 블로그 리뷰 개수도 마찬가지로 사장님이 직접 입력하는 참고용 숫자예요.
    private Integer blogReviewCount;

    // 기본은 false — 토/일요일은 손님 예약 화면에서 회색 처리(전화 예약만)돼요.
    // 사장님이 이 값을 true로 켜면, 주말도 앱에서 바로 예약할 수 있게 돼요.
    @Column(nullable = false)
    private boolean allowWeekendReservations = false;
}