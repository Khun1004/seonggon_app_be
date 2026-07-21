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