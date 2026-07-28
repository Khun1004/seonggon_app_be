package com.seonggong.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.seonggong.entity.ReservationTimeConfig;

public interface ReservationTimeConfigRepository
        extends JpaRepository<ReservationTimeConfig, String> {
}