package com.seonggong.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.seonggong.entity.ReviewGuideStep;

public interface ReviewGuideStepRepository extends JpaRepository<ReviewGuideStep, Long> {
    List<ReviewGuideStep> findAllByOrderByDisplayOrderAsc();

    List<ReviewGuideStep> findAllByActiveTrueOrderByDisplayOrderAsc();
}