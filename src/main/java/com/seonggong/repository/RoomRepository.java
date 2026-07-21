package com.seonggong.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.seonggong.entity.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByActiveTrueOrderByFloorAscCategoryAscDisplayOrderAsc();

    // 관리자 화면은 숨겨진 좌석도 같이 봐야 관리할 수 있어서 active 상관없이 전체 조회
    List<Room> findAllByOrderByFloorAscCategoryAscDisplayOrderAsc();
}