package com.seonggong.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.seonggong.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByLoginIdOrderByCreatedAtDesc(String loginId);

    long countByLoginIdAndIsReadFalse(String loginId);

    List<Notification> findByLoginIdAndIsReadFalse(String loginId);
}