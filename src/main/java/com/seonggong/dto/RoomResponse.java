package com.seonggong.dto;

import com.seonggong.entity.Room;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoomResponse {
    private Long id;
    private String roomKey;
    private String number;
    private int floor;
    private String category;
    private String categoryLabel;
    private String capacity;
    private boolean isRoom;
    private String note;
    private String imageUrl;
    private int displayOrder;
    private boolean active;

    public static RoomResponse from(Room r) {
        return new RoomResponse(
                r.getId(),
                r.getRoomKey(),
                r.getNumber(),
                r.getFloor(),
                r.getCategory(),
                r.getCategoryLabel(),
                r.getCapacity(),
                r.isRoom(),
                r.getNote(),
                r.getImageUrl(),
                r.getDisplayOrder(),
                r.isActive());
    }
}