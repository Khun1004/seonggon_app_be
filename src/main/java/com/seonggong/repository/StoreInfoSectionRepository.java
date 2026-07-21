package com.seonggong.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.seonggong.entity.StoreInfoSection;

public interface StoreInfoSectionRepository extends JpaRepository<StoreInfoSection, Long> {

    List<StoreInfoSection> findByActiveTrueOrderByGroupAscDisplayOrderAsc();

    List<StoreInfoSection> findAllByOrderByGroupAscDisplayOrderAsc();
}