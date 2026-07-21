package com.seonggong.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.seonggong.entity.NearbySpot;

public interface NearbySpotRepository extends JpaRepository<NearbySpot, Long> {

    List<NearbySpot> findAllByOrderBySortOrderAsc();
}