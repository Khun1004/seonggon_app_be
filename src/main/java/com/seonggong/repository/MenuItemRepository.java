package com.seonggong.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.seonggong.entity.MenuItem;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    List<MenuItem> findByActiveTrueOrderByCategoryAscDisplayOrderAsc();

    // 관리자 화면은 숨겨진 메뉴도 같이 봐야 관리할 수 있어서 active 상관없이 전체 조회
    List<MenuItem> findAllByOrderByCategoryAscDisplayOrderAsc();
}