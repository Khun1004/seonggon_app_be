package com.seonggong.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.seonggong.entity.Room;
import com.seonggong.repository.RoomRepository;

import lombok.RequiredArgsConstructor;

// 서버가 켜질 때, rooms 테이블이 비어있으면 지금까지 앱 코드에 고정값으로
// 있던 좌석/룸들을 그대로 옮겨 넣습니다. 이미 데이터가 있으면(사장님이 앱에서
// 직접 수정/추가했으면) 아무것도 하지 않습니다.
// roomKey는 예약(Reservation.roomId)이 참조하는 값과 정확히 같아야 해서,
// 기존 constants/rooms-data.ts에 있던 id 값을 그대로 옮겼습니다.
// 사진(imageUrl)은 비워뒀어요 — 이 좌석들은 원래 앱 안에 내장된 사진을 쓰고
// 있었어서, 서버에 올려진 실제 파일이 없기 때문입니다. 관리자 화면에서 사진을
// 새로 업로드하면 그때부터 그 사진이 우선으로 보여요.
@Component
@RequiredArgsConstructor
public class RoomSeeder implements CommandLineRunner {

    private final RoomRepository roomRepository;

    @Override
    public void run(String... args) {
        if (roomRepository.count() > 0) {
            return;
        }

        // 1층 — 일반 홀 좌석 1~6번
        save("hall-1", "1", 1, "hall", "일반 홀 좌석", "4명 ~ 8명", false, 1);
        save("hall-2", "2", 1, "hall", "일반 홀 좌석", "4명 ~ 6명", false, 2);
        save("hall-3", "3", 1, "hall", "일반 홀 좌석", "4명 ~ 6명", false, 3);
        save("hall-4", "4", 1, "hall", "일반 홀 좌석", "4명 ~ 6명", false, 4);
        save("hall-5", "5", 1, "hall", "일반 홀 좌석", "2명 ~ 4명", false, 5);
        save("hall-6", "6", 1, "hall", "일반 홀 좌석", "2명 ~ 4명", false, 6);

        // 1층 — 프라이빗 룸 (소형)
        save("small-15", "15", 1, "small", "프라이빗 룸 (소형)", "2명 ~ 4명", true, 1);
        save("small-16", "16", 1, "small", "프라이빗 룸 (소형)", "2명 ~ 4명", true, 2);

        // 1층 — 프라이빗 룸 (중형)
        save("medium-13", "13", 1, "medium", "프라이빗 룸 (중형)", "4명 ~ 8명", true, 1);
        save("medium-14", "14", 1, "medium", "프라이빗 룸 (중형)", "4명 ~ 8명", true, 2);

        // 1층 — 프라이빗 룸 (대형)
        save("large-7", "7", 1, "large", "프라이빗 룸 (대형)", "4명 ~ 10명", true, 1);

        // 2층 — 단체 룸 (소형)
        save("2f-1", "1", 2, "group_room", "단체 룸 (소형)", "4명 ~ 8명", true, 1);
        save("2f-2", "2", 2, "group_room", "단체 룸 (소형)", "4명 ~ 8명", true, 2);

        // 2층 — 단체 룸 (대형)
        save("2f-5", "5", 2, "group_large", "단체 룸 (대형)", "4명 ~ 16명", true, 1);

        // 2층 — 대형 홀 (단체석)
        save("2f-6", "6", 2, "group_hall", "대형 홀 (단체석)", "4명 ~ 80명", false, 1);
    }

    private void save(
            String roomKey, String number, int floor, String category,
            String categoryLabel, String capacity, boolean isRoom, int order) {
        Room room = new Room();
        room.setRoomKey(roomKey);
        room.setNumber(number);
        room.setFloor(floor);
        room.setCategory(category);
        room.setCategoryLabel(categoryLabel);
        room.setCapacity(capacity);
        room.setRoom(isRoom);
        room.setDisplayOrder(order);
        room.setActive(true);
        roomRepository.save(room);
    }
}