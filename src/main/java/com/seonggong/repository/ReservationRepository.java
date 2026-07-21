package com.seonggong.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.seonggong.entity.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

        List<Reservation> findByDateAndStatus(LocalDate date, String status);

        List<Reservation> findByPhoneOrderByCreatedAtDesc(String phone);

        // 관리자 화면에서 전체 예약을 최신 날짜/시간 순으로 봅니다.
        List<Reservation> findAllByOrderByDateDescTimeDesc();

        boolean existsByRoomIdAndDateAndTimeAndStatus(
                        String roomId, LocalDate date, String time, String status);

        boolean existsByRoomIdAndDateAndTimeAndStatusAndIdNot(
                        String roomId, LocalDate date, String time, String status, Long id);
}