package com.gifthub.server.Image.Room.DTO;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoomJoinDTO {

    String code;
    String title;

    @Builder
    public RoomJoinDTO(String code, String title) {
        this.code = code;
        this.title = title;
    }
}
