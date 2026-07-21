package com.seonggong.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.seonggong.dto.NotificationResponse;
import com.seonggong.entity.Notification;
import com.seonggong.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    // 예약 완료, 결제 완료, 회원가입 환영 등 — 실제 이벤트가 발생하는 곳(다른
    // 서비스들)에서 이 메서드 하나만 호출하면 알림이 만들어집니다.
    public void create(String loginId, String type, String title, String message, String route) {
        if (loginId == null || loginId.isBlank())
            return; // 로그인 안 된 흐름(전화 예약 등)은 알림 대상이 없음

        Notification notification = new Notification();
        notification.setLoginId(loginId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setRoute(route);
        notificationRepository.save(notification);
    }

    public List<NotificationResponse> getMyNotifications(String loginId) {
        return notificationRepository.findByLoginIdOrderByCreatedAtDesc(loginId)
                .stream()
                .map(NotificationResponse::from)
                .collect(Collectors.toList());
    }

    public long getUnreadCount(String loginId) {
        return notificationRepository.countByLoginIdAndIsReadFalse(loginId);
    }

    public void markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("알림을 찾을 수 없습니다."));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public void markAllAsRead(String loginId) {
        List<Notification> unread = notificationRepository.findByLoginIdAndIsReadFalse(loginId);
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }
}