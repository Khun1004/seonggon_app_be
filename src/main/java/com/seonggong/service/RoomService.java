package com.seonggong.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seonggong.dto.RoomResponse;
import com.seonggong.dto.UpsertRoomRequest;
import com.seonggong.entity.Room;
import com.seonggong.repository.RoomRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    // 손님용 — 숨겨지지 않은 좌석만 층·카테고리·순서대로
    public List<RoomResponse> getActiveRooms() {
        return roomRepository.findByActiveTrueOrderByFloorAscCategoryAscDisplayOrderAsc()
                .stream()
                .map(RoomResponse::from)
                .collect(Collectors.toList());
    }

    // 관리자용 — 숨겨진 좌석도 포함해서 전체
    public List<RoomResponse> getAllRoomsForAdmin() {
        return roomRepository.findAllByOrderByFloorAscCategoryAscDisplayOrderAsc()
                .stream()
                .map(RoomResponse::from)
                .collect(Collectors.toList());
    }

    // 새 좌석을 추가할 때는 roomKey를 자동으로 만들어줍니다 (카테고리-번호 조합).
    // 이미 같은 키가 있으면(예: 같은 카테고리에 같은 번호) 뒤에 숫자를 붙여 겹치지 않게 합니다.
    @Transactional
    public RoomResponse createRoom(UpsertRoomRequest request) {
        Room room = new Room();
        applyRequest(room, request);

        // 람다 안에서 계속 바뀌는 변수를 직접 참조할 수 없어서(effectively final 규칙),
        // 기존 roomKey들을 먼저 Set으로 뽑아두고 그걸로 중복 검사를 합니다.
        java.util.Set<String> existingKeys = roomRepository
                .findAllByOrderByFloorAscCategoryAscDisplayOrderAsc()
                .stream()
                .map(Room::getRoomKey)
                .collect(java.util.stream.Collectors.toSet());

        String baseKey = request.getCategory() + "-" + request.getNumber();
        String key = baseKey;
        int suffix = 1;
        while (existingKeys.contains(key)) {
            suffix++;
            key = baseKey + "-" + suffix;
        }
        room.setRoomKey(key);

        return RoomResponse.from(roomRepository.save(room));
    }

    @Transactional
    public RoomResponse updateRoom(Long id, UpsertRoomRequest request) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("좌석을 찾을 수 없습니다."));
        applyRequest(room, request);
        return RoomResponse.from(room);
    }

    // 실제로 지우면 예전 예약에서 참조하던 좌석 키가 깨질 수 있어서,
    // 삭제 대신 active=false로 숨김 처리합니다.
    @Transactional
    public void hideRoom(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("좌석을 찾을 수 없습니다."));
        room.setActive(false);
    }

    @Transactional
    public void restoreRoom(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("좌석을 찾을 수 없습니다."));
        room.setActive(true);
    }

    private void applyRequest(Room room, UpsertRoomRequest request) {
        room.setNumber(request.getNumber());
        room.setFloor(request.getFloor());
        room.setCategory(request.getCategory());
        room.setCategoryLabel(request.getCategoryLabel());
        room.setCapacity(request.getCapacity());
        room.setRoom(request.isRoom());
        room.setNote(request.getNote());
        if (request.getImageUrl() != null) {
            room.setImageUrl(request.getImageUrl());
        }
        room.setDisplayOrder(request.getDisplayOrder());
        room.setActive(request.isActive());
    }

    // 메뉴/리뷰 사진과 똑같은 방식 — base64로 받아서 서버 파일로 저장하고 경로를 돌려줍니다.
    public String uploadPhoto(String imageBase64) {
        try {
            String pureBase64 = imageBase64.contains(",")
                    ? imageBase64.substring(imageBase64.indexOf(",") + 1)
                    : imageBase64;
            byte[] imageBytes = Base64.getDecoder().decode(pureBase64);

            Path dir = Paths.get("uploads", "rooms");
            Files.createDirectories(dir);

            String fileName = UUID.randomUUID() + ".jpg";
            Path filePath = dir.resolve(fileName);
            Files.write(filePath, imageBytes);

            return "/uploads/rooms/" + fileName;
        } catch (IOException e) {
            throw new IllegalArgumentException("사진 저장에 실패했습니다.");
        }
    }
}