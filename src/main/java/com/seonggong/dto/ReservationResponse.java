package com.seonggong.dto;

import java.util.Map;

import com.seonggong.entity.Reservation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponse {
        private Long id;
        private String roomId;
        private String roomLabel;
        private String type;
        private String date;
        private String time;
        private String name;
        // 이 예약을 만든 회원의 로그인 아이디 — 손님이 앱에서 직접 예약하면 항상
        // 채워지고, 사장님이 관리자 화면에서 대신 등록해준 예약은 비어있어요.
        // 관리자 화면에서 "회원"/"미등록"을 구분하는 데 씁니다.
        private String loginId;
        private String phone;
        private int peopleCount;
        private String message;
        private boolean hasPet;
        private boolean wantsTakeout;
        private Map<String, Integer> menus;
        private Map<String, Integer> takeoutMenus;
        private String status;
        private String paymentStatus;
        private String paymentMethod;
        private int paidAmount;
        private Long paidAt;
        private long createdAt;

        public static ReservationResponse from(Reservation r) {
                return new ReservationResponse(
                                r.getId(),
                                r.getRoomId(),
                                r.getRoomLabel(),
                                r.getType(),
                                r.getDate().toString(),
                                r.getTime(),
                                r.getName(),
                                r.getLoginId(),
                                r.getPhone(),
                                r.getPeopleCount(),
                                r.getMessage(),
                                r.isHasPet(),
                                r.isWantsTakeout(),
                                r.getMenus(),
                                r.getTakeoutMenus(),
                                r.getStatus(),
                                r.getPaymentStatus(),
                                r.getPaymentMethod(),
                                r.getPaidAmount(),
                                r.getPaidAt() == null
                                                ? null
                                                : r.getPaidAt()
                                                                .atZone(java.time.ZoneId.systemDefault())
                                                                .toInstant()
                                                                .toEpochMilli(),
                                r.getCreatedAt()
                                                .atZone(java.time.ZoneId.systemDefault())
                                                .toInstant()
                                                .toEpochMilli());
        }
}