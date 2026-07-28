package com.seonggong.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.seonggong.entity.IngredientSet;

public interface IngredientSetRepository extends JpaRepository<IngredientSet, Long> {

    List<IngredientSet> findAllByOrderByNameAsc();
}