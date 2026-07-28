package com.seonggong.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.seonggong.entity.ClosedDate;

public interface ClosedDateRepository extends JpaRepository<ClosedDate, Long> {

    List<ClosedDate> findAllByOrderByDateAsc();

    List<ClosedDate> findByDateGreaterThanEqualOrderByDateAsc(LocalDate from);

    boolean existsByDate(LocalDate date);
}