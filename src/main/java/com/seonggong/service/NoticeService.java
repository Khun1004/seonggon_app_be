package com.seonggong.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seonggong.dto.NoticeResponse;
import com.seonggong.dto.UpsertNoticeRequest;
import com.seonggong.entity.Notice;
import com.seonggong.repository.NoticeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository repository;

    public List<NoticeResponse> getActiveNotices() {
        return repository.findAllByActiveTrueOrderByDateDesc().stream()
                .map(NoticeResponse::from)
                .toList();
    }

    public List<NoticeResponse> getAllNotices() {
        return repository.findAllByOrderByDateDesc().stream()
                .map(NoticeResponse::from)
                .toList();
    }

    @Transactional
    public NoticeResponse createNotice(UpsertNoticeRequest request) {
        Notice notice = new Notice();
        applyRequest(notice, request);
        return NoticeResponse.from(repository.save(notice));
    }

    @Transactional
    public NoticeResponse updateNotice(Long id, UpsertNoticeRequest request) {
        Notice notice = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("공지사항을 찾을 수 없습니다."));
        applyRequest(notice, request);
        return NoticeResponse.from(notice);
    }

    @Transactional
    public void deleteNotice(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("공지사항을 찾을 수 없습니다.");
        }
        repository.deleteById(id);
    }

    private void applyRequest(Notice notice, UpsertNoticeRequest request) {
        notice.setDate(
                request.getDate() == null || request.getDate().isBlank()
                        ? LocalDate.now()
                        : LocalDate.parse(request.getDate()));
        notice.setTitle(request.getTitle());
        notice.setContent(request.getContent());
        notice.setActive(request.isActive());
    }
}