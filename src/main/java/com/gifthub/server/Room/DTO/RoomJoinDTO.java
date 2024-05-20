package com.gifthub.server.Room.DTO;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoomJoinDTO {

    String code;

    @Builder
    public RoomJoinDTO(String code) {
        this.code = code;
    }
}
