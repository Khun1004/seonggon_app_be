package com.seonggong.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seonggong.dto.CouponNoticeResponse;
import com.seonggong.dto.UpsertCouponNoticeRequest;
import com.seonggong.entity.CouponNotice;
import com.seonggong.repository.CouponNoticeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CouponNoticeService {

    private final CouponNoticeRepository repository;

    @Transactional
    public CouponNoticeResponse getNotice() {
        CouponNotice notice = repository.findById(1L).orElseGet(() -> repository.save(new CouponNotice()));
        return new CouponNoticeResponse(notice.getContent());
    }

    @Transactional
    public CouponNoticeResponse updateNotice(UpsertCouponNoticeRequest request) {
        CouponNotice notice = repository.findById(1L).orElseGet(CouponNotice::new);
        notice.setContent(request.getContent());
        repository.save(notice);
        return new CouponNoticeResponse(notice.getContent());
    }
}