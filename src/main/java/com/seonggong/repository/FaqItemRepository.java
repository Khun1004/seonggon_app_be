package com.seonggong.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.seonggong.entity.FaqItem;

public interface FaqItemRepository extends JpaRepository<FaqItem, Long> {
    List<FaqItem> findAllByOrderByDisplayOrderAsc();

    List<FaqItem> findAllByActiveTrueOrderByDisplayOrderAsc();
}