package com.seonggong.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.seonggong.entity.RewardRedeemableItem;

public interface RewardRedeemableItemRepository extends JpaRepository<RewardRedeemableItem, Long> {
    List<RewardRedeemableItem> findAllByOrderByDisplayOrderAsc();

    List<RewardRedeemableItem> findAllByActiveTrueOrderByDisplayOrderAsc();
}