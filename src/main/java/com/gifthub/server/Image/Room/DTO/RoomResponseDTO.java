package com.gifthub.server.Image.Room.DTO;

import lombok.Builder;
import lombok.Data;

@Data
public class RoomResponseDTO {
    private Long room_id;
    private String code;

    @Builder
    public RoomResponseDTO(Long room_id, String code) {
        this.room_id = room_id;
        this.code = code;
    }
}
