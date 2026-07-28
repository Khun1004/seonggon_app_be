package com.seonggong.service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seonggong.dto.ReservationTimeConfigResponse;
import com.seonggong.dto.UpsertReservationTimeConfigRequest;
import com.seonggong.entity.ReservationTimeConfig;
import com.seonggong.repository.ReservationTimeConfigRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationTimeConfigService {

    private final ReservationTimeConfigRepository repository;

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    @Transactional
    public ReservationTimeConfig getOrCreate(String type) {
        return repository.findById(type).orElseGet(() -> {
            ReservationTimeConfig config = new ReservationTimeConfig();
            config.setType(type);
            return repository.save(config);
        });
    }

    @Transactional
    public ReservationTimeConfigResponse getConfig(String type) {
        return toResponse(getOrCreate(type));
    }

    @Transactional
    public ReservationTimeConfigResponse updateConfig(
            String type, UpsertReservationTimeConfigRequest request) {
        ReservationTimeConfig config = getOrCreate(type);
        config.setStartTime(request.getStartTime());
        config.setEndTime(request.getEndTime());
        config.setIntervalMinutes(request.getIntervalMinutes());
        return toResponse(config);
    }

    private ReservationTimeConfigResponse toResponse(ReservationTimeConfig config) {
        return new ReservationTimeConfigResponse(
                config.getType(),
                config.getStartTime(),
                config.getEndTime(),
                config.getIntervalMinutes(),
                generateSlots(config.getStartTime(), config.getEndTime(), config.getIntervalMinutes()));
    }

    // 시작~종료 시간과 간격(분)만으로 실제 시간 목록을 계산해줍니다.
    // 예: 11:00~13:00, 30분 간격 → ["11:00", "11:30", "12:00", "12:30", "13:00"]
    private List<String> generateSlots(String startTime, String endTime, int intervalMinutes) {
        List<String> slots = new ArrayList<>();
        if (intervalMinutes <= 0)
            return slots;

        LocalTime cur = LocalTime.parse(startTime, FORMAT);
        LocalTime end = LocalTime.parse(endTime, FORMAT);

        while (!cur.isAfter(end)) {
            slots.add(cur.format(FORMAT));
            cur = cur.plusMinutes(intervalMinutes);
        }
        return slots;
    }
}