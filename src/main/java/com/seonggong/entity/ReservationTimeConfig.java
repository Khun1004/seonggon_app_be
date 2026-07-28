package com.seonggong.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

// 예약 가능 시간 설정 — "매장 식사(DINE_IN)"와 "포장(TAKEOUT)"을 따로
// 관리해요. 시작~종료 시간과 간격만 저장해두고, 실제 시간표(11:00, 11:30...)는
// 이 값으로 그때그때 계산해서 만들어줍니다.
@Entity
@Table(name = "reservation_time_configs")
@Getter
@Setter
public class ReservationTimeConfig {

    // "DINE_IN" 또는 "TAKEOUT" — 타입별로 딱 한 줄씩만 있어요.
    @Id
    @Column(length = 20)
    private String type;

    @Column(nullable = false, length = 10)
    private String startTime = "11:00";

    @Column(nullable = false, length = 10)
    private String endTime = "20:00";

    @Column(nullable = false)
    private int intervalMinutes = 30;
}