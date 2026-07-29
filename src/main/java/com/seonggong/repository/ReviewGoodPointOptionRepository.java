package com.seonggong.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.seonggong.entity.ReviewGoodPointOption;

public interface ReviewGoodPointOptionRepository extends JpaRepository<ReviewGoodPointOption, Long> {
    List<ReviewGoodPointOption> findAllByOrderByDisplayOrderAsc();

    List<ReviewGoodPointOption> findAllByActiveTrueOrderByDisplayOrderAsc();
}