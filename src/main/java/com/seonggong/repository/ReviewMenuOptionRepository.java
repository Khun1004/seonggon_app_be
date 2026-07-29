package com.seonggong.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.seonggong.entity.ReviewMenuOption;

public interface ReviewMenuOptionRepository extends JpaRepository<ReviewMenuOption, Long> {
    List<ReviewMenuOption> findAllByOrderByDisplayOrderAsc();

    List<ReviewMenuOption> findAllByActiveTrueOrderByDisplayOrderAsc();
}